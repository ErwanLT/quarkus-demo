package fr.eletutour.tavern.web.controller;

import fr.eletutour.tavern.web.model.Drink;
import fr.eletutour.tavern.web.model.Food;
import fr.eletutour.tavern.web.model.Booking;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestForm;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Path("/tavern-web")
public class TavernWebResource {

    @CheckedTemplate(basePath = "")
    public static class Templates {
        public static native TemplateInstance index(String title);
        public static native TemplateInstance drinks(String title, List<Drink> drinks);
        public static native TemplateInstance food(String title, List<Food> food);
        public static native TemplateInstance booking(String title, BookingForm form, Map<String, String> errors);
        public static native TemplateInstance bookingSuccess(String title, Booking booking);
        public static native TemplateInstance admin(String title, List<Booking> bookings);
    }

    private final Validator validator;

    public TavernWebResource(Validator validator) {
        this.validator = validator;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance index() {
        return Templates.index("Bienvenue");
    }

    @GET
    @Path("/drinks")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance drinks() {
        List<Drink> drinks = List.of(
                new Drink("Pan Galactic Gargle Blaster",
                        "L'effet est celui d'avoir le cerveau écrasé par une tranche de citron.", 42.00),
                new Drink("Hydromel de l'Improbabilité",
                        "Une gorgée et tout change. Littéralement.", 4.20),
                new Drink("Vodka de la Mort Noire",
                        "Attention : peut faire disparaître des planètes.", 18.50),
                new Drink("Thé à la Crème Très Très Chaud",
                        "Comme le préfère Arthur Dent, mais version améliorée par Marvin.", 3.75),
                new Drink("Old Janx Spirit",
                        "Le spiritueux préféré des pilotes de vaisseaux spatiaux.", 12.90),
                new Drink("Grog de Slartibartfast",
                        "Avec un soupçon de fjords norvégiens.", 9.80),
                new Drink("Zaphod's Double Sunburn",
                        "Deux soleils dans un seul verre. Effet garanti.", 25.00)
        );

        return Templates.drinks("Carte des Boissons", drinks);
    }

    @GET
    @Path("/food")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance food() {
        List<Food> foods = List.of(
                new Food("Rôti de Bugblatter Beast de Traal",
                        "Venant tout droit de Traal. Attention à ne pas l'insulter.", 25.00),
                new Food("Pain de Voyageur",
                        "Garanti sans paradoxe temporel.", 5.00),
                new Food("Steak de Vache Mutante",
                        "Elle a passé toute sa vie à vouloir être mangée.", 32.50),
                new Food("Salade de l'Infinie Improbabilité",
                        "Chaque bouchée est différente. Parfois c'est du homard.", 14.90),
                new Food("Fish & Chips de Magrathea",
                        "Les meilleurs de la galaxie, customisés selon vos rêves.", 16.00),
                new Food("Soupe de la Poésie Vogon",
                        "Très... très... très... longue à digérer.", 8.20),
                new Food("Burger du Dernier Restaurant avant la Fin du Monde",
                        "Servi juste avant la destruction de la Terre.", 22.00),
                new Food("Œufs à la McMillan",
                        "Préparés par le chef le plus déprimé de l'univers.", 11.50)
        );

        return Templates.food("Carte des Mets", foods);
    }

    @GET
    @Path("/booking")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance booking() {
        BookingForm form = new BookingForm();
        form.roomType = "Suite Galactique";
        return Templates.booking("Réserver une Chambre", form, Map.of());
    }

    @POST
    @Path("/booking")
    @Transactional
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance processBooking(
            @RestForm String adventurerName,
            @RestForm String arrivalDate,
            @RestForm int nights,
            @RestForm String roomType) {

        BookingForm form = new BookingForm();
        form.adventurerName = adventurerName;
        form.arrivalDate = arrivalDate;
        form.nights = nights;
        form.roomType = roomType;

        Map<String, String> errors = validateForm(form);
        LocalDate parsedDate = null;
        if (errors.isEmpty()) {
            try {
                parsedDate = LocalDate.parse(form.arrivalDate);
            } catch (DateTimeParseException ex) {
                errors.put("arrivalDate", "La date est invalide (format attendu AAAA-MM-JJ).");
            }
        }

        if (!errors.isEmpty()) {
            return Templates.booking("Réserver une Chambre", form, errors);
        }

        Booking booking = new Booking();
        booking.adventurerName = form.adventurerName;
        booking.arrivalDate = parsedDate;
        booking.nights = form.nights;
        booking.roomType = form.roomType;
        
        booking.persist(); // SAUVEGARDE EN BASE (Panache magic)
        
        return Templates.bookingSuccess("Réservation Confirmée", booking);
    }

    @GET
    @Path("/admin")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance admin() {
        List<Booking> allBookings = Booking.listAll(); // RÉCUPÉRATION DE TOUTES LES RÉSERVATIONS
        return Templates.admin("Registre de l'Auberge", allBookings);
    }

    private Map<String, String> validateForm(BookingForm form) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (ConstraintViolation<BookingForm> violation : validator.validate(form)) {
            String field = violation.getPropertyPath().toString();
            if (!errors.containsKey(field)) {
                errors.put(field, violation.getMessage());
            }
        }
        return errors;
    }
}
