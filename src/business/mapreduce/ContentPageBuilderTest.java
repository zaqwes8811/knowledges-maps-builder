package business.mapreduce;

import org.junit.Test;

/**
 * Created by zaqwes on 10/9/14.
 */
public class ContentPageBuilderTest {
  private String getPlainText() {
    return
    "Born of cold and Winter air And mountain rain combining, This icy force" +
    "both foul and fair Has a frozen heart worth mining. Cut through the heart, Cold and Clear. Strike for love And" +
    "Strike for fear. See the beauty Sharp and Sheer.  Split the ice apart" +
    "And break the frozen heart. Hup! Ho! Watch your step! Let it go! Hup! Ho! " +
      "Watch your step! Let it go! Beautiful! Powerful! Dangerous! Cold!";
  }

  @Test
  public void base() {
    // get plain text
    String plainText = getPlainText();
  }
}
