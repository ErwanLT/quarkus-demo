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

import java.util.List;

@ApplicationScoped
public class InMemoryTavernRepository {

    public DashboardSnapshot fetchDashboardSnapshot() {
        return new DashboardSnapshot(
                "La grande salle entre dans son heure la plus vive",
                "Entre le comptoir, les chambres et la cave, le service doit rester precis sans perdre l'ame chaleureuse de la maison.",
                List.of(
                        new HighlightMetric("Tables en service", "18 / 24", "Le premier flux est presque complet."),
                        new HighlightMetric("Recette du jour", "2 480 or", "Les plats mijotes portent le ticket moyen."),
                        new HighlightMetric("Attente moyenne", "11 min", "La cuisine tient un rythme propre."),
                        new HighlightMetric("Futs a surveiller", "2", "L'ambrée et le cidre tombent vite.")),
                "Ambiance du soir",
                "Trois bardes sont annonces au coin du feu, la table des mercenaires demande de la discretion et les voyageurs tardifs remplissent deja les chambres hautes.",
                List.of(
                        "Sortir le plat du jour avant 20h15",
                        "Affecter un serveur supplementaire au patio couvert",
                        "Confirmer quatre chambres pour les voyageurs du nord"),
                List.of(
                        "Le patio reste sous controle malgre la pluie fine.",
                        "Le chef veut pousser l'ardoise du gibier jusqu'a la fermeture.",
                        "Une table de negociants demande un accord avec les reserves de cave."));
    }

    public MenuBoard fetchMenuBoard() {
        return new MenuBoard(
                "Carte du jour",
                "Le chef pousse une cuisine de braise, de reduction et de cave, avec des assiettes plus riches le soir et un comptoir plus vif au coucher du soleil.",
                List.of(
                        new MenuSection(
                                "Entrees de comptoir",
                                "Pour lancer les tabourets et ouvrir les conversations.",
                                List.of(
                                        new MenuEntry("Tourte de gibier", "Oignons confits et pate croustillante.", "14 or"),
                                        new MenuEntry("Fromages des collines", "Trois pates, noix et miel sombre.", "11 or"),
                                        new MenuEntry("Veloute de panais", "Poivre fume et pain grille au beurre noisette.", "9 or"))),
                        new MenuSection(
                                "Plats du foyer",
                                "Les assiettes qui tiennent la route d'un vrai service de taverne.",
                                List.of(
                                        new MenuEntry("Jarret braise", "Reduction de biere brune et racines glacees.", "24 or"),
                                        new MenuEntry("Poelee du marechal", "Pommes de terre, champignons et jus corsé.", "19 or"),
                                        new MenuEntry("Ragout du veilleur", "Cuisson lente, pain rustique et herbes du jardin.", "21 or"))),
                        new MenuSection(
                                "Becs et pintes",
                                "La cave sert la salle, pas l'inverse.",
                                List.of(
                                        new MenuEntry("Blonde des remparts", "Biere nette et lumineuse, tiree du fut du matin.", "7 or"),
                                        new MenuEntry("Hydromel reserve", "Epicé, rond, reserve aux longues tables.", "12 or"),
                                        new MenuEntry("Cidre du verger du nord", "Sec, tendu, utile sur les plats gras.", "8 or")))));
    }

    public ReservationBoard fetchReservationBoard() {
        return new ReservationBoard(
                "Onze reservations confirment un service dense entre 19h15 et 21h00, avec une forte pression sur le coin du feu et les tables d'apparat.",
                List.of(
                        new ReservationEntry("Compagnie du Loup Gris", 6, "19:15", "Coin du feu", "Confirmee", "Veut un service continu et une table protegee."),
                        new ReservationEntry("Negociants de Port-Lune", 4, "20:00", "Grande table", "VIP", "Souhaitent un accord mets et cave reservee."),
                        new ReservationEntry("Messagere du sud", 2, "21:00", "Balcon interieur", "Rapide", "Repas court avant depart au petit matin."),
                        new ReservationEntry("Guilde des cartographes", 5, "20:30", "Arriere-salle", "Confirmee", "Discussion privee sans animation musicale.")),
                List.of(
                        "Garder l'arriere-salle hors du passage des bardes.",
                        "Monter la reserve de verres au balcon avant 19h45.",
                        "Prevoir une alternative de table si la pluie chasse tout le monde du patio."));
    }

    public CellarBoard fetchCellarBoard() {
        return new CellarBoard(
                List.of(
                        new CellarStock("Blonde des remparts", 42, 60, "pintes", "Encore solide pour le premier pic de la nuit."),
                        new CellarStock("Hydromel reserve", 9, 18, "cruchons", "A conserver pour les grandes tables."),
                        new CellarStock("Vin d'epices", 17, 24, "bouteilles", "Rotation saine sans tension immediate."),
                        new CellarStock("Cidre du nord", 6, 20, "service(s)", "Commande a declencher avant demain midi.")),
                "Lecture du cellier",
                "La cave est saine mais l'ambrée fruitee et le cidre descendent vite. Il faut remonter les futs avant que la salle sature le comptoir.",
                List.of(
                        "Remonter un fut d'ambrée avant 19h00",
                        "Mettre trois bouteilles de reserve de cote pour les negociants",
                        "Verifier la temperature des foudres cote pierre"));
    }

    public ServiceBoard fetchServiceBoard() {
        return new ServiceBoard(
                List.of(
                        new HighlightMetric("Commandes en attente", "7", "Le passe reste lisible."),
                        new HighlightMetric("Tables a desservir", "3", "Le rythme salle-comptoir est bon."),
                        new HighlightMetric("Equipe active", "12", "Un renfort dessert arrive a 20h."),
                        new HighlightMetric("Plats / heure", "31", "Cadence ideale pour la taille de la maison.")),
                List.of(
                        "1 maitre d'hotel sur la salle commune",
                        "2 serveurs au balcon et aux chambres basses",
                        "1 coureur entre cave, passe et comptoir",
                        "1 renfort desserts a partir de 20h00"),
                "Lecture du service",
                "Le service est nerveux mais sain. La salle tient si l'on garde la cave en avance et si le patio ne mange pas toute l'attention du comptoir.",
                "Service du soir",
                "Affluence forte attendue entre 19h30 et 21h00, avec pression sur les futs clairs et les places pres du feu.");
    }
}
