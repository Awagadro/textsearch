package dkravchuk.textsearch.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import dkravchuk.textsearch.model.DocItem;

public class Search {
	public static final int DEFAULT_LIMIT = 10; // how many results to return
	private final IndexReader reader;

	public Search(IndexReader reader) {
		this.reader = reader;
	}

	// Search using FuzzyQuery.
	// toSearch - string to search
	// searchField - where to search
	// limit - how many results to return
	public void fuzzySearch(final String toSearch, final String searchField, final int limit)
			throws IOException, ParseException {
		final IndexSearcher indexSearcher = new IndexSearcher(reader);

		final Term term = new Term(searchField, toSearch);

		final int maxEdits = 2; // This is very important variable. It regulates fuzziness of the query
		final Query query = new FuzzyQuery(term, maxEdits);
		final TopDocs search = indexSearcher.search(query, limit);
		final ScoreDoc[] hits = search.scoreDocs;
		showHits(hits);
	}

	public List<DocItem> getSearchResults(final String toSearch) throws IOException, ParseException {
		List<DocItem> searchResults = new ArrayList();

		final IndexSearcher indexSearcher = new IndexSearcher(reader);
		final Term term = new Term("content", toSearch);
		final int maxEdits = 2;
		final Query query = new FuzzyQuery(term, maxEdits);
		final TopDocs search = indexSearcher.search(query, DEFAULT_LIMIT);
		final ScoreDoc[] hits = search.scoreDocs;
		for (ScoreDoc hit : hits) {
			final String title = reader.document(hit.doc).get("title");
			final String content = reader.document(hit.doc).get("content");

			searchResults.add(new DocItem(title, content));
		}
		return searchResults;
	}

	public void fuzzySearch(final String toSearch) throws IOException, ParseException {
		fuzzySearch(toSearch, "content", DEFAULT_LIMIT);
	}

	private void showHits(final ScoreDoc[] hits) throws IOException {
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
