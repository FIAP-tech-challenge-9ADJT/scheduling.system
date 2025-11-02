package tech.challenge.scheduling.system.infrastructure.security;

import tech.challenge.scheduling.system.infrastructure.persistence.entities.UserJpaEntity;

public interface TokenService {
    String generateToken(UserJpaEntity user);

    String verifyToken(String token);
}