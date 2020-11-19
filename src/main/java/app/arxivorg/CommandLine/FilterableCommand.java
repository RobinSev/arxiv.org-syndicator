package app.arxivorg.CommandLine;

import app.arxivorg.controller.ArxivCLIController;
import picocli.CommandLine.Option;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * This class is extended by all the classes that acts according to the filters.
 * Here, filters are described as options that ca be use by the user to refine
 * his search.
 *
 * Multiple commands need or will need these filters, explaining they are grouped
 * in an abstract class.
 *
 * By default, only the filter period is initialized, with today's date.
 *
 * This class also contains options-specific methods.
 */
public abstract class FilterableCommand implements Callable<Integer> {

    protected ArxivCLIController controller;

    /**
     * The category filter. The user can enter one and only one ArXiv code
     * corresponding to a category.
     */
    @Option(names={"-c", "--category"},
            description = "Filters by category. The category must be given by its ArXiv code (e.g.: 'cs.CL')",
            arity = "1")
    protected String category;


    /**
     * The period filter. The user must enter one and only one date in yyyy-mm-dd format.
     */
    @Option(names={"-p", "--period"},
            description = "Filters by period. Must be like yyyy-mm-dd. By default, the value is today's date",
            arity = "1")
    protected String period;// = LocalDate.now().toString();


    /**
     * The authors filter. The user can specify one or more authors. Their names must
     *  be comma separated.
     */
    @Option(names={"-a", "--authors"},
            description = "Filters by authors. Each author must be separated from " +
                    "the others by a comma  (e.g.: 'Yann LeCun, Geoffrey Hinton')",
            arity = "1..*")
    protected List<String> authors;


    /**
     * The keywords filter. The user can specify one or more keywords. They must
     *  be comma separated.
     */
    @Option(names={"-k", "-keywords"},
            description = "Filters by keywords. They must be comma separated (e.g.: 'AI, Machine learning')",
            arity = "1..*",
            split=",\\s")
    protected List<String> keywords;


    /**
     * Describes how the command that extends this class processes
     * the informations providing by the user.
     * It's abstract because depends of each filterable command.
     *
     * @return the exit code of the process (0 if all went great, not 0 otherwise).
     * @throws Exception - Prevents exception that can be launched by this method.
     */
    @Override
    public abstract Integer call() throws Exception;


    /**
     * Returns, as a string, the list of string specified as {@code list}
     * by concatenating all of this elements, separated by a whitespace.
     * The generated string is trimmed before being returned.
     *
     * @param list - the list the returned string is generated from.
     * @return the string representing the specified list.
     */
    protected String stringify(List<String> list) {
        if (list == null) return null;
        if (list.isEmpty()) return "";
        StringBuilder listToString = new StringBuilder();
        for (String element : list) {
            listToString.append(element).append(" ");
        }
        return listToString.toString().trim();
    }


    /**
     * Returns a HashMap instance containing the non-null
     * filters (i.e. the options the user provided).
     *
     * @return HashMap instance containing the filters provided by the user.
     */
    protected HashMap<String, String> getFilters() {
        HashMap<String, String> filters = new HashMap<>();
        if (category != null) filters.put("category", category);
        if (keywords != null) filters.put("keywords", stringify(keywords));
        if (period != null) filters.put("period", period);
        if (authors != null) filters.put("authors", stringify(authors));
        return filters;
    }
}
