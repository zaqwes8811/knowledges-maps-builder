package gae_store_space;

import static gae_store_space.OfyService.ofy;
import gae_store_space.high_perf.OnePageProcessor;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;

import servlets.protocols.PageSummaryValue;
import servlets.protocols.PathValue;

public class AppInstance {
	private final OnePageProcessor processor = new OnePageProcessor();
	
	public PageKind createPageIfNotExist(String name, String text, String type) {
		// FIXME: add user info
		Optional<List<PageKind>> pages = 
				Optional.fromNullable(ofy().load().type(PageKind.class).filter("name = ", name).list());
		
		if (!pages.isPresent()) {
	  	// Own tables
	  	// FIXME: GAE can't read file.
	  	PageKind p0 = processor.buildPageKind(OnePageProcessor.defaultPageName, processor.getTestFileName());
	  	ofy().save().entity(p0).now();
	  	
	  	// TODO: А если здесь проверить сохранена ли, то иногда будет несохранена!
	  	
	  	// add generator
	  	GeneratorKind g = GeneratorKind.create(p0.getRawDistribution());
	  	ofy().save().entity(g).now();
	  	p0.setGenerator(g);
	  	ofy().save().entity(p0).now();
			
			return p0;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public AppInstance() {
		// пока создаем один раз и удаляем. классы могут менятся, лучше так, чтобы не было 
		//   конфликтов.
		//
  	ofy().delete().keys(ofy().load().type(PageKind.class).keys()).now();
  	ofy().delete().keys(ofy().load().type(GeneratorKind.class).keys()).now();
  	ofy().delete().keys(ofy().load().type(WordKind.class).keys()).now();
  	
  	{
	  	// Own tables
	  	// FIXME: GAE can't read file.
	  	PageKind p0 = processor.buildPageKind(OnePageProcessor.defaultPageName, processor.getTestFileName());
	  	ofy().save().entity(p0).now();
	  	
	  	// TODO: А если здесь проверить сохранена ли, то иногда будет несохранена!
	  	
	  	// add generator
	  	GeneratorKind g = GeneratorKind.create(p0.getRawDistribution());
	  	ofy().save().entity(g).now();
	  	p0.setGenerator(g);
	  	ofy().save().entity(p0).now();
  	}
  	
  	{
  		PageKind p0 = processor.buildPageKind(OnePageProcessor.defaultPageName+"_fake", processor.getTestFileName());
    	ofy().save().entity(p0).now();
    	
    	// TODO: А если здесь проверить сохранена ли, то иногда будет несохранена!
    	
    	// add generator
    	GeneratorKind g = GeneratorKind.create(p0.getRawDistribution());
    	ofy().save().entity(g).now();
    	p0.setGenerator(g);
    	ofy().save().entity(p0).now();
  	}
  	
  	// Try read
  	///*
  	// Скрыл, но должно работать!!
  	List<PageKind> pages = ofy().load().type(PageKind.class).filter("name = ", OnePageProcessor.defaultPageName).list();
  	
  	// FIXME: иногда страбатывает - почему - не ясно - список пуст, все вроде бы синхронно
  	if (pages.isEmpty()) 
  		throw new IllegalStateException();
  	
  	if (pages.size() > 1) 
  		throw new IllegalStateException();
	}
	
	// FIXME: may be non thread safe. Да вроде бы должно быть база то потокобезопасная?
	//synchronized  // не в этом дело 
	public PageKind getPage(String nameName) {
		
		List<PageKind> pages = 
    		ofy().load().type(PageKind.class).filter("name = ", nameName).list();
    
    if (pages.size() != 1)
  		throw new IllegalStateException();
    
    return pages.get(0);  // 1 item
	}

	//private static AppInstance w = null;
	//static {
	//	w = new AppInstance(); 
	//}
	
	private static class Holder {
		static final AppInstance w = new AppInstance();
	}
	
	public static AppInstance getInstance() {
		return Holder.w;
	}
	
	// пока не ясно, что за идентификация будет для пользователя
	// данных может и не быть, так что 
	public List<PageSummaryValue> getUserInformation(String userId) {
		// FIXME: пока без фильтра пользователя
		List<PageKind> pages = ofy().load().type(PageKind.class).list();
		
		List<PageSummaryValue> r = new ArrayList<PageSummaryValue>();
		for (PageKind page: pages) {
			r.add(PageSummaryValue.create(page.getName(), page.getGenNames()));
		}
		
		return r;
	}
	
	public void disablePoint(PathValue p) {
		PageKind page = getPage(p.pageName);
		GeneratorKind g = page.getGenerator(p.genName);
		g.disablePoint(p.pointPos);
		
		ofy().save().entity(g).now();
	} 
}
