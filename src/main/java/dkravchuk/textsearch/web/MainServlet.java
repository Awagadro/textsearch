package dkravchuk.textsearch.web;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;

import dkravchuk.textsearch.index.DocItemIndexer;
import dkravchuk.textsearch.model.DocItem;
import dkravchuk.textsearch.search.Search;

@WebServlet(name = "MainServlet", urlPatterns = "/search")
public class MainServlet extends HttpServlet {
	public static final String TMP_DIR = System.getProperty("java.io.tmpdir");
	private final Random rnd = new Random(); // to generate safe name for index folder. After tests we removing folders
	private final DocItemIndexer indexer = new DocItemIndexer(TMP_DIR + "/tutorial_test" + rnd.nextInt());
	List<Document> documents;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final String textToSearch = req.getParameter("q");

		documents = readFiles();
		indexer.index(true, documents); // create index
		final Search searchWith = new Search(indexer.readIndex());
		final IndexReader reader = searchWith.reader;

		try {
			ScoreDoc[] searchResults = searchWith.getHits(textToSearch);
		} catch (ParseException e) {
			PrintWriter pw = resp.getWriter();
			pw.println("<H1>К сожалению, поиск не дал результатов</H1>");
			pw.close();
			return;
		}

		resp.setContentType("text/html;charset=utf-8");
		PrintWriter pw = resp.getWriter();
		pw.println("<H1>Результаты поиска:</H1>");

		pw.println("Искали текст: " + textToSearch);

		ScoreDoc[] searchResults;
		int i = 1;
		try {
			searchResults = searchWith.getHits(textToSearch);
			for (ScoreDoc item : searchResults) {
				final String title = reader.document(item.doc).get("title");
				pw.printf("%s: %s", i, title);
				i++;
				pw.println();
			}
		} catch (ParseException e) {
			pw.println("<H1>поиск не дал результатов</H1>");
		}

		pw.close();

		FileUtils.deleteQuietly(new File(indexer.getPathToIndexFolder())); // remove indexes
	}

	private List<Document> readFiles() throws IOException {
		// создаем коллекцию для всех документов в хранилище
		List<Document> documents = new ArrayList();
		List docItems = new ArrayList();

		ServletContext context = getServletContext();
		String fullPath = context.getRealPath("filestore");
		File dir = new File(fullPath);

		// получаем все вложенные объекты в каталоге
		for (File file : dir.listFiles()) {

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
			DocItem docItem = new DocItem(file.getName(), content);
			documents.add(docItem.convertToDocument());// добавляем документ в коллекцию
		}

		return documents;
	}
}
