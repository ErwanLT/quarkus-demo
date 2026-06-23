package fr.eletutour.model;

/**
 * Représente un plat inscrit sur l'ardoise du menu du jour.
 * <p>
 * Chaque plat a un nom, une description et un prix en pièces d'or.
 * Ce record est immuable : une fois l'ardoise écrite, elle ne change pas
 * jusqu'à ce que le tavernier l'efface ({@code @CacheInvalidate}).
 * </p>
 *
 * @param nom         Le nom du plat (ex : "Ragoût de sanglier")
 * @param description Une courte description pour allécher l'aventurier
 * @param prixEnOr    Le prix en pièces d'or
 */
public record Plat(String nom, String description, double prixEnOr) {}
