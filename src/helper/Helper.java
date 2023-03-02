package helper;

import actor.ActorsAwards;
import entertainment.Genre;

/**
 * Clasa helper folosita pentru a implementa functii ajutatoare.
 */
public final class Helper {
    private Helper() {
    }

    /**
     * Functie care transforma un String intr-un ActorAwards
     * @param string string-ul ce trebuie transformat
     * @return ActorAwards-ul corespunzator, sau null daca nu este gasit
     */
    public static ActorsAwards stringToAward(final String string) {
        switch (string) {
            case "BEST_PERFORMANCE":
                return ActorsAwards.BEST_PERFORMANCE;
            case "BEST_DIRECTOR":
                return ActorsAwards.BEST_DIRECTOR;
            case "PEOPLE_CHOICE_AWARD":
                return ActorsAwards.PEOPLE_CHOICE_AWARD;
            case "BEST_SUPPORTING_ACTOR":
                return ActorsAwards.BEST_SUPPORTING_ACTOR;
            case "BEST_SCREENPLAY":
                return ActorsAwards.BEST_SCREENPLAY;
            default:
                return null;
        }
    }

    /**
     * Functie care transforma un Genre intr-un String
     * @param genre genul ce trebuie transformat
     * @return String-ul corespunzator, sau null daca nu este gasit
     */
    public static String genreToString(final Genre genre) {
        switch (genre) {
            case TV_MOVIE:
                return "TV Movie";
            case DRAMA:
                return "Drama";
            case FANTASY:
                return "Fantasy";
            case COMEDY:
                return "Comedy";
            case FAMILY:
                return "Family";
            case WAR:
                return "War";
            case SCI_FI_FANTASY:
                return "Sci-Fi & Fantasy";
            case CRIME:
                return "Crime";
            case SCIENCE_FICTION:
                return "Science Fiction";
            case ACTION:
                return "Action";
            case HORROR:
                return "Horror";
            case MYSTERY:
                return "Mystery";
            case WESTERN:
                return "Western";
            case ADVENTURE:
                return "Adventure";
            case ACTION_ADVENTURE:
                return "Action & Adventure";
            case ROMANCE:
                return "Romance";
            case THRILLER:
                return "Thriller";
            case KIDS:
                return "Kids";
            case HISTORY:
                return "History";
            default:
                return null;
        }
    }

    /**
     * Functie care verifica daca un caracter este semn de punctuatie
     * @param c caracterul care trebuie testat
     * @return true daca este semn de punctuatie, false in caz contrar
     */
    public static boolean isPunctuation(final char c) {
        return c == '.' || c == ',' || c == '!' || c == '?' || c == '-'
                || c == ':' || c == ';' || c == '(' || c == ')';
    }
}
