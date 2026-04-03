package fr.eletutour.model;

public class Tavern {
    private final String name;
    private final String city;
    private final int maxCapacity;
    private int currentCapacity;

    public Tavern(String name, String city, int maxCapacity) {
        this.name = name;
        this.city = city;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = 0;
    }

    public String getName() { return name; }
    public String getCity() { return city; }
    public int getMaxCapacity() { return maxCapacity; }
    public int getCurrentCapacity() { return currentCapacity; }

    public void enter(int count) {
        this.currentCapacity += count;
    }
}
