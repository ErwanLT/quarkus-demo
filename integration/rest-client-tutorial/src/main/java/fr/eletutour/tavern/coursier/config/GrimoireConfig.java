package fr.eletutour.tavern.coursier.config;

import io.smallrye.config.ConfigMapping;

import java.net.URI;
import java.time.Duration;

/**
 * Réglages du client programmatique du Grimoire, regroupés sous
 * {@code taverne.grimoire}.
 */
@ConfigMapping(prefix = "taverne.grimoire")
public interface GrimoireConfig {

    URI url();

    Duration connectTimeout();

    Duration readTimeout();
}
