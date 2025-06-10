package com.bellatrix.stationapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Clé secrète JWT lue depuis application.properties
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    // Durée de validité du token JWT en millisecondes, lue depuis application.properties
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    /**
     * Extrait le nom d'utilisateur (subject) du token JWT.
     * @param token Le token JWT.
     * @return Le nom d'utilisateur.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait une claim spécifique du token JWT.
     * @param token Le token JWT.
     * @param claimsResolver Fonction pour résoudre la claim.
     * @param <T> Le type de la claim.
     * @return La claim extraite.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Génère un token JWT avec le rôle de l'utilisateur comme claim personnalisée.
     * Cette méthode est utile pour les cas où le rôle est la seule claim supplémentaire.
     * @param username Le nom d'utilisateur.
     * @param role Le rôle de l'utilisateur.
     * @return Le token JWT généré.
     */
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role); // Ajoute le rôle comme une claim personnalisée
        return buildToken(claims, username, jwtExpiration);
    }

    /**
     * Surcharge pour générer un token JWT avec des claims supplémentaires.
     * C'est cette méthode que vous utiliserez pour ajouter des claims comme "role"
     * et potentiellement "clientId" lors du login client.
     * @param extraClaims Des claims additionnelles à inclure dans le token.
     * @param username Le nom d'utilisateur.
     * @return Le token JWT généré.
     */
    public String generateToken(
            Map<String, Object> extraClaims,
            String username
    ) {
        return buildToken(extraClaims, username, jwtExpiration);
    }

    /**
     * Construit le token JWT.
     * @param extraClaims Des claims additionnelles.
     * @param username Le nom d'utilisateur (subject).
     * @param expiration La durée de validité en millisecondes.
     * @return Le token JWT compacté.
     */
    private String buildToken(Map<String, Object> extraClaims, String username, long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Date d'émission
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Date d'expiration
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Signature avec la clé secrète
                .compact();
    }

    /**
     * Valide un token JWT par rapport aux UserDetails fournis.
     * @param token Le token JWT à valider.
     * @param userDetails Les détails de l'utilisateur.
     * @return true si le token est valide pour l'utilisateur et non expiré, sinon false.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Vérifie si le token est expiré.
     * @param token Le token JWT.
     * @return true si le token est expiré, sinon false.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrait la date d'expiration du token.
     * @param token Le token JWT.
     * @return La date d'expiration.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrait toutes les claims du token JWT.
     * Cette méthode est maintenant **publique** pour être accessible depuis JwtAuthenticationFilter.
     * @param token Le token JWT.
     * @return L'objet Claims contenant toutes les claims.
     */
    public Claims extractAllClaims(String token) { // Changement de 'private' à 'public'
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Récupère la clé de signature à partir de la clé secrète Base64.
     * @return L'objet Key utilisé pour la signature.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}