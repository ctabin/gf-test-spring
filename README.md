
# Payara behind Spring Boot

This project contains a simple EAR composed of an EJB and a WAR.
The EAR includes the library Lucene, which contains a Multi-Release JAR since JDK19.

## Requirements

- Java Development Kit 21
- Apache Maven 3.9 or later

## How to test

By default, the project uses Payara 6.2024.1. Ensure you are using the JDK21 by checking `mvn --version`.

Simply use the command `mvn clean package` and the tests will work correclty (because executed through maven).

Once compiled, run the command `java -jar launcher/target/launcher-1.0.0-SNAPSHOT-spring-boot.jar` and the error below will occur:

```
java.lang.RuntimeException
        at org.glassfish.javaee.core.deployment.JavaEEDeployer.prepare(JavaEEDeployer.java:228)
        at org.glassfish.ejb.startup.EjbDeployer.prepare(EjbDeployer.java:232)
        at com.sun.enterprise.v3.server.ApplicationLifecycle.prepareModule(ApplicationLifecycle.java:1255)
        at org.glassfish.javaee.full.deployment.EarDeployer.prepareBundle(EarDeployer.java:301)
        at org.glassfish.javaee.full.deployment.EarDeployer.lambda$prepare$0(EarDeployer.java:164)
        at org.glassfish.javaee.full.deployment.EarDeployer.doOnBundles(EarDeployer.java:221)
        at org.glassfish.javaee.full.deployment.EarDeployer.doOnAllTypedBundles(EarDeployer.java:233)
        at org.glassfish.javaee.full.deployment.EarDeployer.doOnAllBundles(EarDeployer.java:259)
        at org.glassfish.javaee.full.deployment.EarDeployer.prepare(EarDeployer.java:162)
        at com.sun.enterprise.v3.server.ApplicationLifecycle.prepareModule(ApplicationLifecycle.java:1255)
        at com.sun.enterprise.v3.server.ApplicationLifecycle.prepare(ApplicationLifecycle.java:514)
        at org.glassfish.deployment.admin.DeployCommand.execute(DeployCommand.java:570)
        at com.sun.enterprise.v3.admin.CommandRunnerImpl$2$1.run(CommandRunnerImpl.java:556)
        at com.sun.enterprise.v3.admin.CommandRunnerImpl$2$1.run(CommandRunnerImpl.java:552)
        at java.base/java.security.AccessController.doPrivileged(AccessController.java:400)
        at java.base/javax.security.auth.Subject.doAs(Subject.java:453)
        at com.sun.enterprise.v3.admin.CommandRunnerImpl$2.execute(CommandRunnerImpl.java:551)
        at com.sun.enterprise.v3.admin.CommandRunnerImpl$3.run(CommandRunnerImpl.java:582)
        at com.sun.enterprise.v3.admin.CommandRunnerImpl$3.run(CommandRunnerImpl.java:574)
        at java.base/java.security.AccessController.doPrivileged(AccessController.java:400)
        at java.base/javax.security.auth.Subject.doAs(Subject.java:453)
        at com.sun.enterprise.v3.admin.CommandRunnerImpl.doCommand(CommandRunnerImpl.java:573)
        at com.sun.enterprise.v3.admin.CommandRunnerImpl.doCommand(CommandRunnerImpl.java:1497)
        at com.sun.enterprise.v3.admin.CommandRunnerImpl$ExecutionContext.execute(CommandRunnerImpl.java:1879)
        at com.sun.enterprise.v3.admin.CommandRunnerImpl$ExecutionContext.execute(CommandRunnerImpl.java:1755)
        at com.sun.enterprise.admin.cli.embeddable.DeployerImpl.deploy(DeployerImpl.java:131)
        at ch.astorm.launcher.Main.main(Main.java:78)
        at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103)
        at java.base/java.lang.reflect.Method.invoke(Method.java:580)
        at org.springframework.boot.loader.MainMethodRunner.run(MainMethodRunner.java:49)
        at org.springframework.boot.loader.Launcher.launch(Launcher.java:95)
        at org.springframework.boot.loader.Launcher.launch(Launcher.java:58)
        at org.springframework.boot.loader.JarLauncher.main(JarLauncher.java:65)
Caused by: java.nio.file.FileSystemNotFoundException
        at jdk.zipfs/jdk.nio.zipfs.ZipFileSystemProvider.getFileSystem(ZipFileSystemProvider.java:156)
        at jdk.zipfs/jdk.nio.zipfs.ZipFileSystemProvider.getPath(ZipFileSystemProvider.java:142)
        at java.base/java.nio.file.Path.of(Path.java:209)
        at java.base/java.nio.file.Paths.get(Paths.java:98)
        at org.glassfish.loader.util.ASClassLoaderUtil.getModulesClasspath(ASClassLoaderUtil.java:275)
        at org.glassfish.loader.util.ASClassLoaderUtil.getModuleClassPath(ASClassLoaderUtil.java:120)
        at org.glassfish.loader.util.ASClassLoaderUtil.getModuleClassPath(ASClassLoaderUtil.java:146)
        at org.glassfish.javaee.core.deployment.JavaEEDeployer.getModuleClassPath(JavaEEDeployer.java:116)
        at org.glassfish.ejb.startup.EjbDeployer.generateArtifacts(EjbDeployer.java:427)
```

**Note:** This seems to be resolved with Spring Boot 3.2.2. Hence by executing `mvn clean package -Dmaven.test.skip -Dspring_boot.version=3.2.2`, the command above works correctly.
