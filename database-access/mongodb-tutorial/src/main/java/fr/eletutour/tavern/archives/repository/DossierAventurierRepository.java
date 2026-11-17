package fr.eletutour.tavern.archives.repository;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import fr.eletutour.tavern.archives.dto.StatistiquesClasse;
import fr.eletutour.tavern.archives.model.ClasseAventurier;
import fr.eletutour.tavern.archives.model.DossierAventurier;
import fr.eletutour.tavern.archives.model.Rang;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DossierAventurierRepository implements PanacheMongoRepository<DossierAventurier> {

    public Optional<DossierAventurier> parNom(String nom) {
        return find(new Document("nom", nom)).firstResultOptional();
    }

    /**
     * Filtre par classe et/ou par rang d'une prouesse intégrée.
     *
     * <p>{@code prouesses.rang} est une notation à points : MongoDB cherche
     * dans chaque élément du tableau, sans déplier le document.</p>
     */
    public List<DossierAventurier> chercher(ClasseAventurier classe, Rang rang, int page, int taille) {
        return find(filtre(classe, rang)).page(page, taille).list();
    }

    public long compter(ClasseAventurier classe, Rang rang) {
        return count(filtre(classe, rang));
    }

    /**
     * Répartition des effectifs et des bourses par classe, via un pipeline
     * d'agrégation MongoDB.
     */
    public List<StatistiquesClasse> repartition() {
        List<Bson> pipeline = List.of(
                Aggregates.group("$classe",
                        Accumulators.sum("effectif", 1),
                        Accumulators.sum("bourse", "$bourse")),
                Aggregates.sort(Sorts.ascending("_id")));

        List<StatistiquesClasse> statistiques = new ArrayList<>();
        for (Document ligne : mongoCollection().aggregate(pipeline, Document.class)) {
            Number effectif = (Number) ligne.get("effectif");
            Number bourse = (Number) ligne.get("bourse");
            statistiques.add(new StatistiquesClasse(
                    ligne.getString("_id"),
                    effectif == null ? 0 : effectif.intValue(),
                    bourse == null ? 0 : bourse.intValue()));
        }
        return statistiques;
    }

    private Document filtre(ClasseAventurier classe, Rang rang) {
        Document filtre = new Document();
        if (classe != null) {
            filtre.append("classe", classe.name());
        }
        if (rang != null) {
            filtre.append("prouesses.rang", rang.name());
        }
        return filtre;
    }
}
