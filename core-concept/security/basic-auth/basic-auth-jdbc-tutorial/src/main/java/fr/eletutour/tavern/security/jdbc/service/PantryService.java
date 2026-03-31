package fr.eletutour.tavern.security.jdbc.service;

import fr.eletutour.tavern.security.jdbc.dto.StockResponse;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class PantryService {

    public StockResponse pantry() {
        return new StockResponse("pantry", List.of("bread", "cheese", "salted meat", "herbs"));
    }

    public StockResponse cellar() {
        return new StockResponse("cellar", List.of("ale", "mead", "stout"));
    }
}
