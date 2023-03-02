package entertainment;

import java.util.List;

/**
 * Clasa folosita pentru stocarea datelor despre seriale si lucrul cu acestea.
 */
public final class Series extends Video {
    private int seasonCount;
    private List<Season> seasons;

    public Series(final String title, final int year, final List<String> genres,
                  final List<Season> seasons, final List<String> actors, final int seasonCount) {
        super(title, year, genres, actors);
        this.seasons = seasons;
        this.seasonCount = seasonCount;
    }

    public int getSeasonCount() {
        return seasonCount;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    /**
     * Functie care adauga un rating unui sezon al serialului
     * @param rating rating-ul ce trebuie adaugat
     * @param seasonNumber numarul sezonului
     */
    public void addRating(final Double rating, final int seasonNumber) {
        Season season = seasons.get(seasonNumber - 1);
        List<Double> ratings = season.getRatings();
        ratings.add(rating);
    }

    /**
     * Functie care calculeaza rating-ul mediu al intregului serial
     * @return rating-ul mediu
     */
    public Double averageRating() {
        Double sum = 0d;
        // Calculez rating-ul mediu pentru fiecare sezon si il adaug intr-o suma.
        for (Season season : seasons) {
            List<Double> ratings;
            // Daca un sezon nu are rating-uri se poate sari incat rating-ul mediu este 0, deci
            // nu afecteaza suma totala.
            if (season.getRatings().size() == 0) {
                continue;
            }
            ratings = season.getRatings();
            Double currSum = 0d;
            for (Double rating : ratings) {
                currSum = currSum + rating;
            }
            sum = sum + currSum / ratings.size();
        }
        return sum / seasonCount;
    }

    /**
     * Functie care calculeaza durata intregului serial
     * @return durata intregului serial
     */
    public int getDuration() {
        int duration = 0;
        for (Season season : seasons) {
            duration += season.getDuration();
        }
        return duration;
    }
}
