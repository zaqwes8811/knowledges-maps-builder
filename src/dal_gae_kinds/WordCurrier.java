package dal_gae_kinds;

// TODO: Генераторы разные, и данные по слову рассматриваются через генератор.
//@Immutable
public class WordCurrier {
  public final String word;

  // Как быть с рангом?
  //public final Integer rawFrequency;  // Тут не ясно пока

  public WordCurrier(String word) {
    this.word = word;
  }
}
