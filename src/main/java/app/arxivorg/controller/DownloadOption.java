package app.arxivorg.controller;

public enum DownloadOption {
    SAME_FOLDER,
    CATEGORY_ONLY,
    PUBLICATION_DATE_ONLY,
    CATEGORY_THEN_PUBLICATION_DATE,
    PUBLICATION_DATE_THEN_CATEGORY;

    public static DownloadOption getDownloadOption(String description) {
        switch (description) {
            case "Same folder" : return DownloadOption.SAME_FOLDER;
            case "Sort by category only" : return DownloadOption.CATEGORY_ONLY;
            case "Sort by publication date only" : return DownloadOption.PUBLICATION_DATE_ONLY;
            case "Sort by category then by publication date" : return DownloadOption.CATEGORY_THEN_PUBLICATION_DATE;
            case "Sort by publication date then by category" : return DownloadOption.PUBLICATION_DATE_THEN_CATEGORY;
            default : return null;
        }
    }
}
