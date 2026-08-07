#!/usr/bin/env bash
# Contourne deux incompatibilités entre SmallRye GraphQL et rover :
#
# 1. https://github.com/smallrye/smallrye-graphql/issues/2065
#    SmallRye nomme son scalaire fédération "FieldSet" au lieu de "_FieldSet"
#    attendu par rover/Apollo.
#
# 2. SmallRye n'émet jamais de "extend schema @link(...)", donc rover traite
#    le schéma comme "non lié" et plafonne son interprétation à la fédération
#    v2.4, qui ne connaît pas l'argument "label" de @override (arrivé en v2.7).
#    On ne l'utilise nulle part ici, donc on le retire simplement.
#
# On récupère le SDL de chaque subgraph, on corrige les deux, puis rover
# consomme les fichiers corrigés plutôt que d'introspecter les services en direct.
set -euo pipefail
cd "$(dirname "$0")"

echo "→ Introspection de aventurier-service (8081)..."
rover subgraph introspect http://localhost:8081/graphql > aventurier.graphql

echo "→ Introspection de quete-service (8082)..."
rover subgraph introspect http://localhost:8082/graphql > quete.graphql

echo "→ Correction FieldSet -> _FieldSet..."
# Pas de \b ici : le sed BSD (macOS) ne le supporte pas, même avec -E,
# et échoue silencieusement (aucun remplacement, aucune erreur).
sed -i.bak -E 's/FieldSet/_FieldSet/g' aventurier.graphql quete.graphql

echo "→ Correction @override (label non supporté sans @link explicite, et inutilisé ici)..."
sed -i.bak -E 's/directive @override\(from: String!, label: String\) on FIELD_DEFINITION/directive @override(from: String!) on FIELD_DEFINITION/' aventurier.graphql quete.graphql

rm -f aventurier.graphql.bak quete.graphql.bak

echo "→ Schémas corrigés : router/aventurier.graphql, router/quete.graphql"