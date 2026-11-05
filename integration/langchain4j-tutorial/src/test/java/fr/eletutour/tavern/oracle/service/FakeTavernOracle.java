package fr.eletutour.tavern.oracle.service;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Oracle de remplacement pour les tests : il se substitue au service IA généré,
 * donc Ollama n'est jamais contacté (ni en local, ni sur la CI GitHub).
 * Il garde en mémoire ce que le resource lui a transmis (@Singleton : pas de proxy client, les champs publics sont lus tels quels).
 */
@Alternative
@Priority(1)
@Singleton
public class FakeTavernOracle implements TavernOracle {
    public final List<String> questions = new ArrayList<>();
    public final List<String> prompts = new ArrayList<>();
    public String reponse = "";

    public void reinitialiser() {
        questions.clear();
        prompts.clear();
        reponse = "";
    }

    @Override
    public String answer(String question) {
        questions.add(question);
        return reponse;
    }

    @Override
    public String answerFromGrimoire(String contextAugmentedQuestion) {
        prompts.add(contextAugmentedQuestion);
        return reponse;
    }
}
