
package ch.astorm.ejb;

import ch.astorm.api.SimpleBeanRemote;
import ch.astorm.ejb.entities.Leaf;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.File;
import java.nio.file.Paths;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

@LocalBean
@Stateless
@Remote(SimpleBeanRemote.class)
public class SimpleBean {

    @PersistenceContext(unitName="sample-ejbPU")
    private EntityManager em;
    
    @PostConstruct
    public void initLucene() {
        File indexRoot = new File("lucene");
        IndexWriterConfig conf = new IndexWriterConfig(new EnglishAnalyzer());
        conf.setOpenMode(OpenMode.CREATE);
        try(Directory directory = FSDirectory.open(Paths.get(indexRoot.toURI()));
            IndexWriter directoryWriter = new IndexWriter(directory, conf)) {
        } catch(Exception e) {
            throw new RuntimeException("Unable to initialize Lucene", e);
        }
    }
    
    public int create() {
        for(int i=0 ; i<10 ; ++i) {
            Leaf leaf = new Leaf();
            leaf.setId((long)i);
            leaf.setName("Leaf "+(i+1));
            em.persist(leaf);
        }
        return 10;
    }
    
    public String getLeaf(long id) {
        return em.createQuery("SELECT l.name FROM Leaf l WHERE l.id=?1", String.class).
            setParameter(1, id).
            getSingleResult();
    }
}
