package dkravchuk.textsearch.search;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class Search {
	public static final int DEFAULT_LIMIT = 10; // количество возвращаемых результатов поиска
	public final IndexReader reader;

	public Search(IndexReader reader) {
		this.reader = reader;
	}

	// toSearch - что ищем
	// searchField - где ищем

	public ScoreDoc[] getHits(final String toSearch) throws IOException, ParseException {

		final IndexSearcher indexSearcher = new IndexSearcher(reader);
		final Term term = new Term("content", toSearch);
		final int maxEdits = 2;
		final Query query = new FuzzyQuery(term, maxEdits);
		final TopDocs search = indexSearcher.search(query, DEFAULT_LIMIT);
		final ScoreDoc[] hits = search.scoreDocs;
		return hits;
	}

	public void showHits(final ScoreDoc[] hits) throws IOException {
		if (hits.length == 0) {
			System.out.println("\n\tНичего не найдено");
			return;
		}
		System.out.println("\n\tРезультаты поиска:");
		for (ScoreDoc hit : hits) {
			final String title = reader.document(hit.doc).get("title");
			final String content = reader.document(hit.doc).get("content");
			System.out.println("\n\tDocument Id = " + hit.doc + "\n\ttitle = " + title + "\n\tscores = " + hit.score);
		}
	}
}
