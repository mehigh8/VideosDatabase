package action;

import java.util.List;

/**
 * Clasa folosita pentru stocarea datelor despre actiuni.
 */
public final class Action {
    // Id-ul actiunii
    private int id;
    // Categoria actiunii
    private String category;
    // Tipul comenzii sau a recomandarii
    private String type;
    // Utilizatorul care executa actiunea
    private String username;
    // Tipul interogarii (actori, utilizatori, filme, seriale)
    private String queryType;
    // Modul de sortare
    private String sortingType;
    // Criteriul interogarii (average, awards, filter_description, etc.)
    private String criteria;
    // Titlul videoclipului
    private String title;
    // Genul cautat
    private String genre;
    // Numarul de rezultate cerute in urma interogarii
    private int queryLimit;
    // Rating-ul care este lasat
    private double rating;
    // Numarul sezonului
    private int seasonNumber;
    // Lista de filtre: anul videoclipului, genul videoclipului, cuvinte in descrierea unui actor,
    // premiile unui actor
    private List<List<String>> filters;

    // Constructor pentru comenzi
    public Action(final int id, final String category, final String commandType,
                  final String username, final String title, final double rating,
                  final int seasonNumber) {
        this.id = id;
        this.category = category;
        this.type = commandType;
        this.username = username;
        queryType = null;
        sortingType = null;
        criteria = null;
        genre = null;
        queryLimit = 0;
        this.title = title;
        this.rating = rating;
        this.seasonNumber = seasonNumber;
        filters = null;
    }

    // Constructor pentru interogari
    public Action(final int id, final String category, final String queryType,
                  final String sortingType, final String criteria, final int queryLimit,
                  final List<List<String>> filters) {
        this.id = id;
        this.category = category;
        type = null;
        username = null;
        this.queryType = queryType;
        this.sortingType = sortingType;
        this.criteria = criteria;
        genre = null;
        this.queryLimit = queryLimit;
        title = null;
        rating = 0d;
        seasonNumber = 0;
        this.filters = filters;
    }
    // Constructor pentru recomandari
    public Action(final int id, final String category, final String type, final String username,
                  final String genre) {
        this.id = id;
        this.category = category;
        this.type = type;
        this.username = username;
        queryType = null;
        sortingType = null;
        criteria = null;
        this.genre = genre;
        title = null;
        rating = 0d;
        seasonNumber = 0;
        filters = null;
    }

    public int getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public String getQueryType() {
        return queryType;
    }

    public String getSortingType() {
        return sortingType;
    }

    public String getCriteria() {
        return criteria;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public int getQueryLimit() {
        return queryLimit;
    }

    public double getRating() {
        return rating;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public List<List<String>> getFilters() {
        return filters;
    }
}
