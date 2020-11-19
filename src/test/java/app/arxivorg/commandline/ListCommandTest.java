package app.arxivorg.commandline;

import app.arxivorg.CommandLine.ListCommand;
import app.arxivorg.CommandLine.MainCommand;
import app.arxivorg.controller.ArxivCLIController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListCommandTest {

    private ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private PrintStream sysOut, sysErr;

    private final CommandLine arXivorgCLI = new CommandLine(new MainCommand());
    private final ArxivCLIController InterpreterController = new ArxivCLIController(5, true);
    private final ArxivCLIController CLIController = new ArxivCLIController(5, false);

    private final String errorMessage = "An error occurred :/.\nPlease check your input and retry.\n" +
            "For more help, input 'list [-h|--help]'.\n";

    private final String[] inputTest1 = ("list -p 2020/04/26 -c cs.CL").split("\\s");
    private final String[] inputTest2 = ("list -p 2020-04-26 -c cs.AZ").split("\\s");

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


    private void setBasicMode() {
        arXivorgCLI.addSubcommand("list", new ListCommand(CLIController));
    }

    private void setInterpreterMode() {
        arXivorgCLI.addSubcommand("list", new ListCommand(InterpreterController));

    }

    public void setNewStreams() {
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void testCallInterpreterMode() {
        setInterpreterMode();
        arXivorgCLI.execute(inputTest1);
        assertEquals(errorMessage, getOutput());
        setNewStreams();
        arXivorgCLI.execute(inputTest2);
        assertEquals(errorMessage, getOutput());
    }

    @Test
    public void testCallBasicMode() {
        setBasicMode();
        arXivorgCLI.execute(inputTest1);
        assertEquals(errorMessage, getOutput());
        setNewStreams();
        arXivorgCLI.execute(inputTest2);
        assertEquals(errorMessage, getOutput());
    }

    @AfterEach
    public void resetStreams() {
        System.setOut(sysOut);
        System.setErr(sysErr);
    }



}
