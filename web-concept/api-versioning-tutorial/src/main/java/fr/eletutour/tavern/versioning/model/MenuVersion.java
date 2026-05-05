package fr.eletutour.tavern.versioning.model;

import fr.eletutour.tavern.versioning.exception.UnknownMenuVersionException;

public enum MenuVersion {
    V1("1"),
    V2("2");

    private final String apiValue;

    MenuVersion(String apiValue) {
        this.apiValue = apiValue;
    }

    public static MenuVersion fromApiValue(String apiValue) {
        return switch (apiValue) {
            case "1" -> V1;
            case "2" -> V2;
            default -> throw new UnknownMenuVersionException(apiValue);
        };
    }

    public String apiValue() {
        return apiValue;
    }
}
