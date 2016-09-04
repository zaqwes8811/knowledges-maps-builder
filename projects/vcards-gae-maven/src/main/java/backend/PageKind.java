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

package backend;


import com.google.common.base.Optional;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import net.jcip.annotations.NotThreadSafe;
import http_related.PageWithBoundary;

import java.util.List;
import java.util.Set;

@NotThreadSafe
@Entity
public class PageKind {
	// State
	private PageKind() { }
  	public @Id Long id;  // FIXME: make as string - UserId.pageName
	// FIXME: можно еще доступ сереализовать

  	@Index
	private String name;

	private String rawSource;  // для обновленной версии

  // Теперь страница полностью управляет временем жизни
  // удобен разве что для запроса
  // Можно загружать по нему при загрузке страницы, а потом пользоваться кешем
  @Load  
  private	Key<GeneratorKind> generator;
	private Integer boundaryPtr = PageWithBoundary.STEP_WINDOW_SIZE;  // указатель на текущyю границу
	private Integer referenceVolume = 0;

	// Actions
	public Integer getReferenceVolume() {
		return referenceVolume;
	}

	public void setReferenceVolume(Integer referenceVolume) {
		this.referenceVolume = referenceVolume;
	}
	// Assumption: raw source >> sum(other fields)
	public long getPageByteSize() {
		// it's trouble
		// http://stackoverflow.com/questions/9368764/calculate-size-of-object-in-java
		// http://stackoverflow.com/questions/52353/in-java-what-is-the-best-way-to-determine-the-size-of-an-object
		//
		return rawSource.length();
	}

	public Integer getBoundaryPtr() {
		return boundaryPtr;
	}

	public void setBoundaryPtr(Integer boundaryPtr) {
		this.boundaryPtr = boundaryPtr;
	}

	public String getRawSource() {
		return rawSource;
	}

	public Long getGeneratorId() {
		return generator.getId();
	}

	public Long getId() { return id; }

	public String getName() {
		return name;
	}

	// Пришлось раскрыть
	public void setGenerator(GeneratorKind gen) {
		generator = Key.create(gen);
	}

	public PageKind(String name,String rawSource) {
		this.name = name;
		this.rawSource = rawSource;
	}

	private void checkConnectionPage(PageKind p) {
		if (!p.getId().equals(id))
			throw new IllegalArgumentException();
	}

	private void checkConnectionGenerator(GeneratorKind g) {
		if (!g.getId().equals(generator.getId()))
			throw new IllegalArgumentException();
	}

	public void atomicDelete(final PageKind p, final GeneratorKind g) {
		checkConnectionPage(p);
		checkConnectionGenerator(g);

		OfyService.ofy().transactNew(GAEStoreAccessManager.COUNT_REPEATS, new VoidWork() {
			public void vrun() {
				OfyService.ofy().delete().entity(g).now();
				OfyService.ofy().delete().entity(p).now();
			}
		});
	}

	public static Optional<PageKind> getPageKind(String pageName, Set<Key<PageKind>> keys) {
		List<PageKind> pages =
			OfyService.ofy().transactionless().load().type(PageKind.class)
				.filterKey("in", keys)
				.filter("name = ", pageName)
				.list();

		if (pages.size() > 1) {
			throw new StoreIsCorruptedException();
		}

		if (pages.size() == 0)
			return Optional.absent();

		return Optional.of(pages.get(0));
	}

	public void persist(final PageKind p, final GeneratorKind g) {
		checkConnectionPage(p);
		checkConnectionGenerator(g);

		// execution on dal - можно транслировать ошибку нижнего слоя
		OfyService.ofy().transactNew(GAEStoreAccessManager.COUNT_REPEATS, new VoidWork() {
			public void vrun() {
				OfyService.ofy().save().entity(g).now();
				OfyService.ofy().save().entity(p).now();
			}
		});
	}
}
