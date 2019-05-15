package dkravchuk.textsearch.model;

import org.apache.lucene.document.Document;

import dkravchuk.textsearch.index.DocItemToDocument;

public class DocItem {
	private String title;
	private String content;

	public DocItem(String title, String content) {
		super();
		this.title = title;
		this.content = content;
	}

	public Document convertToDocument() {
		return DocItemToDocument.createWith(title, content);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
