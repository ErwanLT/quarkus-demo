package fr.eletutour.tavern.vaadin.service;

import fr.eletutour.tavern.vaadin.model.CellarStock;
import java.util.List;

public record StockUpdatedEvent(List<CellarStock> stocks) {
}
