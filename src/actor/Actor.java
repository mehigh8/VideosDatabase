package actor;

import database.Database;
import entertainment.Movie;
import entertainment.Series;
import helper.Helper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Clasa folosita pentru stocarea datelor despre actori si lucrul cu acestea.
 */
public final class Actor {
    private String name;
    private String careerDescription;
    private List<String> filmography;
    private Map<ActorsAwards, Integer> awards;

    // Campuri folosite pentru comparatoare.
    private Double averageRating;
    private int numberOfAwards;
    private int containsFilters;

    public Actor(final String name, final String careerDescription, final List<String> filmography,
                 final Map<ActorsAwards, Integer> awards) {
        this.name = name;
        this.careerDescription = careerDescription;
        this.filmography = filmography;
        this.awards = awards;
        averageRating = 0d;
        numberOfAwards = 0;
        containsFilters = 0;
    }

    public String getName() {
        return name;
    }

    public List<String> getFilmography() {
        return filmography;
    }

    public Map<ActorsAwards, Integer> getAwards() {
        return awards;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public int getNumberOfAwards() {
        return numberOfAwards;
    }

    public int getContainsFilters() {
        return containsFilters;
    }

    /**
     * Functie care actualizeaza rating-ul mediu al actorului.
     * @param database baza de date in care sunt stocati actorii, utilizatorii, filmele si
     *                 serialele
     */
    public void updateAverageRating(final Database database) {
        // Numarul de filme/seriale care nu sunt in baza de date sau nu au primit niciun rating
        // inca.
        int nonRatedCount = 0;
        Double sum = 0d;
        for (String title : filmography) {
            Movie movie = database.getMovieByName(title);
            Double currRating;
            // Daca nu exista niciun film cu numele luat din filmografie inseamna ca poate fi
            // numele unui serial sau sa nu fie in baza de date.
            if (movie == null) {
                Series series = database.getSeriesByName(title);
                // Daca nu exista numele nici in lista de seriale inseamna ca filmul/serialul cu
                // acest nume nu se afla in baza de date.
                if (series == null) {
                    nonRatedCount++;
                    continue;
                }
                currRating = series.averageRating();
            } else {
                currRating = movie.averageRating();
            }
            if (currRating.compareTo(0d) == 0) {
                nonRatedCount++;
                continue;
            }
            sum = sum + currRating;
        }
        // In cazul in care toate filmele/serialele din filmografia actorului nu au rating atunci
        // actorul are rating-ul 0.
        if (filmography.size() - nonRatedCount == 0) {
            averageRating = 0d;
        } else {
            averageRating = sum / (filmography.size() - nonRatedCount);
        }
    }

    /**
     * Functie care actulizeaza numarul de premii ale actorului daca le are pe cele specificate
     * @param awardList lista de premii pe care trebuie sa le aiba actorul
     */
    public void updateAwards(final List<String> awardList) {
        numberOfAwards = 0;
        for (String award : awardList) {
            // Daca nu are unul dintre premii se considera ca are 0.
            if (!awards.containsKey(Helper.stringToAward(award))) {
                return;
            }
        }
        // Calculez numarul de premii ale actorului.
        for (Map.Entry<ActorsAwards, Integer> award : awards.entrySet()) {
            numberOfAwards += award.getValue();
        }
    }

    /**
     * Functia verifica daca actorul are in descriere toate cuvintele specificate
     * @param words lista de cuvinte ce trebuie sa apara in descriere
     */
    public void updateContainsFilters(final List<String> words) {
        // Creez un sir de caractere care sa contina descrierea actorului dar fara litere mari.
        StringBuilder lowerCase = new StringBuilder();
        for (int i = 0; i < careerDescription.length(); i++) {
            // Daca un caracter este semn de punctuatie se inlocuieste cu un spatiu.
            if (!Helper.isPunctuation(careerDescription.charAt(i))) {
                lowerCase.append(Character.toLowerCase(careerDescription.charAt(i)));
            } else {
                lowerCase.append(' ');
            }
        }
        // Creez o lista de cuvinte impartind sirul de caractere la fiecare spatiu.
        List<String> currWords = Arrays.asList(new String(lowerCase).split(" "));
        containsFilters = 0;
        // Caut fiecare cuvant din lista primita in lista obtinuta, daca nu exista se iese din
        // functie.
        for (String word : words) {
            if (!currWords.contains(word)) {
                return;
            }
        }
        containsFilters = 1;
    }
}
