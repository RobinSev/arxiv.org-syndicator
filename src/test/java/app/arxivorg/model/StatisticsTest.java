package app.arxivorg.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*** This test is not valid. As I don't currently have the time to correct it, I will only say that the tester should
 * have calculated by hand the expected result of each method using the document he uses as a test data material. Then,
 * he should have checked for each result if the method gives the correct answer.
 */

public class StatisticsTest {

    List<Article> articles = AtomParser.parse(Paths.get("src/test/java/app/arxivorg/model/resources/TestFile.atom.xml"));

    @Test
    public void testStatisticsByCategory() {
        Map<String, Float> stat = Statistics.statisticsByCategory(articles);
        double count = 0;
        for(Map.Entry<String,Float> element : stat.entrySet()){
            count += element.getValue();
        }
        assertEquals(100, count, 0.0001);
    }

    @Test
    public void testStatisticsByAuthor() {
        Map<Author, Float> stat = Statistics.statisticsByAuthor(articles);
        double count = 0;
        for(Map.Entry<Author,Float> element : stat.entrySet()){
            count += element.getValue();
        }
        assertEquals(100, count, 0.0001);
    }

    @Test
    public void testStatisticsByDay() {
        Map<LocalDate, Float> stat = Statistics.statisticsByNumberOfArticlePerDay(articles);
        double count = 0;
        for(Map.Entry<LocalDate,Float> element : stat.entrySet()){
            count += element.getValue();
        }
        assertEquals(100, count, 0.0001);
    }


    @Test
    public void testStatisticsByExpressionInSummary() {
        ArrayList<String> expressions = new ArrayList<>();
        expressions.add("summary*|article");
        expressions.add("1.");

        Map<String, Float> stat = Statistics.statisticsByExpressionInSummary(articles,expressions);
        double count = 0;
        for(Map.Entry<String,Float> element : stat.entrySet()){
            count += element.getValue();
        }
        assertTrue(stat.containsKey("summary"));
        assertTrue(stat.containsKey("article"));
        assertTrue(stat.containsKey("1."));
        assertEquals(100, count, 0.0001);
    }

    @Test
    public void testStatisticsByExpressionInTitle() {
        ArrayList<String> expressions = new ArrayList<>();
        expressions.add("[at]|title");
        expressions.add("[12]|article");

        Map<String, Float> stat = Statistics.statisticsByExpressionInTitle(articles,expressions);
        double count = 0;
        for(Map.Entry<String,Float> element : stat.entrySet()){
            count += element.getValue();
        }
        assertTrue(stat.containsKey("title"));
        assertTrue(stat.containsKey("article"));
        assertTrue(stat.containsKey("2"));
        assertEquals(100, count, 0.0001);
    }
}
