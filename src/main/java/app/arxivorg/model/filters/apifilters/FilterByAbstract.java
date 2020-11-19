package app.arxivorg.model.filters.apifilters;

import java.util.Arrays;

public class FilterByAbstract extends APIFilter {

    private static final String PARAMETER_NAME = "abs";

    public FilterByAbstract(String parametersToString) {
        super(parametersToString);
    }

    @Override
    public String addParametersToQuery(String query) {
        return super.addParametersToQuery(query, PARAMETER_NAME);
    }


    /**
     * Test if the APIFilter is equals to another java object.
     * Override the Object equals.
     *
     * @param o : the object we want to test the equality with.
     * @return {@code true} if they are the same type of APIFilter, and if they have the same parameters attribute,
     * whatever the order. {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FilterByAbstract)) return false;
        FilterByAbstract filterByAbstract = (FilterByAbstract) o;
        Arrays.sort(parameters);
        Arrays.sort(filterByAbstract.parameters);
        return Arrays.equals(parameters, filterByAbstract.parameters);
    }
}
