package tech.challenge.scheduling.system.infrastructure.security;

public interface TokenService {
    String generateToken(String login, String role);
    String verifyToken(String token);
}