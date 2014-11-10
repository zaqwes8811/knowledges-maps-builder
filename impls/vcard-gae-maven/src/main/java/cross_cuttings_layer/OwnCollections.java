package cross_cuttings_layer;

import gae_store_space.NGramKind;

import java.util.ArrayList;

import org.apache.commons.collections4.Predicate;
import org.javatuples.Pair;

public final class OwnCollections {
	public static Pair<NGramKind, Integer> find(ArrayList<NGramKind> collection, Predicate<NGramKind> p) {
		Integer pos = 0;
		NGramKind r = null;
		for (NGramKind kind : collection) {
			if (p.evaluate(kind)) {
				r = kind;
				return Pair.with(r, pos);
			}
			pos++;
		}
		
		return Pair.with(r, -1);
	}
}
