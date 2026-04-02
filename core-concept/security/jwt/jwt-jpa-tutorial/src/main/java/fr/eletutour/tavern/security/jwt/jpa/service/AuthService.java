package fr.eletutour.tavern.security.jwt.jpa.service;

import fr.eletutour.tavern.security.jwt.jpa.model.TavernUser;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.Set;

@ApplicationScoped
public class AuthService {

    private static final String ISSUER = "tavern-issuer";
    private static final long TOKEN_TTL_SECONDS = 3600;

    public String login(String username, String password) {
        TavernUser user = TavernUser.find("username", username).firstResult();
        if (user == null || user.password == null || !BcryptUtil.matches(password, user.password)) {
            return null;
        }

        Instant now = Instant.now();
        return Jwt.issuer(ISSUER)
                .upn(user.username)
                .preferredUserName(user.username)
                .groups(Set.of(user.role))
                .issuedAt(now)
                .expiresAt(now.plusSeconds(TOKEN_TTL_SECONDS))
                .sign();
    }

    public long expiresAtSeconds() {
        return Instant.now().plusSeconds(TOKEN_TTL_SECONDS).getEpochSecond();
    }
}
