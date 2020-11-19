package app.arxivorg.CommandLine;

import app.arxivorg.controller.ArxivCLIController;
import app.arxivorg.controller.ArxivOrgController;
import picocli.CommandLine;

import java.util.Scanner;

public class CommandLineManager {

    private ArxivCLIController controller;
    private CommandLine cliMain;
    private boolean isInterpreterMode;

    public CommandLineManager(boolean isInterpreterMode) {
        this.isInterpreterMode = isInterpreterMode;
        this.controller = new ArxivCLIController(5, isInterpreterMode);
        this.cliMain = new CommandLine(new MainCommand());
        initCommand();
    }

    private void initCommand() {
        this.cliMain = cliMain.setCommandName("arxivorg")
                .addSubcommand("list", new ListCommand(controller))
                .addSubcommand("download", new DownloadCommand(controller));
        if (isInterpreterMode)
            this.cliMain = cliMain.setCommandName("")
                    .addSubcommand("select", new SelectCommand(controller));
    }

    public void launch(String[] args) {
        if (isInterpreterMode) launch();
        else cliMain.execute(getSubTable(args, 1, args.length-1));
    }

    public void launch() {
        Scanner input = new Scanner(System.in);
        printInterpreterStartMessage();
        String line; String[] tokens;
        while ((line= input.nextLine()) != null) {
            tokens = line.split("\\s");
            cliMain.execute(tokens);
            printInputMessage();
        }
    }

    private String[] getSubTable(String[] args, int start, int end) {
        if (start < 0 || end < start || end >= args.length)
            throw new IndexOutOfBoundsException();
        String[] subTable = new String[args.length - start];
        System.arraycopy(args, start, subTable, 0, end + 1 - start);
        return subTable;
    }

    private void printInterpreterStartMessage() {
        System.out.println("Welcome to the ArXivorg interpreter!\n" +
                "If you need help about it, input command 'help'\n" +
                "For getting information about a command, enter '<command> [-h][--help]\n");
        printInputMessage();
    }

    private void printInputMessage() {
        System.out.println("Input your command:");
    }


}
