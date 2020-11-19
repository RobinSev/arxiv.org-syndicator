package app.arxivorg.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Statistics {

    /**
     * Give statistics by categories in percentage.
     *
     * @param listArticles : item list
     * @return A hasmap giving category statistics.
     */

    public static Map<String, Float> statisticsByCategory(List<Article> listArticles) {
        int counter = 0;
        Map<String, Float> dictionary = new HashMap<>();
        for (Article article : listArticles) {
            for (String category : article.getCategories()) {
                if (dictionary.containsKey(category)) {
                    dictionary.put(category, dictionary.get(category) + 1);
                } else {
                    dictionary.put(category, (float) 1);
                }
                counter++;
            }
        }

        Map<String, Float> statisticCategory = new HashMap<>();
        for (Map.Entry<String, Float> element : dictionary.entrySet()) {
            statisticCategory.put(element.getKey(), (element.getValue() * 100) / counter);
        }
        return statisticCategory;
    }

    /**
     * Give the statistics of the number of articles per day, a given date will have for example 3 items.
     *
     * @param listArticles : item list
     * @return A hasmap giving the number of articles per day.
     */

    public static Map<LocalDate, Float> statisticsByNumberOfArticlePerDay(List<Article> listArticles) {
        int counter = 0;
        Map<LocalDate, Float> dictionary = new HashMap<>();
        for (Article article : listArticles) {
            LocalDate date = article.getPublicationDate();
            if (dictionary.containsKey(date)) {
                dictionary.put(date, dictionary.get(date) + 1);
            } else {
                dictionary.put(date, (float) 1);
            }
            counter++;
        }

        Map<LocalDate, Float> statisticalDate = new HashMap<>();
        for (Map.Entry<LocalDate, Float> element : dictionary.entrySet()) {
            statisticalDate.put(element.getKey(), (element.getValue() * 100) / counter);
        }
        return statisticalDate;
    }

    /**
     * Give the statistics of the number of articles an author to write.
     *
     * @param listArticles : item list
     * @return A hasmap giving The number of articles an author to write.
     */

    public static Map<Author, Float> statisticsByAuthor(List<Article> listArticles) {
        int counter = 0;
        Map<Author, Float> dictionary = new HashMap<>();
        for (Article article : listArticles) {
            for (Author author : article.getAuthors()) {
                if (dictionary.containsKey(author)) {
                    dictionary.put(author, dictionary.get(author) + 1);
                } else {
                    dictionary.put(author, (float) 1);
                }
                counter++;
            }
        }

        Map<Author, Float> statisticalAuthor = new HashMap<>();
        for (Map.Entry<Author, Float> element : dictionary.entrySet()) {
            statisticalAuthor.put(element.getKey(), (element.getValue() * 100) / counter);
        }
        return statisticalAuthor;
    }

    /**
     * Gives statistics of expressions given by the user and returns their frequency in title.
     * This method also accepts regular expressions
     *
     * @param listArticle : item list
     * @return A hasmap giving how often these expressions are used in title.
     */

    public static Map<String, Float> statisticsByExpressionInTitle(List<Article> listArticle, List<String> expressions) {
        Map<String, Float> statisticsByExpression = new HashMap<>();
        int counter = 0;
        for (Article article : listArticle) {
            String[] titleWords = article.getTitle().split(" ");
            for (String word : titleWords) {
                for (String expression : expressions) {
                    Pattern p = Pattern.compile(String.valueOf(expression));
                    Matcher m = p.matcher(word);
                    if (m.matches()) {
                        if (statisticsByExpression.containsKey(word)) {
                            statisticsByExpression.put(word, statisticsByExpression.get(word) + 1);
                        } else {
                            statisticsByExpression.put(word, (float) 1);
                        }
                        counter++;
                    }
                }
            }
        }

        Map<String, Float> statisticsByExpressionInTitle = new HashMap<>();
        for (Map.Entry<String, Float> element : statisticsByExpression.entrySet()) {
            statisticsByExpressionInTitle.put(element.getKey(), (element.getValue() * 100) / counter);
        }

        return statisticsByExpressionInTitle;
    }

    /**
     * Gives statistics of expressions given by the user and returns their frequency in the summary.
     * This method also accepts regular expressions
     *
     * @param listArticle : item list
     * @return A hasmap giving how often these expressions are used in the summary.
     */

    public static Map<String, Float> statisticsByExpressionInSummary(List<Article> listArticle, List<String> expressions) {
        Map<String, Float> statisticsByExpression = new HashMap<>();
        int counter = 0;
        for (Article article : listArticle) {
            String[] titleWords = article.getSummary().split(" ");
            for (String word : titleWords) {
                for (String expression : expressions) {
                    Pattern p = Pattern.compile(String.valueOf(expression));
                    Matcher match = p.matcher(word);
                    if (match.matches()) {
                        if (statisticsByExpression.containsKey(word)) {
                            statisticsByExpression.put(word, statisticsByExpression.get(word) + 1);
                        } else {
                            statisticsByExpression.put(word, (float) 1);
                        }
                        counter++;
                    }
                }
            }
        }

            Map<String, Float> statisticsByExpressionInSummary = new HashMap<>();
            for (Map.Entry<String, Float> element : statisticsByExpression.entrySet()) {
                statisticsByExpressionInSummary.put(element.getKey(), (element.getValue() * 100) / counter);
            }
            return statisticsByExpressionInSummary;
        }
}