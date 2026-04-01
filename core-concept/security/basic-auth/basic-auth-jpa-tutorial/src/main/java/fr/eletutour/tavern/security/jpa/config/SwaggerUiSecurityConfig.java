package fr.eletutour.tavern.security.jpa.config;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Startup
@ApplicationScoped
public class SwaggerUiSecurityConfig {

    @ConfigProperty(name = "quarkus.http.auth.permission.swagger-ui.paths")
    String swaggerPaths;

    @ConfigProperty(name = "quarkus.http.auth.permission.swagger-ui.policy")
    String swaggerPolicy;
}
