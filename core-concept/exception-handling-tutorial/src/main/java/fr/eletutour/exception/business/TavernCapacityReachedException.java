package fr.eletutour.exception.business;

public class TavernCapacityReachedException extends BusinessException {
    private final int currentCapacity;
    private final int maxCapacity;

    public TavernCapacityReachedException(String name, int current, int max) {
        super(422, "Capacité atteinte", "Impossible d'accueillir plus de monde à " + name);
        this.currentCapacity = current;
        this.maxCapacity = max;
    }

    public int getCurrentCapacity() { return currentCapacity; }
    public int getMaxCapacity() { return maxCapacity; }
}
