package database;

import actor.Actor;
import entertainment.Movie;
import entertainment.Series;
import fileio.ActorInputData;
import fileio.MovieInputData;
import fileio.SerialInputData;
import fileio.UserInputData;
import user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Clasa folosita pentru stocarea tuturor actorilor, utilizatorilor, filmelor si serialelor.
 */
public final class Database {
    private List<Movie> movies;
    private List<Series> series;
    private List<User> users;
    private List<Actor> actors;

    public Database() {
        movies = new ArrayList<>();
        series = new ArrayList<>();
        users = new ArrayList<>();
        actors = new ArrayList<>();
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public List<Series> getSeries() {
        return series;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Actor> getActors() {
        return actors;
    }

    /**
     * Functie care stocheaza utilizatorii, actorii, filmele si serialele primite din input.
     * @param userInput utilizatorii primiti din input
     * @param actorInput actorii primiti din input
     * @param movieInput filmele primite din input
     * @param seriesInput serialele primite din input
     */
    public void load(final List<UserInputData> userInput, final List<ActorInputData> actorInput,
                     final List<MovieInputData> movieInput,
                     final List<SerialInputData> seriesInput) {
        for (UserInputData user : userInput) {
            users.add(new User(user.getUsername(), user.getSubscriptionType(),
                    user.getFavoriteMovies(), user.getHistory()));
        }
        for (ActorInputData actor : actorInput) {
            actors.add(new Actor(actor.getName(), actor.getCareerDescription(),
                    actor.getFilmography(), actor.getAwards()));
        }
        for (MovieInputData movie : movieInput) {
            movies.add(new Movie(movie.getTitle(), movie.getYear(), movie.getGenres(),
                    movie.getDuration(), movie.getCast()));
        }
        for (SerialInputData serial : seriesInput) {
            series.add(new Series(serial.getTitle(), serial.getYear(), serial.getGenres(),
                    serial.getSeasons(), serial.getCast(), serial.getNumberSeason()));
        }
    }

    /**
     * Functie care cauta in baza de date utilizatorul cu numele specificat
     * @param username numele utilizatorului cautat
     * @return utilizatorul cautat sau null daca nu exista
     */
    public User getUserByName(final String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Functie care cauta in baza de date un film cu titlul specificat
     * @param title titlul filmului cautat
     * @return filmul cautat sau null daca nu exista
     */
    public Movie getMovieByName(final String title) {
        for (Movie movie : movies) {
            if (movie.getTitle().equals(title)) {
                return movie;
            }
        }
        return null;
    }
    /**
     * Functie care cauta in baza de date un serial cu titlul specificat
     * @param title titlul serialului cautat
     * @return serialul cautat sau null daca nu exista
     */
    public Series getSeriesByName(final String title) {
        for (Series serial : series) {
            if (serial.getTitle().equals(title)) {
                return serial;
            }
        }
        return null;
    }

    /**
     * Functie care calculeaza de cate ori a fost vazut un videoclip de utilizatori
     * @param title titlul videoclipului
     * @return numarul de vizionari
     */
    public int timesViewed(final String title) {
        int times = 0;
        for (User user : users) {
            if (user.getViewed().containsKey(title)) {
                times += user.getViewed().get(title);
            }
        }
        return times;
    }

    /**
     * Functie care calculeaza popularitatea unui gen
     * @param genre genul a carui popularitate trebuie calculata
     * @return valoarea popularitatii
     */
    public int getPopularity(final String genre) {
        int popularity = 0;
        for (Movie movie : movies) {
            if (movie.getGenres().contains(genre)) {
                popularity += timesViewed(movie.getTitle());
            }
        }
        for (Series show : series) {
            if (show.getGenres().contains(genre)) {
                popularity += timesViewed(show.getTitle());
            }
        }
        return popularity;
    }

    /**
     * Functie care calculeaza de cate ori se afla un videoclip in listele de favorite ale
     * utilizatorilor
     * @param title titlul videoclipului
     * @return de cate ori apare videoclipul
     */
    public int timesFavorite(final String title) {
        int times = 0;
        for (User user : users) {
            if (user.getFavorites().contains(title)) {
                times++;
            }
        }
        return times;
    }
}
