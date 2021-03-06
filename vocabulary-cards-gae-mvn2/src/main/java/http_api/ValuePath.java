package http_api;

import com.google.common.base.Optional;


// troubles on deserialize
// FIXME: похоже десереализовать можно почти что угодно, просто части объекта будет нулевыми
public class ValuePath {
	//public final UserId
	// FIXME: final is removed, need getters
	private String pageName;
	
	@Deprecated
	public String genName;
	
	public Integer pointPos;
	
	public Optional<String> getPageName() {
		return Optional.fromNullable(pageName);
	}
	
	public Optional<String> getGenName() {
		return Optional.fromNullable(genName);
	}
	
	// для сереализации
	private ValuePath() {}
	
	public ValuePath(String p, String g, Integer pos) {
		pageName = p;
		genName = g;
		pointPos = pos;
	}
}
