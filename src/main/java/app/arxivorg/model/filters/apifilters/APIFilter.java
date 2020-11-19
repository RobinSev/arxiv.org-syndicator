package app.arxivorg.model.filters.apifilters;

public abstract class APIFilter {

    protected final String[] parameters;

    public APIFilter(String parametersToString) {
        this.parameters = parametersToString.split(",");
        trimArray(parameters);
    }

    /**
     * Complete a specified query with the parameters adapted to the arxiv.org API.
     * The double quotes are used to add a String containing spaces, so that the API can search all the sentence,
     * and not each word individually.
     *
     * @param query : the query to complete.
     * @param PARAMETER_NAME : the name of the parameter type to begin each parameter values with.
     * @return the completed query.
     */
    protected String addParametersToQuery(String query, String PARAMETER_NAME) {
        query += PARAMETER_NAME + ":" + "\"" + parameters[0] + "\"";

        StringBuilder queryBuilder = new StringBuilder(query);
        for(int i = 1; i < parameters.length ; i++) {
            queryBuilder.append(" AND ")
                        .append(PARAMETER_NAME)
                        .append(":").append("\"")
                        .append(parameters[i])
                        .append("\"");
        }
        query = queryBuilder.toString();
        return query;
    }

    /**
     * Remove all the empty spaces before and after the characters in all the String contained in the array given.
     *
     * @param array : the String array in which to trim all elements.
     */
    protected void trimArray(String[] array) {
        for (int i = 0 ; i < array.length ; i++) {
            array[i] = array[i].trim();
        }
    }

    public abstract String addParametersToQuery(String query);

    public String[] getParameters() {
        return parameters;
    }

}
