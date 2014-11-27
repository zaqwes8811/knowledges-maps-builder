package cross_cuttings_layer;

import pipeline.Unigram;

import java.util.ArrayList;

import org.apache.commons.collections4.Predicate;
import org.javatuples.Pair;

public final class OwnCollections {
	public static Pair<Unigram, Integer> find(ArrayList<Unigram> collection, Predicate<Unigram> p) {
		Integer pos = 0;
		Unigram r = null;
		for (Unigram kind : collection) {
			if (p.evaluate(kind)) {
				r = kind;
				return Pair.with(r, pos);
			}
			pos++;
		}
		
		return Pair.with(r, -1);
	}
}
