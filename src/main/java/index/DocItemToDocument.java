package index;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

public class DocItemToDocument {
//Creates Lucene Document using two strings: content and title
	public static Document createWith(final String titleStr, final String contentStr) {
		final Document document = new Document();

		final FieldType textIndexedType = new FieldType();
		textIndexedType.setStored(true);
		textIndexedType.setIndexOptions(IndexOptions.DOCS);
		textIndexedType.setTokenized(true);

		// index title
		Field title = new Field("title", titleStr, textIndexedType);
		// index content
		Field content = new Field("content", contentStr, textIndexedType);

		document.add(title);
		document.add(content);
		return document;
	}

}
