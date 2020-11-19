package app.arxivorg;

import app.arxivorg.CommandLine.CommandLineManager;

import java.io.PrintStream;
import java.util.Scanner;

public class ArxivOrgCLI {

    public static void main(String[] args) {
        printWelcomeMessage();
        Scanner input = new Scanner(System.in);
        args = getLine(input);
        if (!hasValidCommand(args)) {
            System.out.println("bash: no command found '"+ args[0] +"'"); return;
        }
        boolean isInterpreterRequired = args.length == 1;
        CommandLineManager CLIManager = new CommandLineManager(isInterpreterRequired);
        CLIManager.launch(args);
        input.close();
    }

    private static boolean hasValidCommand(String[] args) {
        return args.length > 0 && args[0].equals("arxivorg");
    }

    private static void printWelcomeMessage() {
        System.out.println("Welcome to the arXiv organizer!");
        System.out.println("====================\nInput your command:");
    }

    private static String[] getLine(Scanner scanner) {
        return scanner.nextLine().split("\\s");
    }
}
