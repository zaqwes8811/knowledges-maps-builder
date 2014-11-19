package instances;

// FIXME: а нужно ли? возможно просто понадобятся несколько кешей и конвееров?
public class ResearchInstance {
	public static class Holder {
		static final ResearchInstance w = new ResearchInstance();
	}
	
	public static ResearchInstance getInstance() {
		return Holder.w;
	}
}
