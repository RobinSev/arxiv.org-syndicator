package app.arxivorg.commandline;

import app.arxivorg.CommandLine.CommandLineManager;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CommandLineManagerTest {

    private ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private PrintStream sysOut, sysErr;
    private final CommandLineManager notInterpreterManager = new CommandLineManager(false);

    @BeforeEach
    public void setStreams() {
        sysOut = System.out;
        sysErr = System.err;
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    public String getOutput() {
        return outContent.toString();
    }

    public String getErrOutput() {
        return errContent.toString();
    }


    public void setNewStreams() {
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    /*
    Cannot test in interpreter mode beacsue of the exception thrown by the scanner.
     */

    @Test
    public void testLaunch() {
        String[] line = ("list -p 2020-04-12").split("\\s");
        notInterpreterManager.launch(line);
        assertEquals(getErrorMessage(line[1], line[2]), getErrOutput());
    }


    private String getErrorMessage(String... options) {
        String str = getErrorLine(options);
        return str + getUsageMessage();
    }

    private String getUsageMessage() {
        return  "Usage: arxivorg [-hV] [COMMAND]\n" +
                "Main command. Without any options, it launches the interpreter\n" +
                "  -h, --help      Show this help message and exit.\n" +
                "  -V, --version   Print version information and exit.\n" +
                "Commands:\n" +
                "  help      Displays help information about the specified command\n" +
                "  quit      Stop this process\n" +
                "  list      Lists the articles according to the specified filters.\n" +
                "  download  Download articles to the specified path\n";
    }

    @AfterEach
    public void resetStreams() {
        System.setErr(sysErr);
        System.setOut(sysOut);
    }

    @NotNull
    private String getErrorLine(String[] options) {
        String str = "Unknown options: ";
        if (options.length != 0) str += "'" + options[0] + "'";
        for (int i = 1; i < options.length; i++) {
            str += ", '" + options[i] + "'";
        }
        str += "\n";
        return str;
    }

}
