package dkravchuk.textsearch.search;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.junit.After;
import org.junit.Test;

import dkravchuk.textsearch.model.DocItem;
import index.DocItemIndexer;

public class SearchTest {
	public static String TMP_DIR = System.getProperty("java.io.tmpdir");
	private final Random rnd = new Random(); // to generate safe name for index folder. After tests we removing folders
	private final DocItemIndexer indexer = new DocItemIndexer(TMP_DIR + "/tutorial_test" + rnd.nextInt());
	final List<Document> documents;

	public SearchTest() throws IOException {
		documents = readFiles();
	}

	@Test
	public void fuzzySearch() throws Exception {
		indexer.index(true, documents); // create index

		final Search searchWith = new Search(indexer.readIndex());
		searchWith.fuzzySearch("сказка");
	}

	@After
	public void removeIndexes() {
		FileUtils.deleteQuietly(new File(indexer.getPathToIndexFolder())); // remove indexes
	}

	public List<Document> readFiles() throws IOException, FileNotFoundException {
		// создаем коллекцию для всех документов в хранилище
		List<Document> documents = new ArrayList();
		List docItems = new ArrayList();
		File dir = new File("filestore");

		// получаем все вложенные объекты в каталоге
		for (File file : dir.listFiles()) {

			DocItem docItem = new DocItem();
			docItem.setTitle(file.getName()); // присваиваем каждому документу имя

			// считываем содержимое
			String content = null;
			FileReader reader = null;
			try {
				reader = new FileReader(file);
				char[] chars = new char[(int) file.length()];
				reader.read(chars);
				content = new String(chars);

				reader.close();

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					reader.close(); // закрываем поток
				}
			}
			docItem.setContent(content); // присваиваем каждому документу содержание
			documents.add(docItem.convertToDocument());// добавляем документ в коллекцию
		}

		return documents;
	}
}
