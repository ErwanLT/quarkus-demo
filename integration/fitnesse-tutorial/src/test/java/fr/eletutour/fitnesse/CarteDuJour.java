package fr.eletutour.fitnesse;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Fixture de table de requête (query, subset query, ordered query) : elle
 * renvoie toutes les lignes, FitNesse se charge de les comparer à la table.
 * Le format attendu par Slim est une liste de lignes, chaque ligne étant une
 * liste de couples [nom de colonne, valeur].
 */
public class CarteDuJour {

    private final String categorie;

    public CarteDuJour() {
        this("");
    }

    public CarteDuJour(String categorie) {
        this.categorie = categorie;
    }

    public List<List<List<String>>> query() {
        var reponse = ClientTaverne.get("/taverne/carte?categorie=" + ClientTaverne.encoder(categorie));
        List<List<List<String>>> lignes = new ArrayList<>();
        for (JsonNode plat : ClientTaverne.json(reponse.body())) {
            lignes.add(List.of(
                    List.of("plat", plat.get("nom").asText()),
                    List.of("categorie", plat.get("categorie").asText()),
                    List.of("prix", plat.get("prix").asText())));
        }
        return lignes;
    }
}
