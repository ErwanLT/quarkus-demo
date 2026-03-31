package fr.eletutour.tavern.service;

import fr.eletutour.tavern.dto.TavernGreetingResponse;
import fr.eletutour.tavern.dto.TavernOrderRequest;
import fr.eletutour.tavern.dto.TavernOrderResponse;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TavernService {

    public TavernGreetingResponse greeting(String name) {
        String target = (name == null || name.isBlank()) ? "adventurer" : name;
        return new TavernGreetingResponse("Welcome to the tavern, " + target + "!");
    }

    public TavernOrderResponse order(TavernOrderRequest request) {
        return new TavernOrderResponse(request.item(), request.quantity(), "Order accepted by the tavern keeper.");
    }
}
