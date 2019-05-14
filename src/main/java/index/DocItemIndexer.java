package index;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class DocItemIndexer {
	private final String pathToIndexFolder;

	public DocItemIndexer(final String pathToIndexFolder) {
		this.pathToIndexFolder = pathToIndexFolder;
	}

	public void index(final Boolean create, List<Document> documents) throws IOException {
		final Analyzer analyzer = new RussianAnalyzer(); // Indexing documents with RussianAnalyzer
		index(create, documents, analyzer);
	}

	public void index(final Boolean create, List<Document> documents, Analyzer analyzer) throws IOException {
		final Directory dir = FSDirectory.open(Paths.get(pathToIndexFolder));
		final IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		if (create) {
			// Create a new index in the directory, removing any
			// previously indexed documents:
			iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		} else {
			// Add new documents to an existing index:
			iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		}

		final IndexWriter w = new IndexWriter(dir, iwc);
		w.addDocuments(documents);
		w.close();
	}

	public IndexReader readIndex() throws IOException {
		final Directory dir = FSDirectory.open(Paths.get(pathToIndexFolder));
		return DirectoryReader.open(dir);
	}

	public String getPathToIndexFolder() {
		return pathToIndexFolder;
	}
}
