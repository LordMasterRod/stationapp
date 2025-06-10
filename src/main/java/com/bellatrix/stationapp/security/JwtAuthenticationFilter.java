package com.bellatrix.stationapp.security;

import com.bellatrix.stationapp.service.UtilisateurService;
import io.jsonwebtoken.Claims; // Importez Claims
import io.jsonwebtoken.JwtException; // Importez JwtException
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Importez SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Importez UsernameNotFoundException
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger; // Importez le Logger
import org.slf4j.LoggerFactory; // Importez le LoggerFactory

import java.io.IOException;
import java.util.Collections; // Importez Collections

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class); // Initialisez le Logger

    private final JwtService jwtService;
    private final UtilisateurService userService; // Utilisé pour les rôles ADMIN/autres

    public JwtAuthenticationFilter(JwtService jwtService, UtilisateurService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String usernameFromToken; // Renommé pour plus de clarté

        // 1. Vérifier si l'en-tête d'autorisation est présent et au bon format
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // Extraire le token JWT

        // 2. Extraire le nom d'utilisateur (ou ID) du token
        try {
            usernameFromToken = jwtService.extractUsername(jwt);
        } catch (JwtException e) {
            // Gérer les tokens JWT invalides ou expirés
            logger.warn("JWT invalide ou expiré: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }


        // 3. Procéder à l'authentification si le nom d'utilisateur est présent
        // et qu'aucune authentification n'est déjà présente dans le contexte de sécurité
        if (usernameFromToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // Tenter d'extraire les claims pour obtenir le rôle
                Claims claims = jwtService.extractAllClaims(jwt); // Assurez-vous que jwtService a cette méthode
                String role = claims.get("role", String.class); // Assurez-vous que le rôle est bien dans les claims

                // Log pour le débogage
                logger.debug("Tentative d'authentification pour l'utilisateur: {} avec le rôle: {}", usernameFromToken, role);

                if ("CLIENT_WEB".equals(role)) { // Si le rôle est CLIENT_WEB
                    // Pour un client, nous ne cherchons PAS l'utilisateur dans UtilisateurService
                    // car son "username" est en fait son ID de carte ou client
                    // Nous créons directement l'objet d'authentification basé sur les claims du token.
                    // Le `principal` peut être l'ID du client (si stocké en tant que "sub" ou "clientId" dans le token)
                    // Note: Long.valueOf(usernameFromToken) est utilisé si l'ID client est directement le "subject" du JWT.
                    // Sinon, utilisez claims.get("clientId", Long.class) si vous avez un claim spécifique.
                    Long clientId = Long.valueOf(usernameFromToken);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            clientId, // Le principal est l'ID du client
                            null, // Pas de credentials pour les tokens JWT
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)) // Rôle du client
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("Authentification réussie pour le client avec ID: {}", clientId);

                } else { // Pour les autres rôles (ADMIN, etc.)
                    // On charge les UserDetails depuis le service utilisateur habituel
                    UserDetails userDetails = this.userService.loadUserByUsername(usernameFromToken);

                    // Vérifier si le token est valide pour ces UserDetails
                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        logger.debug("Authentification réussie pour l'utilisateur: {}", userDetails.getUsername());
                    } else {
                        logger.warn("Token JWT non valide pour l'utilisateur: {}", usernameFromToken);
                    }
                }
            } catch (UsernameNotFoundException e) {
                // Cette exception peut se produire si le "usernameFromToken" est un ID client
                // et qu'il n'existe pas en tant qu'utilisateur "normal".
                // Pour les clients, nous la gérons différemment ci-dessus.
                // Pour les autres cas (admin non trouvé par exemple), on log l'erreur.
                logger.warn("Utilisateur non trouvé par le service utilisateur: {} - {}", usernameFromToken, e.getMessage());
            } catch (JwtException e) {
                logger.warn("Erreur de traitement du JWT: {}", e.getMessage());
            } catch (NumberFormatException e) { // Pour le cas où usernameFromToken n'est pas un Long
                logger.error("Erreur de format de numéro lors de la conversion de l'ID client: {}", usernameFromToken, e);
            }
            catch (Exception e) { // Attrape toute autre exception inattendue
                logger.error("Erreur inattendue lors de l'authentification JWT: {}", e.getMessage(), e);
            }
        }
        filterChain.doFilter(request, response);
    }
}