package app.arxivorg.controller.JUnitTests;

import app.arxivorg.controller.ParseCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParseCategoryTest {

    private final String filepath = "src/main/java/app/arxivorg/controller/categoryArXiv.txt";
    private final String filenameCategoriesTest = "ArXivCategories.txt";
    private final List<String> categoriesCodes = getCodes(filenameCategoriesTest);
    private ParseCategory categoriesParser;
    private final Map<String, List<String>> categoriesMap = parse();


    @BeforeEach
    public void setUp() {
        categoriesParser = new ParseCategory(filepath);
    }

    @Test
    public void testGetCategoriesCodes() {
        assertEquals(categoriesCodes, categoriesParser.getCategoriesCodes());
    }

    @Test
    public void testGetFileName() {
        assertEquals(filepath, categoriesParser.getFileName());
    }

    @Test
    public void testGetCodeArxiv() {
        assertEquals("cs.AI", categoriesParser.getCodeArxiv("Computer Science", "Artificial Intelligence"));
    }

    @Test
    public void testGetCategories() {
        assertEquals(categoriesMap, categoriesParser.getCategories());
    }


    private List<String> getCodes(String filename) {
        List<String> codes = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = br.readLine()) != null) {
                codes.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return codes;
    }


    private Map<String, List<String>> parse() {
        Map<String, List<String>> categories = new HashMap<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filepath));
            String line;
            String key = "";
            while ((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length == 1) {
                    key = tokens[0];
                    categories.put(key, new ArrayList<>());
                } else {
                    categories.get(key).add(line);
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return categories;
    }

}
