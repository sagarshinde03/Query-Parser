package edu.buffalo.cse.irf14.query;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import edu.buffalo.cse.irf14.SearchRunner;

public class Scorer{

	private QueryDocumentIndex queryDocs = new QueryDocumentIndex();

	public TreeMap<Double, String> evaluateScore( List<QueryTermIndex> termsMapping, Query query, SearchRunner.ScoringModel model ) {

		TreeMap<Double, String> retVal = new TreeMap<Double, String>();
		DecimalFormat formatter = new DecimalFormat("0.#####");
		try {
		for ( QueryTermIndex temp : termsMapping ) {
			queryDocs.add(temp.getTerm(), temp.getPostings(), temp.getDocFreq());
		}
		if( model.equals( SearchRunner.ScoringModel.TFIDF ) ) {
			queryDocs.normalizeTfIdfWeight(); 
			for( Entry<String, List<Term>> entry : queryDocs.getDocTermMap().entrySet() ) {
				double docScore = 0.0;
				for( Term term : entry.getValue() ) {
					docScore += term.getWeight()*query.getQueryTerms().get( term.getTerm() );
				}
				retVal.put( Double.valueOf( formatter.format( docScore ) ), entry.getKey() );
			}	
		} else {
			queryDocs.computeOkapiScore();
			for( Entry<String, List<Term>> entry : queryDocs.getDocTermMap().entrySet() ) {
				double docScore = 0.0;
				for( Term term : entry.getValue() ) {
					docScore += term.getWeight();
				}
				retVal.put( Double.valueOf( formatter.format( docScore ) ), entry.getKey() );
			}
		}
		return retVal;
		} catch( Exception e) {
			return null;
		}
	}

}
