package dkravchuk.textsearch.controllers;

import java.util.List;

import org.springframework.ui.Model;

import dkravchuk.textsearch.model.DocItem;
import dkravchuk.textsearch.search.Search;

public class MainController {
	private Search search;

//	@RequestMapping("/search")
	public String search(String q, Model model) {
		List<DocItem> searchResults = null;
		try {
//			searchResults = search.fuzzySearch(q);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		model.addAttribute("searchResults", searchResults);
		return "search";
	}
}
