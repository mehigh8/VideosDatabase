package action;

import actor.Actor;
import common.Constants;
import comparators.ActorAverageComparator;
import comparators.ActorAwardsComparator;
import comparators.ActorFilterComparator;
import comparators.UserRatingComparator;
import comparators.VideoDurationComparator;
import comparators.VideoFavoritesComparator;
import comparators.VideoRatingComparator;
import comparators.VideoViewedComparator;
import comparators.VideoRatingOnlyComparator;
import comparators.PairValueComparator;
import comparators.PairValueNameComparator;
import database.Database;
import entertainment.Genre;
import entertainment.Movie;
import entertainment.Series;
import entertainment.Video;
import fileio.ActionInputData;
import helper.Helper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import user.User;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.AbstractMap;

/**
 * Clasa care stocheaza actiunile ce trebuie executate, si le executa pe rand.
 */
public final class ActionManager {
    private List<Action> actions;

    public ActionManager() {
        actions = new ArrayList<>();
    }

    /**
     * Functie care stocheaza actiunile citite in ActionManager.
     * @param actionInput lista de actiuni citita
     */
    public void load(final List<ActionInputData> actionInput) {
        for (ActionInputData action : actionInput) {
            // Determin tipul de constructor pe care trebuie sa il folosesc in functie de
            // tipul actiunii.
            switch (action.getActionType()) {
                case "command":
                    actions.add(new Action(action.getActionId(),
                            action.getActionType(), action.getType(),
                            action.getUsername(), action.getTitle(),
                            action.getGrade(), action.getSeasonNumber()));
                    break;
                case "query":
                    actions.add(new Action(action.getActionId(),
                            action.getActionType(), action.getObjectType(),
                            action.getSortType(), action.getCriteria(),
                            action.getNumber(), action.getFilters()));
                    break;
                case "recommendation":
                    actions.add(new Action(action.getActionId(),
                            action.getActionType(), action.getType(),
                            action.getUsername(), action.getGenre()));
                    break;
                default:
                    System.out.println("Cannot find action");
            }
        }
    }

    /**
     * Functie care executa actiunea de tip comanda primita ca parametru.
     * @param database baza de date in care sunt stocati actorii, utilizatorii, filmele si
     *                 serialele
     * @param action comanda ce trebuie executata
     * @param obj JSONObject-ul ce va fi adaugat in JSONArray-ul folosit pentru afisare
     */
    private void runCommand(final Database database, final Action action, final JSONObject obj) {
        User user = database.getUserByName(action.getUsername());
        if (action.getType().equals("favorite")) {
            // Verific daca utilizatorul are deja filmul/serialul in lista de favorite.
            if (user.getFavorites().contains(action.getTitle())) {
                obj.put(Constants.MESSAGE, "error -> " + action.getTitle()
                        + " is already in favourite list");
            } else {
                // Verific daca a vazut filmul/serialul.
                if (user.getViewed().containsKey(action.getTitle())) {
                    user.addFavorites(action.getTitle());
                    obj.put(Constants.MESSAGE, "success -> " + action.getTitle()
                            + " was added as favourite");
                } else {
                    obj.put(Constants.MESSAGE, "error -> " + action.getTitle() + " is not seen");
                }
            }
        } else if (action.getType().equals("view")) {
            user.addViewed(action.getTitle());
            obj.put(Constants.MESSAGE, "success -> " + action.getTitle()
                    + " was viewed with total views of "
                    + user.getViewed().get(action.getTitle()));
        } else if (action.getType().equals("rating")) {
            // Verific daca a vazut filmul/serialul.
            if (user.getViewed().containsKey(action.getTitle())) {
                // Verific daca este vorba despre un film sau un serial.
                if (action.getSeasonNumber() == 0) {
                    // Verific daca a lasat deja rating filmului.
                    if (user.getRated().containsKey(action.getTitle())) {
                        obj.put(Constants.MESSAGE, "error -> " + action.getTitle()
                                + " has been already rated");
                    } else {
                        user.addRated(action.getTitle(), 0);
                        Movie mov = database.getMovieByName(action.getTitle());
                        mov.addRating(action.getRating());
                        obj.put(Constants.MESSAGE, "success -> " + action.getTitle()
                                + " was rated with " + action.getRating() + " by "
                                + action.getUsername());
                    }
                } else {
                    // Verific daca a lasat rating cel putin unui sezon al serialului.
                    if (user.getRated().containsKey(action.getTitle())) {
                        // Verific daca a lasat rating sezonului specificat.
                        if (user.getRated().get(action.getTitle()).
                                contains(action.getSeasonNumber())) {
                            obj.put(Constants.MESSAGE, "error -> " + action.getTitle()
                                    + " has been already rated");
                        } else {
                            user.addRated(action.getTitle(), action.getSeasonNumber());
                            Series series = database.getSeriesByName(action.getTitle());
                            series.addRating(action.getRating(), action.getSeasonNumber());
                            obj.put(Constants.MESSAGE, "success -> " + action.getTitle()
                                    + " was rated with " + action.getRating() + " by "
                                    + action.getUsername());
                        }
                    } else {
                        user.addRated(action.getTitle(), action.getSeasonNumber());
                        Series series = database.getSeriesByName(action.getTitle());
                        series.addRating(action.getRating(), action.getSeasonNumber());
                        obj.put(Constants.MESSAGE, "success -> " + action.getTitle()
                                + " was rated with " + action.getRating() + " by "
                                + action.getUsername());
                    }
                }
            } else {
                obj.put(Constants.MESSAGE, "error -> " + action.getTitle() + " is not seen");
            }
        }
    }

