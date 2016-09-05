package backend;

import java.util.ArrayList;

import org.apache.commons.collections4.Predicate;
import org.javatuples.Pair;

public final class OwnCollections {
	public static Pair<UniGram, Integer> find(ArrayList<UniGram> collection, Predicate<UniGram> p) {
		Integer pos = 0;
		UniGram r = null;
		for (UniGram kind : collection) {
			if (p.evaluate(kind)) {
				r = kind;
				return Pair.with(r, pos);
			}
			pos++;
		}
		
		return Pair.with(r, -1);
	}
}
