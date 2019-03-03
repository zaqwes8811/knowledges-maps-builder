package http_api;

import java.util.ArrayList;
import java.util.List;

public class ValuePageSummary {
	public final String pageName;
	public final List<String> genNames;
	
	private ValuePageSummary(String pageName, List<String> genNames) {
		this.pageName = pageName;
		this.genNames = genNames;
	}
	
	public static ValuePageSummary create(String pageName, List<String> genNames) {
		return new ValuePageSummary(pageName, genNames);
	}

	public static ValuePageSummary create(String pageName, String genNames) {
		ArrayList<String> s = new ArrayList<>();
		s.add(genNames);
		return new ValuePageSummary(pageName, s);
	}
}
