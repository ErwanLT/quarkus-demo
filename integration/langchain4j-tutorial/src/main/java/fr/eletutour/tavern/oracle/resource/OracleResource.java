package fr.eletutour.tavern.oracle.resource;

import fr.eletutour.tavern.oracle.service.RecipeRetrievalService;
import fr.eletutour.tavern.oracle.service.TavernOracle;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/oracle")
@Produces(MediaType.TEXT_PLAIN)
public class OracleResource {
    private final TavernOracle oracle;
    private final RecipeRetrievalService retrieval;

    public OracleResource(TavernOracle oracle, RecipeRetrievalService retrieval) {
        this.oracle = oracle;
        this.retrieval = retrieval;
    }

    @GET
    public String ask(@QueryParam("q") String question) {
        if (question == null || question.isBlank()) return "Posez une question avec ?q=...";
        return oracle.answer(question);
    }

    @GET
    @Path("/grimoire")
    public String askGrimoire(@QueryParam("q") String question) {
        if (question == null || question.isBlank()) return "Posez une question avec ?q=...";
        String context = retrieval.retrieve(question);
        String prompt = "Question de l'aventurier : " + question
                + "\n\nPassages retrouvés dans le grimoire :\n" + context;
        return oracle.answerFromGrimoire(prompt);
    }
}
