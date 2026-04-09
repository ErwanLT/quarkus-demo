package fr.eletutour.exception.business;

public class TavernException extends BusinessException {
    private final TavernError error;
    private final Integer currentCapacity;
    private final Integer maxCapacity;

    public TavernException(TavernError error, Object... detailArgs) {
        this(error, null, null, detailArgs);
    }

    private TavernException(TavernError error, Integer currentCapacity, Integer maxCapacity, Object... detailArgs) {
        super(error.getStatus(), error.getTitle(), error.formatDetail(detailArgs));
        this.error = error;
        this.currentCapacity = currentCapacity;
        this.maxCapacity = maxCapacity;
    }

    public static TavernException notFound(String name) {
        return new TavernException(TavernError.NOT_FOUND, name);
    }

    public static TavernException alreadyExists(String name) {
        return new TavernException(TavernError.ALREADY_EXISTS, name);
    }

    public static TavernException capacityReached(String name, int current, int max) {
        return new TavernException(TavernError.CAPACITY_REACHED, current, max, name);
    }

    public TavernError getError() {
        return error;
    }

    public Integer getCurrentCapacity() {
        return currentCapacity;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }
}
