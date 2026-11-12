package fr.eletutour.tavern.barde.resource;

import io.quarkus.test.junit.QuarkusIntegrationTest;

/**
 * Rejoue tous les tests de {@link BardeResourceTest} contre l'artefact construit :
 * le jar en mode JVM, ou le binaire natif avec {@code ./mvnw verify -Dnative}.
 */
@QuarkusIntegrationTest
class BardeResourceIT extends BardeResourceTest {
}
