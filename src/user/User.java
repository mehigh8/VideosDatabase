package user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clasa folosita pentru stocarea datelor despre utilizatori si lucrul cu acestea.
 */
public final class User {
    private String username;
    private String subscription;
    private List<String> favorites;
    private Map<String, Integer> viewed;
    private int ratingCount;
    private Map<String, List<Integer>> rated;

    public User(final String username, final String subscription, final List<String> favorites,
                final Map<String, Integer> viewed) {
        this.username = username;
        this.subscription = subscription;
        this.favorites = favorites;
        this.viewed = viewed;
        ratingCount = 0;
        rated = new HashMap<>();
    }

    public String getUsername() {
        return username;
    }

    public String getSubscription() {
        return subscription;
    }

    public List<String> getFavorites() {
        return favorites;
    }

    public Map<String, Integer> getViewed() {
        return viewed;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public Map<String, List<Integer>> getRated() {
        return rated;
    }

    /**
     * Functie care adauga un videoclip in lista de favorite
     * @param video titlul videoclipului
     */
    public void addFavorites(final String video) {
        favorites.add(video);
    }

    /**
     * Functie care adauga un videoclip in lista de videoclipuri vizionate, sau incrementeaza
     * numarul de vizionari daca exista deja videoclipul in lista
     * @param video titlul videoclipului
     */
    public void addViewed(final String video) {
        if (viewed.containsKey(video)) {
            Integer views = viewed.get(video);
            viewed.remove(video);
            viewed.put(video, views + 1);
        } else {
            viewed.put(video, 1);
        }
    }

    /**
     * Functie care adauga un videoclip in lista de videoclipuri la care acest utilizator a dat
     * rating si numarul sezonului in cazul in care videoclipul este un serial
     * @param video titlul videoclipului
     * @param season numarul sezonului, va fi 0 in cazul filmelor
     */
    public void addRated(final String video, final Integer season) {
        if (season == 0) {
            rated.put(video, null);
        } else {
            // Daca utilizatorul a mai dat rating-uri la alte sezoane ale serialului se adauga
            // numarul sezonului in lista de sezoane care au primit rating.
            if (rated.containsKey(video)) {
                List<Integer> seasons = rated.get(video);
                seasons.add(season);
                rated.remove(video);
                rated.put(video, seasons);
            } else {
                List<Integer> seasons = new ArrayList<>();
                seasons.add(season);
                rated.put(video, seasons);
            }
        }
        ratingCount++;
    }
}
