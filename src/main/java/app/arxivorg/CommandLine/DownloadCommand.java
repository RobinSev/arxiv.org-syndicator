package app.arxivorg.CommandLine;

import app.arxivorg.controller.ArxivCLIController;
import app.arxivorg.controller.DownloadOption;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import javax.annotation.Nonnull;
import java.util.HashMap;

/**
 *
 */

@Command(
        name = "download",
        description = "Download articles to the specified path",
        version = "download 0.1.0",
        mixinStandardHelpOptions = true
)
public class DownloadCommand extends FilterableCommand {

    /**
     * Creates an instance of this class with the specified {@code controller}
     *
     * @param controller - The controller communicating with the model.
     */
    public DownloadCommand(@Nonnull ArxivCLIController controller) {
        this.controller = controller;
    }


    /**
     * The filepath.
     * The user must specified one and only one path each time he uses this command.
     */
    @Parameters(arity = "1", description = "Path to the folder where you want to download.")
    private String filepath = "/";

    /**
     * Describes how the command process the datas.
     * More precisely, if the user has selected articles,
     * these are taken to be selected.
     *
     *
     * @return
     */
    @Override
    public Integer call() {
        int downloadStatus = -2;
        if (controller.isInterpreterMode()) {
            downloadStatus = controller.downloadSelected(filepath);
        } else {
            HashMap<String, String> filters = getFilters();
            if (!filters.isEmpty())
                downloadStatus = controller.downloadAll(filepath, filters);
        }
        printMessage(downloadStatus);
        return 0;
    }

    /**
     * Print the right message according to the exit status of
     * the controller's download method.
     *
     * @param downloadStatus - The exit status of the download method.
     */
    private void printMessage(int downloadStatus) {
        if (downloadStatus == 0)
            System.out.println("Download complete to " + filepath + ".");
        if (downloadStatus > 0)
            System.out.println(downloadStatus + " articles couldn't be downloaded to " + filepath);
        if (downloadStatus == -1) {
            System.out.println("Impossible to download. Please, check the path and " +
                    "allow us to read and write in the folder you want us to download into.");
            CommandLine.usage(this, System.out);
        }
        if (downloadStatus == -2) {
            System.out.println("Invalid input : specify filters or " +
                    "select articles (only in interpreter mode)");
            CommandLine.usage(this, System.out);
        }
        if (downloadStatus == -3)
            System.out.println("The task exit with an exception. Please retry later.");
    }


}