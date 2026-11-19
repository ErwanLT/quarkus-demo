package fr.eletutour.tavern.archives.exception.model;

import java.util.Map;

/**
 * Réponse d'erreur unique (RFC 9457), partagée par tous les mappers.
 *
 * <p>Les champs {@code instance}, {@code timestamp} et {@code additional}
 * ne sont pas obligatoires dans le standard, mais ils rendent l'erreur
 * beaucoup plus exploitable côté client et en observabilité.</p>
 */
public class Problem {

    private String type;
    private String title;
    private int status;
    private String detail;
    private String instance;
    private String timestamp;
    private Map<String, Object> additional;

    private Problem() {
    }

    public static Problem of(int status, String title) {
        Problem problem = new Problem();
        problem.status = status;
        problem.title = title;
        return problem;
    }

    public Problem type(String type) {
        this.type = type;
        return this;
    }

    public Problem detail(String detail) {
        this.detail = detail;
        return this;
    }

    public Problem instance(String instance) {
        this.instance = instance;
        return this;
    }

    public Problem timestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Problem additional(Map<String, Object> additional) {
        this.additional = additional;
        return this;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public int getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }

    public String getInstance() {
        return instance;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Map<String, Object> getAdditional() {
        return additional;
    }
}
