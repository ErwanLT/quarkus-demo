package fr.eletutour.tavern.federation.quete.model;

import io.smallrye.graphql.api.federation.External;
import io.smallrye.graphql.api.federation.Extends;
import io.smallrye.graphql.api.federation.FieldSet;
import io.smallrye.graphql.api.federation.Key;
import org.eclipse.microprofile.graphql.Id;

/**
 * Ce service ne connaît d'Aventurier que sa clé : il ne le définit pas,
 * il l'étend. @External signale que "id" est fourni par un autre subgraph
 * (aventurier-service), pas par celui-ci.
 */
@Extends
@Key(fields = @FieldSet("id"))
public class Aventurier {

    @Id
    @External
    private Long id;

    public Aventurier() {
    }

    public Aventurier(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
