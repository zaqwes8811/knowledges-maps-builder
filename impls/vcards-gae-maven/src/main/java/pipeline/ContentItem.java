package pipeline;

public class ContentItem {
  public ContentItem(String sentence) {
    this.sentence = sentence;
  }

  // value <= 500 symbols // TODO: 500 чего именно?
  String sentence;

  private Integer countWords;

  public void setCountWords(Integer value) {
    countWords = value;
  }

  public Integer getCountWords() {
    return countWords;
  }

  public String getSentence() {
    return sentence;
  }
}
