package fr.eletutour.tavern.vaadin.repository;

import fr.eletutour.tavern.vaadin.model.CellarBoard;
import fr.eletutour.tavern.vaadin.model.CellarStock;
import fr.eletutour.tavern.vaadin.model.DashboardSnapshot;
import fr.eletutour.tavern.vaadin.model.HighlightMetric;
import fr.eletutour.tavern.vaadin.model.MenuBoard;
import fr.eletutour.tavern.vaadin.model.MenuEntry;
import fr.eletutour.tavern.vaadin.model.MenuSection;
import fr.eletutour.tavern.vaadin.model.ReservationBoard;
import fr.eletutour.tavern.vaadin.model.ReservationEntry;
import fr.eletutour.tavern.vaadin.model.ServiceBoard;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class InMemoryTavernRepository {

    private final List<ReservationEntry> reservations = new ArrayList<>(List.of(
            new ReservationEntry("Compagnie du Loup Gris", 6, "19:15", "Coin du feu", "Confirmée", "Veut un service continu et une table protégée."),
            new ReservationEntry("Négociants de Port-Lune", 4, "20:00", "Grande table", "VIP", "Souhaitent un accord mets et cave réservée."),
            new ReservationEntry("Messagère du sud", 2, "21:00", "Balcon intérieur", "Rapide", "Repas court avant départ au petit matin."),
            new ReservationEntry("Guilde des cartographes", 5, "20:30", "Arrière-salle", "Confirmée", "Discussion privée sans animation musicale.")));

    public DashboardSnapshot fetchDashboardSnapshot() {
        return new DashboardSnapshot(
                "La grande salle entre dans son heure la plus vive",
                "Entre le comptoir, les chambres et la cave, le service doit rester précis sans perdre l'âme chaleureuse de la maison.",
                List.of(
                        new HighlightMetric("Tables en service", "18 / 24", "Le premier flux est presque complet."),
                        new HighlightMetric("Recette du jour", "2 480 or", "Les plats mijotés portent le ticket moyen."),
                        new HighlightMetric("Attente moyenne", "11 min", "La cuisine tient un rythme propre."),
                        new HighlightMetric("Fûts à surveiller", "2", "L'ambrée et le cidre tombent vite.")),
                "Ambiance du soir",
                "Trois bardes sont annoncés au coin du feu, la table des mercenaires demande de la discrétion et les voyageurs tardifs remplissent déjà les chambres hautes.",
                List.of(
                        "Sortir le plat du jour avant 20h15",
                        "Affecter un serveur supplémentaire au patio couvert",
                        "Confirmer quatre chambres pour les voyageurs du nord"),
                List.of(
                        "Le patio reste sous contrôle malgré la pluie fine.",
                        "Le chef veut pousser l'ardoise du gibier jusqu'à la fermeture.",
                        "Une table de négociants demande un accord avec les réserves de cave."));
    }

    public MenuBoard fetchMenuBoard() {
        return new MenuBoard(
                "Carte du jour",
                "Le chef pousse une cuisine de braise, de réduction et de cave, avec des assiettes plus riches le soir et un comptoir plus vif au coucher du soleil.",
                List.of(
                        new MenuSection(
                                "Entrées de comptoir",
                                "Pour lancer les tabourets et ouvrir les conversations.",
                                List.of(
                                        new MenuEntry("Tourte de gibier", "Oignons confits et pâte croustillante.", "14 or"),
                                        new MenuEntry("Fromages des collines", "Trois pâtes, noix et miel sombre.", "11 or"),
                                        new MenuEntry("Velouté de panais", "Poivre fumé et pain grillé au beurre noisette.", "9 or"))),
                        new MenuSection(
                                "Plats du foyer",
                                "Les assiettes qui tiennent la route d'un vrai service de taverne.",
                                List.of(
                                        new MenuEntry("Jarret braisé", "Réduction de bière brune et racines glacées.", "24 or"),
                                        new MenuEntry("Poêlée du maréchal", "Pommes de terre, champignons et jus corsé.", "19 or"),
                                        new MenuEntry("Ragoût du veilleur", "Cuisson lente, pain rustique et herbes du jardin.", "21 or"))),
                        new MenuSection(
                                "Becs et pintes",
                                "La cave sert la salle, pas l'inverse.",
                                List.of(
                                        new MenuEntry("Blonde des remparts", "Bière nette et lumineuse, tirée du fût du matin.", "7 or"),
                                        new MenuEntry("Hydromel réservé", "Epicé, rond, réservé aux longues tables.", "12 or"),
                                        new MenuEntry("Cidre du verger du nord", "Sec, tendu, utile sur les plats gras.", "8 or")))));
    }

    public ReservationBoard fetchReservationBoard() {
        return new ReservationBoard(
                "Le livre de salle est bien rempli pour le coup de feu.",
                new ArrayList<>(reservations),
                List.of(
                        "Garder l'arrière-salle hors du passage des bardes.",
                        "Monter la réserve de verres au balcon avant 19h45.",
                        "Prévoir une alternative de table si la pluie chasse tout le monde du patio."));
    }

    public void addReservation(ReservationEntry entry) {
        reservations.add(entry);
    }

    public CellarBoard fetchCellarBoard() {
        return new CellarBoard(
                List.of(
                        new CellarStock("Blonde des remparts", 42, 60, "pintes", "Encore solide pour le premier pic de la nuit."),
                        new CellarStock("Hydromel réservé", 9, 18, "cruchons", "A conserver pour les grandes tables."),
                        new CellarStock("Vin d'épices", 17, 24, "bouteilles", "Rotation saine sans tension immédiate."),
                        new CellarStock("Cidre du nord", 6, 20, "service(s)", "Commande à déclencher avant demain midi.")),
                "Lecture du cellier",
                "La cave est saine mais l'ambrée fruitée et le cidre descendent vite. Il faut remonter les fûts avant que la salle sature le comptoir.",
                List.of(
                        "Remonter un fût d'ambrée avant 19h00",
                        "Mettre trois bouteilles de réserve de côté pour les négociants",
                        "Vérifier la température des foudres côté pierre"));
    }

    public ServiceBoard fetchServiceBoard() {
        return new ServiceBoard(
                List.of(
                        new HighlightMetric("Commandes en attente", "7", "Le passe reste lisible."),
                        new HighlightMetric("Tables à desservir", "3", "Le rythme salle-comptoir est bon."),
                        new HighlightMetric("Equipe active", "12", "Un renfort dessert arrive à 20h."),
                        new HighlightMetric("Plats / heure", "31", "Cadence idéale pour la taille de la maison.")),
                List.of(
                        "1 maître d'hôtel sur la salle commune",
                        "2 serveurs au balcon et aux chambres basses",
                        "1 coureur entre cave, passe et comptoir",
                        "1 renfort desserts à partir de 20h00"),
                "Lecture du service",
                "Le service est nerveux mais sain. La salle tient si l'on garde la cave en avance et si le patio ne mange pas toute l'attention du comptoir.",
                "Service du soir",
                "Affluence forte attendue entre 19h30 et 21h00, avec pression sur les fûts clairs et les places près du feu.");
    }
}
