package backend;

import java.util.ArrayList;

// http://www.artima.com/weblogs/viewpost.jsp?thread=118828
public class ValuePipeline {
  public final String PAGE_NAME;
  public final String SOURCE;
  public final ArrayList<ContentItem> SENTENCES_KINDS;
  public final ArrayList<UniGram> UNIGRAMS;

  ValuePipeline(String pageName, String source,
                ArrayList<ContentItem> kinds,
                ArrayList<UniGram> unigrams) {
    PAGE_NAME = pageName;
    SOURCE = source;
    SENTENCES_KINDS = kinds;
    UNIGRAMS = unigrams;
  }
}
