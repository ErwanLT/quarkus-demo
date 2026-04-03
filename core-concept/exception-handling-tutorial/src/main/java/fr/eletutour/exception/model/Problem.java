package fr.eletutour.exception.model;

import java.util.Map;

public class Problem {
    private String type;
    private String title;
    private int status;
    private String detail;
    private Map<String, Object> additional;

    private Problem() {}

    public static Problem of(int status, String title) {
        Problem p = new Problem();
        p.status = status;
        p.title = title;
        return p;
    }

    public Problem type(String type) {
        this.type = type;
        return this;
    }

    public Problem detail(String detail) {
        this.detail = detail;
        return this;
    }

    public Problem additional(Map<String, Object> additional) {
        this.additional = additional;
        return this;
    }

    public String getType() { return type; }
    public String getTitle() { return title; }
    public int getStatus() { return status; }
    public String getDetail() { return detail; }
    public Map<String, Object> getAdditional() { return additional; }
}
