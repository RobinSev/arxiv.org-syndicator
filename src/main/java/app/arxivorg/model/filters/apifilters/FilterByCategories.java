package app.arxivorg.model.filters.apifilters;

import java.util.Arrays;

public class FilterByCategories extends APIFilter {

    private static final String PARAMETER_NAME = "cat";

    public FilterByCategories(String parametersToString) {
        super(parametersToString);
    }


    /**
     * Complete a specified query with the parameters adapted to the arxiv.org API.
     * Override the APIFilter method, in order not to use double quotes, unnecessary here.
     *
     * @param query : the query to complete.
     * @return the completed query.
     */
    @Override
    public String addParametersToQuery(String query) {
        query += PARAMETER_NAME + ":" + parameters[0];

        StringBuilder queryBuilder = new StringBuilder(query);
        for(int i = 1; i < parameters.length ; i++) {
            queryBuilder.append(" AND " + PARAMETER_NAME + ":").append(parameters[i]);
        }
        query = queryBuilder.toString();
        return query;
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
        if (!(o instanceof FilterByCategories)) return false;
        FilterByCategories filterByCategories = (FilterByCategories) o;
        Arrays.sort(parameters);
        Arrays.sort(filterByCategories.parameters);
        return Arrays.equals(parameters, filterByCategories.parameters);
    }
}
