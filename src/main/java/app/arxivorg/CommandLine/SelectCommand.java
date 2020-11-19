package app.arxivorg.CommandLine;

import app.arxivorg.controller.ArxivCLIController;
import app.arxivorg.controller.ArxivOrgController;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.Callable;

@Command(
        name = "select",
        description = "select the articles by their numbers in the generated list",
        version = "list 0.1.0",
        mixinStandardHelpOptions = true
)
public class SelectCommand implements Callable<Integer> {

    private ArxivCLIController controller;

    public SelectCommand(@Nonnull ArxivCLIController controller) {
        this.controller = controller;
    }

    @Parameters(index = "0..*", arity = "1", description = "Indexes of the articles you want to select")
    private List<Integer> selected;


    @Override
    public Integer call() {
        if (selected == null) CommandLine.usage(this, System.out);
        int status = controller.setSelected(selected);
        if (status == 0)  System.out.println("Selection updated with success\n");
        else if (status == 1)
            System.out.println("Index out of bounds : list is empty or index is not between 1 and 5.");
            else System.out.println("An error occurred. Please retry or input 'select -h' for help.");
        return 0;
    }

}