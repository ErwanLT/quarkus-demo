package fr.eletutour.validation.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Contrainte custom de classe : elle porte sur plusieurs champs à la fois,
 * ce qu'aucune annotation de champ ne peut faire.
 */
@Documented
@Constraint(validatedBy = SejourCoherentValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SejourCoherent {
    String message() default "Le séjour doit durer entre 1 et 7 nuits";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
