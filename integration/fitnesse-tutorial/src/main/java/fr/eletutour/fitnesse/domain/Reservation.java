package fr.eletutour.fitnesse.domain;

public record Reservation(int numero, String nom, Chambre chambre, int nuits, boolean membre, int prix) {
}
