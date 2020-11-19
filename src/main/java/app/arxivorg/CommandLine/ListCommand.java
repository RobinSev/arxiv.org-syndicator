package app.arxivorg.CommandLine;

import app.arxivorg.controller.ArxivCLIController;
import app.arxivorg.model.Article;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;


@Command(
        name = "list",
        description = "Lists the articles according to the specified filters.",
        version = "list 0.1.0",
        mixinStandardHelpOptions = true
)
public class ListCommand extends FilterableCommand {

    private String errorMessage = "An error occurred :/.\nPlease check your input and retry.\n" +
            "For more help, input 'list [-h|--help]'.";

    public ListCommand(ArxivCLIController controller) {
        this.controller = controller;
    }

    @Override
    public Integer call() {
        HashMap<String, String> filters = getFilters();
        List<Article> articles = controller.requestArticles(filters);
        if (articles == null)
            System.out.println(errorMessage);
        else printArticles(articles);
        return 0;
    }

    private void printArticles(List<Article> articles) {
        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            System.out.println(
                    i + 1 + ". " + article.getTitle()
                    + "\n" + article.getAuthors() + "\n"
                    + article.getArxivLink());
        }
    }
}
