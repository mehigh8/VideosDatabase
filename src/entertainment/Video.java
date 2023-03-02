package entertainment;

import user.User;

import java.util.List;

/**
 * Clasa folosita pentru stocarea datelor despre videoclipuri si lucrul cu acestea.
 */
public abstract class Video {
    private String title;
    private int year;
    private List<String> genres;
    private List<String> actors;

    // Campuri folosite pentru comparatoare
    private int timesInFavorites;
    private int timesInViewed;
    private Double rating;

    public Video(final String title, final int year, final List<String> genres,
                 final List<String> actors) {
        this.title = title;
        this.year = year;
        this.genres = genres;
        this.actors = actors;
        timesInFavorites = 0;
        timesInViewed = 0;
        rating = 0d;
    }

    public final String getTitle() {
        return title;
    }

    public final int getYear() {
        return year;
    }

    public final List<String> getGenres() {
        return genres;
    }

    public final List<String> getActors() {
        return actors;
    }

    public final int getTimesInFavorites() {
        return timesInFavorites;
    }

    public final int getTimesInViewed() {
        return timesInViewed;
    }

    public final Double getRating() {
        return rating;
    }

    public final void setRating(final Double rating) {
        this.rating = rating;
    }

    /**
     * Functie care actulizeaza de cate ori se afla videoclipul in listele de favorite ale
     * utilizatorilor
     * @param users lista de utilizatori
     */
    public final void updateFavorites(final List<User> users) {
        for (User user : users) {
            if (user.getFavorites() == null) {
                continue;
            }
            if (user.getFavorites().contains(this.getTitle())) {
                timesInFavorites++;
            }
        }
    }

    /**
     * Functie care actulizeaza de cate ori a fost vazut videoclipul de utilizatori
     * @param users lista de utilizatori
     */
    public final void updateViewed(final List<User> users) {
        for (User user : users) {
            if (user.getViewed().containsKey(title)) {
                timesInViewed += user.getViewed().get(title);
            }
        }
    }
}
