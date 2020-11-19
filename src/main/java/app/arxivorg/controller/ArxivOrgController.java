package app.arxivorg.controller;

/*
    JavaFX imports
 */
import app.arxivorg.controller.popups.DownloadPopup;
import app.arxivorg.controller.popups.ErrorPopup;
import app.arxivorg.controller.popups.ListLastUsePopup;
import app.arxivorg.model.Article;
import app.arxivorg.model.filters.apifilters.*;
import app.arxivorg.model.filters.nonapifilters.FilterByDate;
import app.arxivorg.model.filters.nonapifilters.NonAPIFilter;
import app.arxivorg.model.userintelhandler.UserIntel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

/*
    Project and Java's external libraries imports
 */
import javafx.util.Callback;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;


/**
 * An instance of this class controls the interaction being the graphical user interface and the model.
 * His role is to check datas provided by the user and requests the model with the right information.
 * Then the controller converts data into a viewable format for the user.
 */
public class ArxivOrgController extends Controller implements Initializable  {


    /**
     * This attribute corresponds to the listView where
     * the list of articles will be displayed.
     *
     */
    @FXML private ListView<HBox> listArticles;

    /*
                ATTRIBUTES LINKED WITH THE METADATAS AREA
     */

    /**
     * {@code datasArea} corresponds to the Anchor Pane where details
     * of the selected item in the listView will be displayed.
     */
    @FXML private AnchorPane datasArea;

    /**
     * The download button on the right side of the
     * datasArea mentionned above.
     */
    @FXML private Button downloadDatasAreaButton;

    /**
     * The "favorite" checkBox on the right side of the
     * datasArea
     */
    @FXML private CheckBox favoriteCheckBox;


    @FXML private ImageView imageField;


    /**
     * Attributes linked with filters fields displayed
     * in the graphical user interface
     */
    @FXML private ComboBox<String> categoryField;
    @FXML private ComboBox<String> subcategoryField;
    @FXML private DatePicker calendar;
    @FXML private TextArea authorsArea;
    @FXML private TextArea keywordsArea;
    @FXML private CheckBox searchInTitle;
    @FXML private CheckBox searchInSummary;

    /**
     * Attributes that allows controller to have
     * data to display.
     */
    private final String EMPTY_VALUE_CATEGORY = "Empty";



    public ArxivOrgController() {
        super(50);
    }


    /**
     * A method launched when all the FXML linked objects are initialized.
     * Initializes particularly
     * • A HashMap {@code filters} where each key will
     *      be the name of the field of a filter while the associated value
     *      is the last value the filter had (null when initialized).
     * • The selection model for the listView.
     *
     * @param location - The location used to resolve relative paths for the root object,
     *                 or null if the location is not known.
     * @param resourceBundle - The resources used to localize the root object, or null if
     *                       the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        initCalendar();
        loadCategory();
        loadImage();
        this.listArticles.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        handleSelectedMetadatas();
    }

    /**
     * Initializes the parameters of the DatePicker {@code calendar}
     */
    private void initCalendar() {
        calendar.setShowWeekNumbers(false);
        calendar.setDayCellFactory(getDateCellFactory());
    }