    /**
     * Functie care executa actiunea de tip interogare pentru actori primita ca parametru.
     * @param database baza de date in care sunt stocati actorii, utilizatorii, filmele si
     *                 serialele
     * @param action interogarea ce trebuie executata
     * @param obj JSONObject-ul ce va fi adaugat in JSONArray-ul folosit pentru afisare
     */
    public void runQueryActors(final Database database, final Action action,
                               final JSONObject obj) {
        List<Actor> actors = database.getActors();
        List<String> res = new ArrayList<>();
        if (action.getCriteria().equals("average")) {
            // Actualizez rating-ul actorilor.
            for (Actor actor : actors) {
                actor.updateAverageRating(database);
            }
            // Sortez actorii in functie de rating, respectiv alfabetic.
            actors.sort(new ActorAverageComparator());
            // Populez lista finala in functie de ordinea de sortare si de numarul de actori
            // ceruti in cadrul interogarii.
            if (action.getSortingType().equals("asc")) {
                for (int i = 0; i < actors.size(); i++) {
                    if (actors.get(i).getAverageRating() > 0) {
                        res.add(actors.get(i).getName());
                    }
                    if (res.size() == action.getQueryLimit()) {
                        break;
                    }
                }
            } else if (action.getSortingType().equals("desc")) {
                for (int i = 0; i < actors.size(); i++) {
                    if (actors.get(actors.size() - i - 1).getAverageRating() > 0) {
                        res.add(actors.get(actors.size() - i - 1).getName());
                    }
                    if (res.size() == action.getQueryLimit()) {
                        break;
                    }
                }
            }
        } else if (action.getCriteria().equals("awards")) {
            // Actualizez numarul de premii specificate ale fiecarui actor.
            for (Actor actor : actors) {
                actor.updateAwards(action.getFilters().get(Constants.AWARDS_NUM));
            }
            // Sortez actorii in functie de numarul de premii, respectiv alfabetic.
            actors.sort(new ActorAwardsComparator());
            // Populez lista finala cu toti actorii ce detin toate premiile mentionate in functie
            // de ordinea de sortare.
            if (action.getSortingType().equals("asc")) {
                for (int i = 0; i < actors.size(); i++) {
                    if (actors.get(i).getNumberOfAwards() > 0) {
                        res.add(actors.get(i).getName());
                    }
                }
            } else if (action.getSortingType().equals("desc")) {
                for (int i = 0; i < actors.size(); i++) {
                    if (actors.get(actors.size() - i - 1).getNumberOfAwards() > 0) {
                        res.add(actors.get(actors.size() - i - 1).getName());
                    }
                }
            }
        } else if (action.getCriteria().equals("filter_description")) {
            // Verific care actori contin in descriere intreaga lista de cuvinte primita.
            for (Actor actor : actors) {
                actor.updateContainsFilters(action.getFilters().get(Constants.WORDS_NUM));
            }
            actors.sort(new ActorFilterComparator());
            if (action.getSortingType().equals("asc")) {
                for (int i = 0; i < actors.size(); i++) {
                    if (actors.get(i).getContainsFilters() == 1) {
                        res.add(actors.get(i).getName());
                    }
                }
            } else if (action.getSortingType().equals("desc")) {
                for (int i = 0; i < actors.size(); i++) {
                    if (actors.get(actors.size() - i - 1).getContainsFilters() == 1) {
                        res.add(actors.get(actors.size() - i - 1).getName());
                    }
                }
            }
        }
        obj.put(Constants.MESSAGE, "Query result: " + res);
    }

