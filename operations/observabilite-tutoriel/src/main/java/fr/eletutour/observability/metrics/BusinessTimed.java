package fr.eletutour.observability.metrics;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import io.micrometer.core.aop.MeterTag;
import jakarta.enterprise.util.Nonbinding;
import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@InterceptorBinding
@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface BusinessTimed {

    @Nonbinding
    String value();

    @Nonbinding
    String description() default "";

    @Nonbinding
    MeterTag[] tags() default {};
}
