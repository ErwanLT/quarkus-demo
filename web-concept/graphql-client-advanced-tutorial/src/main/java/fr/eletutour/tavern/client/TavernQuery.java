package fr.eletutour.tavern.client;

import io.smallrye.graphql.client.generator.GraphQLQuery;
import io.smallrye.graphql.client.generator.GraphQLSchema;

@GraphQLSchema("resource:schema.graphql")
@GraphQLQuery("""
query Aventuriers {
  aventuriers {
    id
    nom
    classe
    niveau
    quetes {
      id
      titre
      difficulte
      recompenseOr
    }
  }
}
""")
@GraphQLQuery("""
query Aventurier($id: Int) {
  aventurier(id: $id) {
    id
    nom
    classe
    niveau
    quetes {
      id
      titre
      difficulte
      recompenseOr
    }
  }
}
""")
public interface TavernQuery {
}