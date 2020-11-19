package app.arxivorg.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseCategory {

	private String fileName;
	private List<String> categoriesCodes;
	private Map<String, List<String>> categorys;

	/**
	 * Builds a class instance and calls
	 * the method parsingFile that parses the file.
	 *
	 * @param fileName - file path of the file to be parsed
	 * */
	public ParseCategory(String fileName) {
		this.fileName = fileName;
		this.categoriesCodes = new ArrayList<>();
		this.categorys = new HashMap<>();
		parsingFile();
	}

	/**
	 * @return the value the attribute categoriesCodes
	 * */
	public List<String> getCategoriesCodes() {
		return this.categoriesCodes;
	}

	public String getFileName() {
		return this.fileName;
	}

	/**
	 * @return the value the attribute categorys
	 * */
	public Map<String, List<String>> getCategories() {
		return this.categorys;
	}


	/**
	 * This method looks for a correspondence between the parameters and
	 * the content of the attribute categorys.
	 *
	 * @param categoryChose - categoryChose is the category chosen by the user in the
	 *                         ComboBox in the category filters section.
	 * @param subCategoryChose - subCategoryChose is the subcategory chosen by the user in the
	 *                         ComboBox in the category filters section.
	 * @return An ArXivOrg code is returned in String format.
	 * */
	public String getCodeArxiv(String categoryChose, String subCategoryChose) {
		String codeCategory = "";
		for (String elemeCategorys: this.categorys.get(categoryChose)) {
			String[] codeAndDescriptionOfCategory = elemeCategorys.split(",");
			if (codeAndDescriptionOfCategory[1].equals(subCategoryChose)) codeCategory = codeAndDescriptionOfCategory[0];
		}
		return codeCategory;
	}


	private void parsingFile() {
		Map<String, List<String>> category = new HashMap<>();
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(getFileName()));
			String lines = "";
			String key = "";
			while ((lines = bufferedReader.readLine()) != null) {
				String[] codeAndDescriptionOfCategory = lines.split(",");
				if (codeAndDescriptionOfCategory.length == 1) {
					key = codeAndDescriptionOfCategory[0];
					category.put(key, new ArrayList<>());
				} else {
					category.get(key).add(lines);
					this.categoriesCodes.add(codeAndDescriptionOfCategory[0]);
				}
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.categorys = category;
	}


}//class
