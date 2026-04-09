package fr.eletutour.observability.metrics;

import io.micrometer.core.aop.MeterTag;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import java.util.ArrayList;
import java.util.List;

@BusinessTimed("")
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class BusinessTimedInterceptor {

    private static final String COUNTER_SUFFIX = ".count";

    @Inject
    MeterRegistry meterRegistry;

    @AroundInvoke
    Object track(InvocationContext context) throws Exception {
        BusinessTimed timed = resolveAnnotation(context);
        if (timed == null || timed.value().isBlank()) {
            return context.proceed();
        }

        List<Tag> tags = toTags(timed.tags());
        Counter counter = Counter.builder(timed.value() + COUNTER_SUFFIX)
            .description(description(timed.description(), "Number of calls"))
            .tags(tags)
            .register(meterRegistry);
        Timer timer = Timer.builder(timed.value())
            .description(description(timed.description(), "Execution time"))
            .publishPercentileHistogram()
            .tags(tags)
            .register(meterRegistry);

        counter.increment();
        return timer.recordCallable(context::proceed);
    }

    private BusinessTimed resolveAnnotation(InvocationContext context) {
        BusinessTimed timed = context.getMethod().getAnnotation(BusinessTimed.class);
        if (timed != null) {
            return timed;
        }
        return context.getTarget().getClass().getAnnotation(BusinessTimed.class);
    }

    private List<Tag> toTags(MeterTag[] meterTags) {
        List<Tag> tags = new ArrayList<>();
        if (meterTags == null) {
            return tags;
        }
        for (MeterTag meterTag : meterTags) {
            tags.add(Tag.of(meterTag.key(), meterTag.value()));
        }
        return tags;
    }

    private String description(String provided, String fallback) {
        return (provided == null || provided.isBlank()) ? fallback : provided;
    }
}
