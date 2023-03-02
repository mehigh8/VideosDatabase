package entertainment;

import java.util.ArrayList;
import java.util.List;

/**
 * Clasa folosita pentru stocarea datelor despre filme si lucrul cu acestea.
 */
public final class Movie extends Video {
    private int duration;
    private List<Double> ratings;

    public Movie(final String title, final int year, final List<String> genres, final int duration,
                 final List<String> actors) {
        super(title, year, genres, actors);
        this.duration = duration;
        ratings = new ArrayList<>();
    }

    public int getDuration() {
        return duration;
    }

    public List<Double> getRatings() {
        return ratings;
    }

    public void setRatings(final List<Double> ratings) {
        this.ratings = ratings;
    }

    /**
     * Functie care adauga un rating in lista
     * @param rating rating-ul ce trebuie adaugat
     */
    public void addRating(final Double rating) {
        ratings.add(rating);
    }

    /**
     * Functie care calculeaza rating-ul mediu al fillmului
     * @return rating-ul mediu
     */
    public Double averageRating() {
        Double sum = 0d;
        for (Double rating : ratings) {
            sum = sum + rating;
        }
        // Daca filmul nu a primit rating-uri inca, se considera ca are rating-ul 0.
        if (ratings.size() == 0) {
            return 0d;
        }
        return sum / ratings.size();
    }
}
