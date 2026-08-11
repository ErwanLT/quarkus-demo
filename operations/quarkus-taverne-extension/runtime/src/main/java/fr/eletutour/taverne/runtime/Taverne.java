package fr.eletutour.taverne.runtime;

import jakarta.enterprise.util.Nonbinding;
import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Le glyphe du royaume.
 *
 * <p>Poser {@code @Taverne} sur une méthode revient à graver une rune sur la porte
 * d'une salle : chaque appel est annoncé à la cantonade par le tavernier, avec
 * le ton qui va bien.
 */
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Taverne {

    /**
     * Le nom de la quête annoncée dans les logs.
     * Par défaut, le nom de la méthode interceptée est utilisé.
     *
     * <p>Marqué {@code @Nonbinding} : sans ça, CDI considère que
     * {@code @Taverne(quete = "A")} et {@code @Taverne(quete = "B")} sont deux
     * bindings différents, et l'interceptor ne matcherait que la valeur qu'il
     * porte lui-même (ici la valeur par défaut, une chaîne vide).
     */
    @Nonbinding
    String quete() default "";
}
