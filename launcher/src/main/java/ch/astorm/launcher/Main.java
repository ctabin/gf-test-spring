
package ch.astorm.launcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.glassfish.embeddable.BootstrapProperties;
import org.glassfish.embeddable.Deployer;
import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;

public class Main {
    public static void main(String[] args) throws Exception {
        //mapped in the pom.xml ear by the variable ${ch.astorm.root} and
        //defined in the embedded domain.xml as system property
        String dynRoot = "root";

        File rootDir = new File("test-gf");
        if(rootDir.exists()) { FileUtils.cleanDirectory(rootDir); }
        
        boolean inMaven = false;
        File parent = new File("ear");
        if(!parent.exists()) {
            parent = new File("../ear");
            inMaven = true;
        }
        
        File ear = new File(parent, "target/sample-gf-app.ear");
        if(!ear.exists()) { throw new IllegalStateException("File does not exists: "+ear.getAbsolutePath()); }
        
        String domainXML;
        try(InputStream is = Main.class.getResourceAsStream("domain.xml")) {
            domainXML = IOUtils.toString(is, StandardCharsets.UTF_8);
        }
        
        File dbDir = new File(rootDir, "database");
        dbDir.mkdirs();
        
        boolean isGlassFish = System.getProperty("appserver.artifactId", ".").equals("glassfish-embedded-all");
        
        File db = new File(dbDir, "db.data");
        domainXML = domainXML.replace("${DATABASE_FILE}", db.getAbsolutePath());
        domainXML = domainXML.replace("${KEYSTORE}", "keystore."+(isGlassFish ? "jks" : "p12"));
        
        File domainFile = new File(rootDir, "domain.xml");
        try(FileOutputStream fos = new FileOutputStream(domainFile)) {
            IOUtils.write(domainXML, fos, StandardCharsets.UTF_8);
        }
        
        //special property needed with the JDK17
        //see https://github.com/eclipse-ee4j/orb-gmbal/issues/22#issuecomment-882293428
        System.setProperty("org.glassfish.gmbal.no.multipleUpperBoundsException", "true");
        
        GlassFishProperties gfprops = new GlassFishProperties();
        gfprops.setConfigFileURI(domainFile.toURI().toString());
        
        //https://glassfish.org/docs/SNAPSHOT/embedded-server-guide.html#instance-root-directory-2
        File tempRoot = new File(rootDir, "glassfish");
        if(tempRoot.exists()) { FileUtils.deleteQuietly(tempRoot); }
        gfprops.setProperty("glassfish.embedded.tmpdir", tempRoot.getAbsolutePath());
        
        BootstrapProperties bsprops = new BootstrapProperties();
        GlassFishRuntime runtime = GlassFishRuntime.bootstrap(bsprops);
        GlassFish glassfish = runtime.newGlassFish(gfprops);
        glassfish.start();
        
        //String installRoot = System.getProperty("com.sun.aas.installRoot");

        Deployer deployer = glassfish.getDeployer();
        String earDepName = deployer.deploy(ear);
        if(earDepName==null) { throw new IllegalStateException("EAR deployment has failed"); }
        
        try(CloseableHttpClient client = HttpClientBuilder.create().build()) {
            try(CloseableHttpResponse response = client.execute(new HttpGet("http://localhost:8080/"+dynRoot))) {
                if(response.getStatusLine().getStatusCode()!=200) { throw new IllegalStateException(response.getStatusLine().getStatusCode()+": "+response.getStatusLine().getReasonPhrase()); }

                String responseStr;
                try(InputStream is = response.getEntity().getContent()) {
                    responseStr = IOUtils.toString(is, StandardCharsets.UTF_8);
                    System.out.println("### [CREATE] Response: "+responseStr);
                }
            }
            
            try(CloseableHttpResponse response = client.execute(new HttpGet("http://localhost:8080/"+dynRoot+"?query=3"))) {
                if(response.getStatusLine().getStatusCode()!=200) { throw new IllegalStateException(response.getStatusLine().getStatusCode()+": "+response.getStatusLine().getReasonPhrase()); }

                String responseStr;
                try(InputStream is = response.getEntity().getContent()) {
                    responseStr = IOUtils.toString(is, StandardCharsets.UTF_8);
                    System.out.println("### [QUERY][WAR] Response: "+responseStr);
                }
            }
        }
        
        deployer.undeploy(earDepName);
        
        glassfish.stop();
        runtime.shutdown();
        
        FileUtils.deleteQuietly(rootDir);
        
        if(!inMaven) {
            Runtime.getRuntime().halt(0);
        }
    }
}
