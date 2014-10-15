package servlets.protocols;

public class PathValue {
	//public final UserId
	public final String pageName;
	public final String genName;
	public final Integer pointPos;
	
	public PathValue(String p, String g, Integer pos) {
		pageName = p;
		genName = g;
		pointPos = pos;
	}
}
