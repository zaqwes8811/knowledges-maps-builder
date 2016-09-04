package http_related;

import java.util.ArrayList;
import java.util.List;

public class PageSummaryValue {
	public final String pageName;
	public final List<String> genNames;
	
	private PageSummaryValue(String pageName, List<String> genNames) {
		this.pageName = pageName;
		this.genNames = genNames;
	}
	
	public static PageSummaryValue create(String pageName, List<String> genNames) {
		return new PageSummaryValue(pageName, genNames);
	}

	public static PageSummaryValue create(String pageName, String genNames) {
		ArrayList<String> s = new ArrayList<>();
		s.add(genNames);
		return new PageSummaryValue(pageName, s);
	}
}
