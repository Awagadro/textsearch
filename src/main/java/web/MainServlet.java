package web;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;

import dkravchuk.textsearch.model.DocItem;
import dkravchuk.textsearch.search.Search;
import index.DocItemIndexer;

@WebServlet(name = "MainServlet", urlPatterns = "/search")
public class MainServlet extends HttpServlet {
	public static final String TMP_DIR = System.getProperty("java.io.tmpdir");
	private final Random rnd = new Random(); // to generate safe name for index folder. After tests we removing folders
	private final DocItemIndexer indexer = new DocItemIndexer(TMP_DIR + "/tutorial_test" + rnd.nextInt());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final String textToSearch = req.getParameter("q");

		final List<Document> documents;
		documents = readFiles();
		indexer.index(true, documents); // create index

		List<DocItem> searchResults = new ArrayList();

		final Search searchWith = new Search(indexer.readIndex());
		try {
			searchResults = searchWith.getSearchResults(textToSearch);
		} catch (ParseException e) {
			PrintWriter pw = resp.getWriter();
			pw.println("<H1>К сожалению, поиск не дал результатов</H1>");
			pw.close();
			return;
		}
		FileUtils.deleteQuietly(new File(indexer.getPathToIndexFolder())); // remove indexes

		resp.setContentType("text/html;charset=utf-8");
		PrintWriter pw = resp.getWriter();
		pw.println("<H1>Результаты поиска:</H1>");

		for (DocItem item : searchResults) {
			pw.printf("<title>%s</title>", item.getTitle());
			pw.println();
		}
		pw.close();

		// getServletContext().getRequestDispatcher("/searchResult.jsp").forward(req,
		// resp);

	}

	private List<Document> readFiles() throws IOException {
		// создаем коллекцию для всех документов в хранилище
		List<Document> documents = new ArrayList();
		List docItems = new ArrayList();
		File dir = new File("filestore");

		// получаем все вложенные объекты в каталоге
		for (File file : dir.listFiles()) {

			DocItem docItem = new DocItem(null, null);
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
