package app.arxivorg.model.userintelhandler;

import app.arxivorg.model.Article;
import app.arxivorg.model.filters.apifilters.APIFilter;
import app.arxivorg.model.filters.nonapifilters.NonAPIFilter;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserIntelHandler {

    private final Path filePath;
    private final GsonBuilder gson;

    public UserIntelHandler(String filePath) {
        this.filePath = Paths.get(filePath);
        GsonBuilder gson = new GsonBuilder().registerTypeAdapter(APIFilter.class, new APIFilterClassAdapter());
        this.gson = gson.registerTypeAdapter(NonAPIFilter.class, new NonAPIFilterClassAdapter());
    }


    /**
     * Create a UserIntel object matching the save file intel.
     *
     * @return the UserIntel object.
     * @throws IOException if the save file is missing.
     */
    public UserIntel getUserIntel() throws IOException {
        if(fileIsEmpty()) return createEmptyUserIntel();

        String userIntel;
        try {
            userIntel = new String(Files.readAllBytes(filePath));
        } catch (IOException e) {
            throw new IOException("The save file is missing. We can't get or save your preferences.");
        }

        return gson.create().fromJson(userIntel, UserIntel.class);
    }


    /**
     * Add a new article in the favorites articles list.
     *
     * @param article : the article to add to the favorites.
     * @return {@code true} if the article was not already in the favorites and the updated favorites are saved.
     * {@code false} if the save failed, the code couldn't generate a UserIntel object from the saved files or the
     * article is already in the favorites.
     * @throws Exception if the save file is missing.
     */
    public boolean addFavorite(Article article) throws Exception {
        UserIntel userIntel = getUserIntel();

        if (userIntel == null) return false;

        if(!userIntel.addFavorite(article)) return false;
        return save(userIntel);
    }


    /**
     * Remove an article in the favorites articles list.
     *
     * @param article : the article to remove from the favorites.
     * @return {@code true} if the updated favorites are saved.
     * {@code false} if the save failed or the code couldn't generate a UserIntel object from the saved files
     * @throws Exception if the save file is missing.
     */
    public boolean removeFavorite(Article article) throws Exception {
        if (fileIsEmpty())
            throw new Exception("The save file seems to have a problem. Please contact the technical support.");

        UserIntel userIntel = getUserIntel();
        if (userIntel == null) return false;

        userIntel.removeFavorite(article);
        return save(userIntel);
    }


    /**
     * Changes the saved values of the last filters used, and update the date of last request with the date of the day.
     *
     * @param newAPIFilters : the list of APIFilters used in the last request (can be empty if none was used).
     * @param newNonAPIFilters : the list of NonAPIFilters used in the last request (can be empty if none was used).
     * @return {@code true} if the user intel was successfully saved.
     * {@code false} if the save failed or the code couldn't generate a UserIntel object from the saved files.
     * @throws Exception if the save file is missing or the stream of data was interrupted.
     */
    public boolean updateFiltersAndDateOfLastRequest(List<APIFilter> newAPIFilters, List<NonAPIFilter> newNonAPIFilters)
            throws Exception {
        UserIntel userIntel = getUserIntel();
        if (userIntel == null) return false;

        userIntel.updateFiltersUsed(newAPIFilters, newNonAPIFilters);
        userIntel.updateDateOfLastUsage(LocalDate.now());
        return save(userIntel);
    }


    /**
     * update the date of last request with the date of the day.
     *
     * @return {@code true} if the user intel was successfully saved.
     * {@code false} if the save failed or the code couldn't generate a UserIntel object from the saved files.
     * @throws Exception if the save file is missing or the stream of data was interrupted.
     */
    public boolean updateOnlyDateOfLastRequest() throws Exception {
        UserIntel userIntel = getUserIntel();
        if (userIntel == null) return false;

        userIntel.updateDateOfLastUsage(LocalDate.now());
        return save(userIntel);
    }


    private boolean save(UserIntel userIntel) throws Exception {
        try {
            String data = gson.create().toJson(userIntel);
            BufferedWriter writer = Files.newBufferedWriter(filePath);
            writer.write(data);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            throw new Exception("The save of your preferences has failed.");
        }
        return true;
    }

    private boolean fileIsEmpty() {
        File file = new File(String.valueOf(filePath));
        return file.length() == 0;
    }

    private UserIntel createEmptyUserIntel() {
        return new UserIntel(LocalDate.MIN, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public Path getFilePath() {
        return filePath;
    }
}