    /**
     * Returns as a CallBack instance, the DateCell factory chosen for the DatePicker.
     * Particularly, the DateCell factory returned enables date cells
     * that are after today and turn their background into gray.
     *
     * @return the DateCell factory chosen for the DatePicker {@code calendar}
     */
    private Callback<DatePicker, DateCell> getDateCellFactory() {
        return new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {

                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item.isAfter(LocalDate.now())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #EEEEEE;");
                        }
                    }
                };
            }
        };
    }


    /**
     * Implements interface {@code ChangeListener<Number>} to listen each selected item
     * in the listView and then display its metadatas in the datasArea.
     */
    private void handleSelectedMetadatas() {
        this.listArticles.getSelectionModel().selectedIndexProperty()
                .addListener((observableValue, hBox, t1) -> {
            printMetadatas(t1.intValue());
        });
    }



    private boolean checkEmptyValueCategory(String str) {
        return str != null && str.equals(this.EMPTY_VALUE_CATEGORY);
    }


    @FXML private void loadSubCategory() {
        if (this.categoryField.getValue().equals("Empty")) {
            this.subcategoryField.getItems().removeAll(this.subcategoryField.getItems());
            return;
        }
        List<String> subCateString = new ArrayList<>();
        for (String str: this.category.get(this.categoryField.getValue())) {
            String[] tmp = str.split(",");
            subCateString.add(tmp[1]);
        }
        this.subcategoryField.setItems(FXCollections.observableArrayList(subCateString));
    }


    private void loadCategory() {
        Iterator<Map.Entry<String, List<String>>> iterCategorys = this.category.entrySet().iterator();
        List<String> newCategory = new ArrayList<>();
        newCategory.add(this.EMPTY_VALUE_CATEGORY);
        while (iterCategorys.hasNext()) {
            Map.Entry<String, List<String>> category = iterCategorys.next();
            newCategory.add(category.getKey());
        }
        this.categoryField.setItems(FXCollections.observableArrayList(newCategory));
    }//loadCategory


    private boolean categoryIsUsed(List<APIFilter> filtersList) {
        if (!checkEmptyValueCategory(this.categoryField.getValue())) {
            if (this.subcategoryField.getValue() != null) {
                String codeCategory = this.categoryParser.getCodeArxiv(this.categoryField.getValue(),
                        this.subcategoryField.getValue());
                filtersList.add(new FilterByCategories(codeCategory));
                return true;
            } else if (this.categoryField.getValue() != null) {
                String codeCategory = this.categoryParser.getCodeArxiv(this.categoryField.getValue(),
                        this.categoryField.getValue());
                filtersList.add(new FilterByCategories(codeCategory));
                return true;
            }
        }
        return false;
    }//addFilterCategory


    private boolean keyWordIsUsed(List<APIFilter> filtersList) {
        if (!keywordsArea.getText().isEmpty()) {
            if (this.searchInSummary.isSelected() && this.searchInTitle.isSelected() ||
                    !this.searchInSummary.isSelected() && !this.searchInTitle.isSelected()) {
                filtersList.add(new FilterByAbstract(this.keywordsArea.getText()));
                filtersList.add(new FilterByTitle(this.keywordsArea.getText()));
                return true;
            } else if (this.searchInSummary.isSelected()) {
                filtersList.add(new FilterByAbstract(this.keywordsArea.getText()));
                return true;
            } else {
                filtersList.add(new FilterByTitle(this.keywordsArea.getText()));
                return true;
            }
        }
        return false;
    }//addFilterKeyWord


    private boolean authorsAreaIsUsed(List<APIFilter> filtersList) {
        if (!this.authorsArea.getText().isEmpty()) {
            filtersList.add(new FilterByAuthors(this.authorsArea.getText()));
            return true;
        }
        return false;
    }//authorsAreaIsUsed


    private List<APIFilter> getAPIFiltersInGUI() {
        List<APIFilter> filtersList = new ArrayList<>();
        categoryIsUsed(filtersList);
        keyWordIsUsed(filtersList);
        authorsAreaIsUsed(filtersList);
        return filtersList;
    }//getFilters


    @FXML protected void requestMoreArticles() {
        if (this.listHandler.getArticles().isEmpty()) {
            ErrorPopup.display("No more articles found with that filters!");
            return;
        }
        try {
            List<Article> newArticles = super.model.requestMoreArticles(this.listHandler.getArticles());
            saveNewArticles(newArticles);
        } catch (Exception e) {
            ErrorPopup.display(e.getMessage());
        }
    }

    /**
     * Sets the specified list of articles as the new listHandler's {@code articles} list.
     *
     * @param newArticles - The articles to save in listHandler.
     */
    private void saveNewArticles(List<Article> newArticles) {
        this.listHandler.setArticles(new ArrayList<>(this.listHandler.getSelected()));
        this.listHandler.addAllInArticlesList(newArticles);
    }

    /**
     * Shows in the listView container the articles saved in listHandler.
     */
    private void showSavedArticles() {
        this.listArticles.getItems().clear();
        this.listArticles.getItems().addAll(this.listHandler.getDisplayableItems());
    }


    /**
     * @return the list of the values of the nonAPIFilters provided
     * by the user in the GUI.
     */
    private List<NonAPIFilter> getNoAPIFiltersInGUI() {
        List<NonAPIFilter> nonAPIFilters = new ArrayList<>();
        LocalDate date = this.calendar.getValue();
        if (date != null)
            nonAPIFilters.add(new FilterByDate(date));
        return nonAPIFilters;
    }


    /**
     * Makes a request to the model according ti the values provided in each
     * filters showing in the GUI.
     * Precisely, it first calls the requestWithFilters method of the abstract class controller,
     * and handle the process according to the returned list of articles.
     * If this list is empty, it shows a popup that specified the case to the user.
     * Else, it saves the received articles in listHandler class and show them.
     *
     * If the method requestWithFilters throws an exception, then the exception is caught
     * and an error popup is showing to the user.
     */
    @FXML private void request() {
        try {
            List<Article> newArticles = super.requestWithFilters(
                    getAPIFiltersInGUI(),
                    getNoAPIFiltersInGUI(),
                    super.NUMBERS_OF_ARTICLES_TO_REQUEST
            );
            if (newArticles.isEmpty()) {
                ErrorPopup.display("No articles to show.");
                return;
            }
            saveNewArticles(newArticles);
            showSavedArticles();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorPopup.display("Sorry, the request process exit with an exception.\n" +
                    "Please, think to check the filters before retrying.");
        }
    }

    /**
     * Prints in the datasArea the matadatas of the article
     * at the specified {@code index} in the artciles list
     * in the listHandler instance.
     * @param index the index of the article to consider.
     */
    private void printMetadatas(int index) {
        if(index < 0 || index >= listHandler.getArticles().size()) return;
        HBox item = listHandler.getMetadata(index);
        item.setPrefWidth(datasArea.getWidth());
        datasArea.getChildren().setAll(item);
    }


    /**
     * Loads the arXiv logo image to the imageView on
     * the right side of the GUI.
     */
    private void loadImage() {
        try {
            File file = new File(String.valueOf(getClass().getResource("/app.arxivorg.image/arXiv.png")));
            imageField.setImage(new Image(file.toURI().toURL().toString(), true));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recovers the index of the selected items in the listView and
     * gets the article corresponding to the same index in the listView.
     *
     * Then, it calls the handleDownloadProcess method with the title of the pop-up and
     * the list of article to download.
     *
     */
    @FXML
    private void downloadCurrentArticle() {
        int articleIndex = listArticles.getSelectionModel().getSelectedIndex();
        if (articleIndex < 0) {
            ErrorPopup.display("No articles to download"); return;
        }
        Article article = listHandler.getArticles().get(articleIndex);
        handleDownloadProcess("Download the article", List.of(article));
    }


    /**
     * Calls the handleDownloadProcess method with all the articles
     * in the listHandler's {@code articles} list as the list of articles to download.
     */
    @FXML
    private void downloadAll() {
        handleDownloadProcess("Download all articles",listHandler.getArticles());
    }

    /**
     * Calls the handleDownloadProcess method with the articles
     * in the listHandler's {@code selected} list as the list of articles to download.
     */
    @FXML
    private void downloadSelectedArticles() {
        handleDownloadProcess("Download your selection", listHandler.getSelected());
    }


    /**
     * Shows, if the list of articles to download is not empty, the pop-up to the user
     * for recovering the download options the the path.
     * Then it calls the download method (abstract class Controller) and the
     * method to handle the message to show to the user.
     *
     * @param popupTitle - The title of the pop-up shown to the user. It depends of the
     *                   clicked button in the GUI.
     * @param articles - The articles to download.
     */
    private void handleDownloadProcess(String popupTitle, List<Article> articles) {
        if (articles.isEmpty()) {
            ErrorPopup.display("No articles to download"); return;
        }
        List<String> inputs = DownloadPopup.getDownloadOptions(popupTitle);
        if (inputs == null) return;
        DownloadOption option = DownloadOption.getDownloadOption(inputs.get(1));
        String path = inputs.get(0);
        int notDownloaded = download(path, articles, option);
        int nbArticlesToDownload = articles.size();
        showDownloadEndMessage(notDownloaded, nbArticlesToDownload, path);
    }

    /**
     * Shows a message to the user according to the value of the specified {@code notDownloaded}
     * parameter.
     *
     * • If notDownloaded has a negative value, it's an error message so that
     *      it calls the showDownloadErrorMessage function.
     * • If no articles were downloaded (notDownloaded equals to the number of articles to download),
     *      the method shows to the user an error message specifying the aborting of the process.
     * • Otherwise, it shows a notification to the user specifying the path
     *      and the number of downloaded articles.
     *
     *
     * @param path - The path where the user want to download articles.
     * @param notDownloaded - The return code of the download method. If it's positive or 0,
     *                      this code is the number of not downloaded articles.
     *                      Otherwise, it is the error code of the function.
     * @param nbArticlesToDownload - The number of articles the user wants to download.
     */
    private void showDownloadEndMessage(int notDownloaded, int nbArticlesToDownload, String path) {
        if (notDownloaded < 0) {
            showDownloadErrorMessage(notDownloaded);
            return;
        }
        if (notDownloaded == 0) {
            NotificationsHandler.showConfirmation("Download complete",
                    nbArticlesToDownload +" articles downloaded to " + path);
            return;
        }
        if (notDownloaded == nbArticlesToDownload)
            ErrorPopup.display("Download process aborted : Please check the path and allow " +
                    "us\nto read in write in the folder you want to download into.");
        if (notDownloaded < nbArticlesToDownload)
            NotificationsHandler.showWarning("Download partially complete",
                    notDownloaded + " articles couldn't be downloaded.");

    }

    /**
     * Shows in an error pop-up the error message
     * thrown by the download process.
     * The message is chosen according to the specified error code
     * (that must be negative).
     *
     * @param exitCode - The negative error code of downloading.
     */
    private void showDownloadErrorMessage(int exitCode) {
        if (exitCode == -3) {
            ErrorPopup.display("No article to download");
        }
        if (exitCode == -2) {
            ErrorPopup.display("The task exit with an error code (-2 : Invalid option).");
        }
        if (exitCode == -1)
            ErrorPopup.display("Invalid path format. Please input an absolute path.");
        if (exitCode < -3 || exitCode > -1)
            ErrorPopup.display("An unexpected error occurred making the task unavailable. Please retry.");
    }

    /**
     *
     */
    @FXML
    private void getArticlesSinceLastUse() {
        boolean shown = ListLastUsePopup.show("List articles since last use");
        if (!shown) return;
        try {
            UserIntel userIntel = model.getUserIntel();
            int numberOfArticles = ListLastUsePopup.getNumberOfArticles() == -1 ?
                    super.NUMBERS_OF_ARTICLES_TO_REQUEST : ListLastUsePopup.getNumberOfArticles();
            List<NonAPIFilter> nonAPIFilters = new ArrayList<>();
            if (ListLastUsePopup.getStartDate().equals(LocalDate.now()))
                nonAPIFilters = userIntel.getLastNonAPIFiltersUsed();
            else
                nonAPIFilters.add(new FilterByDate(ListLastUsePopup.getStartDate()));
            List<APIFilter> apiFilters = userIntel.getLastAPIFiltersUsed();
            List<Article> newArticles = super.requestWithFilters(apiFilters, nonAPIFilters, numberOfArticles);
            saveNewArticles(newArticles);
            showSavedArticles();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorPopup.display("error load last usage");
        }
    }


    /**
     * Shows a popup to the user explaining that the action he asks for
     * is not implemented yet.
     * This method is linked with buttons that are in GUI but don't have
     * any implemented method in the controller.
     *
     */
    @FXML
    private void notImplemented() {
        ErrorPopup.display("Sorry, this task is not available yet.");
    }

}
