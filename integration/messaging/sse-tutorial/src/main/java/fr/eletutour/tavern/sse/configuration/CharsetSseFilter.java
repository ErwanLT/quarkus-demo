package fr.eletutour.tavern.sse.configuration;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CharsetSseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext,
                        ContainerResponseContext responseContext) {
        Object contentType = responseContext.getHeaders().getFirst("Content-Type");
        if (contentType != null && contentType.toString().startsWith(MediaType.SERVER_SENT_EVENTS)) {
            responseContext.getHeaders().putSingle("Content-Type", "text/event-stream;charset=UTF-8");
        }
    }
}