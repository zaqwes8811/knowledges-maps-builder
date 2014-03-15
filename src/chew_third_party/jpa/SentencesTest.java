package chew_third_party.jpa;

import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.List;

public class SentencesTest {
  private static final String PERSISTENCE_UNIT_NAME = "todos";
  private static EntityManagerFactory factory;

  @Test
  public void testReadAll() {
    factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    EntityManager em = factory.createEntityManager();

    Query q = em.createQuery("select t from Sentences t");
    List<Todo> todoList = q.getResultList();
    for (Todo todo : todoList) {
      System.out.println(todo);
    }
    System.out.println("Size: " + todoList.size());

    em.close();
  }
}