    /**
     * Functie care executa actiunea de tip interogare pentru utilizatori primita ca parametru.
     * @param database baza de date in care sunt stocati actorii, utilizatorii, filmele si
     *                 serialele
     * @param action interogarea ce trebuie executata
     * @param obj JSONObject-ul ce va fi adaugat in JSONArray-ul folosit pentru afisare
     */
    public void runQueryUsers(final Database database, final Action action, final JSONObject obj) {
        List<User> users = database.getUsers();
        List<String> res = new ArrayList<>();
        if (action.getCriteria().equals("num_ratings")) {
            // Sortez utilizatorii in functie de numarul de rating-uri lasate, respectiv
            // lexicografic.
            users.sort(new UserRatingComparator());
            if (action.getSortingType().equals("asc")) {
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getRatingCount() > 0) {
                        res.add(users.get(i).getUsername());
                    }
                    if (res.size() == action.getQueryLimit()) {
                        break;
                    }
                }
            } else if (action.getSortingType().equals("desc")) {
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(users.size() - i - 1).getRatingCount() > 0) {
                        res.add(users.get(users.size() - i - 1).getUsername());
                    }
                    if (res.size() == action.getQueryLimit()) {
                        break;
                    }
                }
            }
        }
        obj.put(Constants.MESSAGE, "Query result: " + res);
    }
    /**
     * Functie care executa actiunea de tip interogare pentru filme primita ca parametru.
     * @param database baza de date in care sunt stocati actorii, utilizatorii, filmele si
     *                 serialele
     * @param action interogarea ce trebuie executata
     * @param obj JSONObject-ul ce va fi adaugat in JSONArray-ul folosit pentru afisare
     */
    public void runQueryMovies(final Database database, final Action action,
                               final JSONObject obj) {
        List<Movie> movies = database.getMovies();
        // Creez o lista noua de filme care sa fie filtrata in functie de anul si genul primite.
        List<Movie> filtered = new ArrayList<>();
        for (Movie movie : movies) {
            Movie mov = new Movie(movie.getTitle(), movie.getYear(), movie.getGenres(),
                    movie.getDuration(), movie.getActors());
            mov.setRatings(movie.getRatings());
            filtered.add(mov);
        }
        if (action.getFilters().get(Constants.YEAR_NUM).get(0) != null) {
            filtered.removeIf((movie) -> movie.getYear()
                    != Integer.parseInt(action.getFilters().get(Constants.YEAR_NUM).get(0)));
        }
        if (action.getFilters().get(Constants.GENRE_NUM).get(0) != null) {
            filtered.removeIf((movie) -> !(movie.getGenres().
                    contains(action.getFilters().get(Constants.GENRE_NUM).get(0))));
        }
        List<String> res = new ArrayList<>();
        if (action.getCriteria().equals("favorite")) {
            // Actualizez de cate ori apare fiecare film in listele de favorite ale utilizatorilor.
            for (Movie movie : filtered) {
                movie.updateFavorites(database.getUsers());
            }
            // Sortez filmele in functie de cate ori apar in listele de favorite, respectiv
            // alfabetic.
            filtered.sort(new VideoFavoritesComparator());
            if (action.getSortingType().equals("asc")) {
                for (int i = 0; i < filtered.size(); i++) {
                    if (filtered.get(i).getTimesInFavorites() > 0) {
                        res.add(filtered.get(i).getTitle());
                    }
                    if (res.size() == action.getQueryLimit()) {
                        break;
                    }
                }
            } else if (action.getSortingType().equals("desc")) {
                for (int i = 0; i < filtered.size(); i++) {
                    if (filtered.get(filtered.size() - i - 1).getTimesInFavorites() > 0) {
                        res.add(filtered.get(filtered.size() - i - 1).getTitle());
                    }
                    if (res.size() == action.getQueryLimit()) {
                        break;
                    }
                }
            }
        } else if (action.getCriteria().equals("ratings")) {
            // Sortez filmele in functie de rating, respectiv alfabetic.
            filtered.sort(new VideoRatingComparator());
            if (action.getSortingType().equals("asc")) {
                for (int i = 0; i < filtered.size(); i++) {
                    if (filtered.get(i).averageRating() > 0) {
                        res.add(filtered.get(i).getTitle());
                    }
                    if (res.size() == action.getQueryLimit()) {
                        break;
                    }
                }
            } else if (action.getSortingType().equals("desc")) {
                for (int i = 0; i < filtered.size(); i++) {
                    if (filtered.get(filtered.size() - i - 1).averageRating() > 0) {
                        res.add(filtered.get(filtered.size() - i - 1).getTitle());
                    }
                    if (res.size() == action.getQueryLimit()) {
                        break;
                    }
                }
            }
        } else if (action.getCriteria().equals("longest")) {
            // Sortez filmele in functie de durata, respectiv alfabetic.
            filtered.sort(new VideoDurationComparator());
            if (action.getSortingType().equals("asc")) {
                for (int i = 0; i < filtered.size(); i++) {
                    res.add(filtered.get(i).getTitle());
                    if (res.size() == action.getQueryLimit()) {
                        break;
                    }
                }
            } else if (action.getSortingType().equals("desc")) {
                for (int i = 0; i < filtered.size(); i++) {
                    res.add(filtered.get(filtered.size() - i - 1).getTitle());
                    if (res.size() == action.getQueryLimit()) {
                        break;
                    }
                }
            }
        } else if (action.getCriteria().equals("most_viewed")) {
            // Actualizez de cate ori a fost vizionat fiecare film.
            for (Movie movie : filtered) {
                movie.updateViewed(database.getUsers());
            }
            // Sortez filmele in functie de cate ori au fost vizionate, respectiv alfabetic.
            filtered.sort(new VideoViewedComparator());
            if (action.getSortingType().equals("asc")) {
                for (int i = 0; i < filtered.size(); i++) {
                    if (filtered.get(i).getTimesInViewed() > 0) {
                        res.add(filtered.get(i).getTitle());
                    }
                    if (res.size() == action.getQueryLimit()) {
                        break;
                    }
                }
            } else if (action.getSortingType().equals("desc")) {
                for (int i = 0; i < filtered.size(); i++) {
                    if (filtered.get(filtered.size() - i - 1).getTimesInViewed() > 0) {
                        res.add(filtered.get(filtered.size() - i - 1).getTitle());
                    }
                    if (res.size() == action.getQueryLimit()) {
                        break;
                    }
                }
            }
        }
        obj.put(Constants.MESSAGE, "Query result: " + res);
    }
    /**
     * Functie care executa actiunea de tip interogare pentru seriale primita ca parametru. Este
     * implementata in mod analog cu cea pentru filme.
     * @param database baza de date in care sunt stocati actorii, utilizatorii, filmele si
     *                 serialele
     * @param action interogarea ce trebuie executata
     * @param obj JSONObject-ul ce va fi adaugat in JSONArray-ul folosit pentru afisare
     */
    public void runQueryShows(final Database database, final Action action, final JSONObject obj) {
        List<Series> shows = database.getSeries();
        List<Series> filtered = new ArrayList<>();
        for (Series series : shows) {
            filtered.add(new Series(series.getTitle(), series.getYear(), series.getGenres(),
                    series.getSeasons(), series.getActors(), series.getSeasonCount()));
        }
        if (action.getFilters().get(Constants.YEAR_NUM).get(0) != null) {
            filtered.removeIf((series) -> series.getYear()
                    != Integer.parseInt(action.getFilters().get(Constants.YEAR_NUM).get(0)));
        }
        if (action.getFilters().get(Constants.GENRE_NUM).get(0) != null) {
            filtered.removeIf((series) -> !(series.getGenres().
                    contains(action.getFilters().get(Constants.GENRE_NUM).get(0))));
        }
        List<String> res = new ArrayList<>();
        if (action.getCriteria().equals("favorite")) {
            for (Series series : filtered) {
                series.updateFavorites(database.getUsers());
            }
            filtered.sort(new VideoFavoritesComparator());
            if (action.getSortingType().equals("asc")) {
                for (int i = 0; i < filtered.size(); i++) {
                    if (filtered.get(i).getTimesInFavorites() > 0) {
                        res.add(filtered.get(i).getTitle());
                    }
                    if (res.size() == action.getQueryLimit()) {
                        break;
                    }
                }
            } else if (action.getSortingType().equals("desc")) {
                for (int i = 0; i < filtered.size(); i++) {
                    if (filtered.get(filtered.size() - i - 1).getTimesInFavorites() > 0) {
                        res.add(filtered.get(filtered.size() - i - 1).getTitle());
                    }
                    if (res.size() == action.getQueryLimit()) {
                        break;
                    }
                }
            }
        } else if (action.getCriteria().equals("ratings")) {
            filtered.sort(new VideoRatingComparator());
            if (action.getSortingType().equals("asc")) {
                for (int i = 0; i < filtered.size(); i++) {
                    if (filtered.get(i).averageRating() > 0) {
                        res.add(filtered.get(i).getTitle());
                    }
                    if (res.size() == action.getQueryLimit()) {
                        break;
                    }
                }
            } else if (action.getSortingType().equals("desc")) {
                for (int i = 0; i < filtered.size(); i++) {
                    if (filtered.get(filtered.size() - i - 1).averageRating() > 0) {
                        res.add(filtered.get(filtered.size() - i - 1).getTitle());
                    }
                    if (res.size() == action.getQueryLimit()) {
                        break;
                    }
                }
            }
        } else if (action.getCriteria().equals("longest")) {
            filtered.sort(new VideoDurationComparator());
            if (action.getSortingType().equals("asc")) {
                for (int i = 0; i < filtered.size(); i++) {
                    res.add(filtered.get(i).getTitle());
                    if (res.size() == action.getQueryLimit()) {
                        break;
                    }
                }
            } else if (action.getSortingType().equals("desc")) {
                for (int i = 0; i < filtered.size(); i++) {
                    res.add(filtered.get(filtered.size() - i - 1).getTitle());
                    if (res.size() == action.getQueryLimit()) {
                        break;
                    }
                }
            }
        } else if (action.getCriteria().equals("most_viewed")) {
            for (Series series : filtered) {
                series.updateViewed(database.getUsers());
            }
            filtered.sort(new VideoViewedComparator());
            if (action.getSortingType().equals("asc")) {
                for (int i = 0; i < filtered.size(); i++) {
                    if (filtered.get(i).getTimesInViewed() > 0) {
                        res.add(filtered.get(i).getTitle());
                    }
                    if (res.size() == action.getQueryLimit()) {
                        break;
                    }
                }
            } else if (action.getSortingType().equals("desc")) {
                for (int i = 0; i < filtered.size(); i++) {
                    if (filtered.get(filtered.size() - i - 1).getTimesInViewed() > 0) {
                        res.add(filtered.get(filtered.size() - i - 1).getTitle());
                    }
                    if (res.size() == action.getQueryLimit()) {
                        break;
                    }
                }
            }
        }
        obj.put(Constants.MESSAGE, "Query result: " + res);
    }
    /**
     * Functie care executa actiunea de tip interogare primita ca parametru.
     * @param database baza de date in care sunt stocati actorii, utilizatorii, filmele si
     *                 serialele
     * @param action interogarea ce trebuie executata
     * @param obj JSONObject-ul ce va fi adaugat in JSONArray-ul folosit pentru afisare
     */
    public void runQuery(final Database database, final Action action, final JSONObject obj) {
        // In functie de tipul de interogare apelez functia corespunzatoare.
        if (action.getQueryType().equals("actors")) {
            runQueryActors(database, action, obj);
        } else if (action.getQueryType().equals("users")) {
            runQueryUsers(database, action, obj);
        } else if (action.getQueryType().equals("movies")) {
            runQueryMovies(database, action, obj);
        } else if (action.getQueryType().equals("shows")) {
            runQueryShows(database, action, obj);
        }
    }

    /**
     * Functie care executa actiunea de tip recomandare standard primita ca parametru.
     * @param database baza de date in care sunt stocati actorii, utilizatorii, filmele si
     *                 serialele
     * @param action recomandarea ce trebuie executata
     * @param obj JSONObject-ul ce va fi adaugat in JSONArray-ul folosit pentru afisare
     */
    public void runRecommendationStandard(final Database database, final Action action,
                                          final JSONObject obj) {
        User user = database.getUserByName(action.getUsername());
        String res = null;
        List<Movie> movies = database.getMovies();
        List<Series> shows = database.getSeries();
        // Caut primul film din baza de date care nu a fost vazut de utilizator.
        for (Movie movie : movies) {
            if (!user.getViewed().containsKey(movie.getTitle())) {
                res = movie.getTitle();
                break;
            }
        }
        // Daca nu a fost gasit niciun film, caut in lista de seriale.
        if (res == null) {
            for (Series series : shows) {
                if (!user.getViewed().containsKey(series.getTitle())) {
                    res = series.getTitle();
                    break;
                }
            }
        }
        // Verific daca a fost gasit un rezultat.
        if (res == null) {
            obj.put(Constants.MESSAGE, "StandardRecommendation cannot be applied!");
        } else {
            obj.put(Constants.MESSAGE, "StandardRecommendation result: " + res);
        }
    }

    /**
     * Functie care executa actiunea de tip recomandare best_unseen primita ca parametru.
     * @param database baza de date in care sunt stocati actorii, utilizatorii, filmele si
     *                 serialele
     * @param action recomandarea ce trebuie executata
     * @param obj JSONObject-ul ce va fi adaugat in JSONArray-ul folosit pentru afisare
     */
    public void runRecommendationBestUnseen(final Database database, final Action action,
                                          final JSONObject obj) {
        User user = database.getUserByName(action.getUsername());
        String res = null;
        List<Movie> movies = database.getMovies();
        List<Series> shows = database.getSeries();

        List<Video> videos = new ArrayList<>();
        for (Movie movie : movies) {
            Movie mov = new Movie(movie.getTitle(), movie.getYear(), movie.getGenres(),
                    movie.getDuration(), movie.getActors());
            mov.setRating(movie.averageRating());
            videos.add(mov);
        }
        for (Series series : shows) {
            Series show = new Series(series.getTitle(), series.getYear(), series.getGenres(),
                    series.getSeasons(), series.getActors(), series.getSeasonCount());
            show.setRating(series.averageRating());
            videos.add(show);
        }
        // Sortez videoclipurile in functie de rating.
        videos.sort(new VideoRatingOnlyComparator());
        // Caut primul videoclip pe care nu l-a vazut utilizatorul.
        for (Video video : videos) {
            if (!user.getViewed().containsKey(video.getTitle())) {
                res = video.getTitle();
                break;
            }
        }
        // Verific daca a fost gasit un rezultat.
        if (res == null) {
            obj.put(Constants.MESSAGE, "BestRatedUnseenRecommendation cannot be applied!");
        } else {
            obj.put(Constants.MESSAGE, "BestRatedUnseenRecommendation result: " + res);
        }
    }

    /**
     * Functie care executa actiunea de tip recomandare popular primita ca parametru.
     * @param database baza de date in care sunt stocati actorii, utilizatorii, filmele si
     *                 serialele
     * @param action recomandarea ce trebuie executata
     * @param obj JSONObject-ul ce va fi adaugat in JSONArray-ul folosit pentru afisare
     */
    public void runRecommendationPopular(final Database database, final Action action,
                                          final JSONObject obj) {
        User user = database.getUserByName(action.getUsername());
        String res = null;
        List<Movie> movies = database.getMovies();
        List<Series> shows = database.getSeries();
        // Verific daca utilizatorul are abonamentul corespunzator.
        if (user.getSubscription().equals("BASIC")) {
            obj.put(Constants.MESSAGE, "PopularRecommendation cannot be applied!");
        } else if (user.getSubscription().equals("PREMIUM")) {
            // Creez o lista de perechi (gen, popularitate).
            List<Map.Entry<String, Integer>> popularities = new ArrayList<>();
            Genre[] genres = Genre.values();
            for (Genre genre : genres) {
                String genreString = Helper.genreToString(genre);
                Map.Entry<String, Integer> pair = new AbstractMap.
                        SimpleEntry<>(genreString, database.getPopularity(genreString));
                popularities.add(pair);
            }
            // Sortez lista de perechi in functie de popularitate.
            popularities.sort(new PairValueComparator());
            // Caut primul film/serial pe care utilizatorul nu l-a vazut din cel mai popular
            // gen. In cazul in care nu este gasit niciun rezultat in primul gen trec la
            // urmatorul. Continui in mod analog pana gasesc un rezultat sau parcurg toate
            // genurile.
            for (Map.Entry<String, Integer> genre : popularities) {
                for (Movie movie : movies) {
                    if (movie.getGenres().contains(genre.getKey())
                            && !user.getViewed().containsKey(movie.getTitle())) {
                        res = movie.getTitle();
                        break;
                    }
                }
                if (res != null) {
                    break;
                }
                for (Series series : shows) {
                    if (series.getGenres().contains(genre.getKey())
                            && !user.getViewed().containsKey(series.getTitle())) {
                        res = series.getTitle();
                        break;
                    }
                }
                if (res != null) {
                    break;
                }
            }
        }
        // Verific daca a fost gasit un rezultat.
        if (res == null) {
            obj.put(Constants.MESSAGE, "PopularRecommendation cannot be applied!");
        } else {
            obj.put(Constants.MESSAGE, "PopularRecommendation result: " + res);
        }
    }

    /**
     * Functie care executa actiunea de tip recomandare favorite primita ca parametru.
     * @param database baza de date in care sunt stocati actorii, utilizatorii, filmele si
     *                 serialele
     * @param action recomandarea ce trebuie executata
     * @param obj JSONObject-ul ce va fi adaugat in JSONArray-ul folosit pentru afisare
     */
    public void runRecommendationFavorite(final Database database, final Action action,
                                          final JSONObject obj) {
        User user = database.getUserByName(action.getUsername());
        String res = null;
        List<Movie> movies = database.getMovies();
        List<Series> shows = database.getSeries();

        if (user.getSubscription().equals("BASIC")) {
            obj.put(Constants.MESSAGE, "FavoriteRecommendation cannot be applied!");
        } else if (user.getSubscription().equals("PREMIUM")) {
            // Creez o lista de perechi (videoclip, numar de ori in favorite).
            List<Map.Entry<String, Integer>> favorites = new ArrayList<>();
            for (Movie movie : movies) {
                Map.Entry<String, Integer> pair
                        = new AbstractMap.SimpleEntry<>(movie.getTitle(),
                        database.timesFavorite(movie.getTitle()));
                favorites.add(pair);
            }
            for (Series series : shows) {
                Map.Entry<String, Integer> pair
                        = new AbstractMap.SimpleEntry<>(series.getTitle(),
                        database.timesFavorite(series.getTitle()));
                favorites.add(pair);
            }
            // Sortez lista in functie de numarul de ori in listele de favorite ale
            // utilizatorilor.
            favorites.sort(new PairValueComparator());
            // Caut primul videoclip pe care utilizatorul nu l-a vazut.
            for (Map.Entry<String, Integer> video : favorites) {
                if (video.getValue() > 0) {
                    if (!user.getViewed().containsKey(video.getKey())) {
                        res = video.getKey();
                        break;
                    }
                }
            }
        }
        // Verific daca a fost gasit un rezultat.
        if (res == null) {
            obj.put(Constants.MESSAGE, "FavoriteRecommendation cannot be applied!");
        } else {
            obj.put(Constants.MESSAGE, "FavoriteRecommendation result: " + res);
        }
    }

    /**
     * Functie care executa actiunea de tip recomandare search primita ca parametru.
     * @param database baza de date in care sunt stocati actorii, utilizatorii, filmele si
     *                 serialele
     * @param action recomandarea ce trebuie executata
     * @param obj JSONObject-ul ce va fi adaugat in JSONArray-ul folosit pentru afisare
     */
    public void runRecommendationSearch(final Database database, final Action action,
                                          final JSONObject obj) {
        User user = database.getUserByName(action.getUsername());
        List<Movie> movies = database.getMovies();
        List<Series> shows = database.getSeries();

        if (user.getSubscription().equals("BASIC")) {
            obj.put(Constants.MESSAGE, "SearchRecommendation cannot be applied!");
        } else if (user.getSubscription().equals("PREMIUM")) {
            // Creez o lista de perechi (videoclip, rating) in care adaug videoclipurile
            // care au genul cerut.
            List<Map.Entry<String, Double>> videos = new ArrayList<>();
            for (Movie movie : movies) {
                if (movie.getGenres().contains(action.getGenre())) {
                    Map.Entry<String, Double> pair = new AbstractMap.
                            SimpleEntry<>(movie.getTitle(), movie.averageRating());
                    videos.add(pair);
                }
            }
            for (Series series : shows) {
                if (series.getGenres().contains(action.getGenre())) {
                    Map.Entry<String, Double> pair = new AbstractMap.
                            SimpleEntry<>(series.getTitle(), series.averageRating());
                    videos.add(pair);
                }
            }
            // Scot videoclipurile vazute de utilizator.
            videos.removeIf((video) -> user.getViewed().containsKey(video.getKey()));
            // Sortez lista in functie de rating, respectiv alfabetic.
            videos.sort(new PairValueNameComparator());
            List<String> result = new ArrayList<>();
            for (Map.Entry<String, Double> video : videos) {
                result.add(video.getKey());
            }
            // Verific daca se afla videoclipuri in rezultat.
            if (videos.isEmpty()) {
                obj.put(Constants.MESSAGE, "SearchRecommendation cannot be applied!");
            } else {
                obj.put(Constants.MESSAGE, "SearchRecommendation result: " + result);
            }
        }
    }

    /**
     * Functie care executa actiunea de tip recomandare primita ca parametru.
     * @param database baza de date in care sunt stocati actorii, utilizatorii, filmele si
     *                 serialele
     * @param action recomandarea ce trebuie executata
     * @param obj JSONObject-ul ce va fi adaugat in JSONArray-ul folosit pentru afisare
     */
    public void runRecommendation(final Database database, final Action action,
                                  final JSONObject obj) {
        // Apelez functia corespunzatoare in functie de tipul recomandarii.
        if (action.getType().equals("standard")) {
            runRecommendationStandard(database, action, obj);
        } else if (action.getType().equals("best_unseen")) {
            runRecommendationBestUnseen(database, action, obj);
        } else if (action.getType().equals("popular")) {
            runRecommendationPopular(database, action, obj);
        } else if (action.getType().equals("favorite")) {
            runRecommendationFavorite(database, action, obj);
        } else if (action.getType().equals("search")) {
            runRecommendationSearch(database, action, obj);
        }
    }

    /**
     * Functie care executa actiunile in ordinea primita.
     * @param database baza de date in care sunt stocati actorii, utilizatorii, filmele si
     *                 serialele
     * @param arrayResult JSONArray-ul care este folosit pentru output
     */
    public void run(final Database database, final JSONArray arrayResult) {
        Action action = actions.get(0);
        JSONObject obj = new JSONObject();
        obj.put(Constants.ID_STRING, action.getId());
        // Apelez functia corespunzatoare in functie de tipul comenzii.
        if (action.getCategory().equals("command")) {
            runCommand(database, action, obj);
        } else if (action.getCategory().equals("query")) {
            runQuery(database, action, obj);
        } else if (action.getCategory().equals("recommendation")) {
            runRecommendation(database, action, obj);
        }

        arrayResult.add(obj);
        // Scot din lista actiunea executata.
        actions.remove(0);
        // Daca mai sunt actiuni ramase reapelez functia.
        if (actions.size() > 0) {
            run(database, arrayResult);
        }
    }
}
