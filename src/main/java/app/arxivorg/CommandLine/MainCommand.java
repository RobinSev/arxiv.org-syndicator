package app.arxivorg.CommandLine;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command (
        name = "",
        description = "Main command. Without any options, it launches the interpreter",
        version = "arxivorg 0.1.0",
        mixinStandardHelpOptions = true,
        subcommands = {CommandLine.HelpCommand.class}
)
public class MainCommand implements Callable<Integer> {

    @Command(name="quit", description="Stop this process")
    private void exit() {
        System.exit(0);
    }

    @Override
    public Integer call() throws Exception {
        return 0;
    }
}
