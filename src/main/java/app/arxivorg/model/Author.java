package app.arxivorg.model;

public class Author {

    private final String firstName;
    private final String middleName;
    private final String lastName;

    public Author(String firstName, String lastName) {
        this.firstName = firstName;
        this.middleName = "";
        this.lastName = lastName;
    }

    public Author(String firstName, String middleName, String lastName) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
    }

    public String toString() {
        if (middleName.equals("")) return firstName + " " + lastName;
        else return firstName + " " + middleName + " " + lastName;
    }


    /**
     * Test if the Author is equals to another java object.
     * Override the Object equals.
     *
     * @param o : the object we want to test the equality with.
     * @return {@code true} if they are both authors, and if they have the same parameters attribute.
     * {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return firstName.equals(author.firstName) &&
                middleName.equals(author.middleName) &&
                lastName.equals(author.lastName);
    }
}
