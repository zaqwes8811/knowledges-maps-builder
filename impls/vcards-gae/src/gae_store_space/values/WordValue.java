package gae_store_space.values;

import gae_store_space.SentenceKind;

import java.util.Collection;

public class WordValue {
  public final Integer frequency;
  public final String word;
  public final Collection<SentenceKind> sentences;

  public WordValue(String word, Integer frequency, Collection<SentenceKind> c) {
    this.word = word;
    this.frequency = frequency;
    this.sentences = c;
  }
}