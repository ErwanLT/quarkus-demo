package fr.eletutour.tavern.archives.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Le dossier d'un aventurier, stocké en une seule unité dans la collection
 * {@code aventuriers}.
 *
 * <p>Les prouesses sont <em>intégrées</em> au document : on les lit toujours
 * avec leur aventurier, et leur structure peut varier d'un dossier à l'autre.</p>
 */
@MongoEntity(collection = "aventuriers")
public class DossierAventurier extends PanacheMongoEntity {

    public String nom;
    public ClasseAventurier classe;
    public int niveau;
    public int bourse;
    public List<Prouesse> prouesses = new ArrayList<>();
}
