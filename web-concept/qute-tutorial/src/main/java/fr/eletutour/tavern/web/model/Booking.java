package fr.eletutour.tavern.web.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
public class Booking extends PanacheEntity {
    public String adventurerName;
    public LocalDate arrivalDate;
    public int nights;
    public String roomType;

    public Booking() {}
}
