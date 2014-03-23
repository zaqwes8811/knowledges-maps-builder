package dal.jpa.eclipselink;

/*

Born of cold and Winter air
And mountain rain combining,
This icy force
both foul and fair
Has a frozen heart worth mining.

*/

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.HashSet;
import java.util.Set;

public class WordsMediumTest {
  private static final String PERSISTENCE_UNIT_NAME = "words_test";
  private EntityManagerFactory factory;

  @Before
  public void setUp() throws Exception {
    factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    EntityManager em = factory.createEntityManager();

    // Begin a new local transaction so that we can persist a new entity
    em.getTransaction().begin();

    // read the existing entries
    Query q = em.createQuery("select m from Sentences m");
    // Persons should be empty

    // do we have entries?
    assertTrue(q.getResultList().size() == 0);
    Words word = new Words();
    word.setValue("heart");
    em.persist(word);

    Sentences sentence = new Sentences();
    sentence.setSentence("both foul and fair");
    word.getSentences().add(sentence);
    em.persist(sentence);
    em.persist(word);


    // Commit the transaction, which will cause the entity to
    // be stored in the database
    em.getTransaction().commit();

    // It is always good practice to close the EntityManager so that
    // resources are conserved.
    em.close();
  }

  @After
  public void tearDown() {
    // TODO: Очистить базу данных
  }

  @Test
  public void testCreate() {

  }

  @Test
  public void testRecreate() {

  }
}
