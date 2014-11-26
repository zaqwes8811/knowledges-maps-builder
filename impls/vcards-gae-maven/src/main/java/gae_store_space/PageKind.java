// Tasks:
//   Сперва подключить кеш,
//   Затем думать о распределении
//   Затем думать об удалении.

// http://www.oracle.com/technetwork/java/javase/documentation/index-137868.html#styleguide
// TODO: http://www.oracle.com/technetwork/articles/marx-jpa-087268.html
// TODO: скрыть персистентность в этом классе, пусть сам себя сохраняет и удаляет.
// TODO: Функция очистки данных связанных со страницей, себя не удаляет.
// TODO: Добавить оценки текста
// не хочется выносить ofy()... выше. Но может быть, если использовать класс пользователя, то он может.
/**
 * About:
 *   Отражает один элемент данный пользователя, например, один файл субтитров.
 */

package gae_store_space;


import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import net.jcip.annotations.NotThreadSafe;
import org.apache.commons.collections4.Predicate;

import java.util.ArrayList;

class Tmp implements Predicate<NGramKind> {
	@Override
	public boolean evaluate(NGramKind o) {
		return o.getNGram().equals(ngram);
	}

	String ngram;
	public Tmp(String value) {
		ngram = value;
	}
}

@NotThreadSafe
@Entity
public class PageKind {
	// State
  private PageKind() { }
  public @Id Long id;  // FIXME: make as string - UserId.pageName
	// FIXME: можно еще доступ сереализовать

  @Index String name;
  String rawSource;  // для обновленной версии

  // Теперь страница полностью управляет временем жизни
  // удобен разве что для запроса
  // Можно загружать по нему при загрузке страницы, а потом пользоваться кешем
  @Load  
  //private
	Key<GeneratorKind> generator;
	//private
	Integer boundaryPtr = PageFrontend.STEP_WINDOW_SIZE;  // указатель на текущyю границу
	//private
	Integer referenceVolume = 0;
	// Assumption: raw source >> sum(other fields)
	public long getPageByteSize() {
		// it's trouble
		// http://stackoverflow.com/questions/9368764/calculate-size-of-object-in-java
		// http://stackoverflow.com/questions/52353/in-java-what-is-the-best-way-to-determine-the-size-of-an-object
		//
		return rawSource.length();
	}

	public Long getId() { return id; }

	public String getName() {
		return name;
	}

	// Пришлось раскрыть
	public void setGenerator(GeneratorKind gen) {
		generator = Key.create(gen);
	}

	public PageKind(
		String name, ArrayList<SentenceKind> items, ArrayList<NGramKind> words, String rawSource)
	{
		this.name = name;
		this.rawSource = rawSource;
	}
}
