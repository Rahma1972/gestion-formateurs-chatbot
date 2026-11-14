package tn.enicarthage.gestionformation.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUETES_LOGIN = 15;
    private static final int MAX_REQUETES_CHATBOT = 8;
    private static final Set<String> ROUTES_AUTH = Set.of("/login", "/inscription");
    private static final Set<String> ROUTES_CHATBOT = Set.of("/chatbot/message", "/chatbot/analyser-cv", "/chatbot/generer-description");
    private static final long FENETRE_MS = 60_000; // 1 minute
    private static final Set<String> ROUTES_PROTEGEES = Set.of("/login", "/inscription", "/chatbot/message", "/chatbot/analyser-cv", "/chatbot/generer-description");

    private final ConcurrentHashMap<String, Compteur> compteurs = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        boolean estMethodeConcernee = "POST".equalsIgnoreCase(request.getMethod()) || "GET".equalsIgnoreCase(request.getMethod());

        int limite = 0;
        if (estMethodeConcernee && ROUTES_AUTH.contains(uri)) {
            limite = MAX_REQUETES_LOGIN;
        } else if ("POST".equalsIgnoreCase(request.getMethod()) && ROUTES_CHATBOT.contains(uri)) {
            limite = MAX_REQUETES_CHATBOT;
        }

        if (limite > 0) {
            String ip = extraireIp(request);
            String cle = ip + ":" + uri;
            Compteur compteur = compteurs.computeIfAbsent(cle, k -> new Compteur());

            synchronized (compteur) {
                long maintenant = System.currentTimeMillis();
                if (maintenant - compteur.debutFenetre > FENETRE_MS) {
                    compteur.debutFenetre = maintenant;
                    compteur.nombre.set(0);
                }
                int nb = compteur.nombre.incrementAndGet();
                if (nb > limite) {
                    response.setStatus(429);
                    response.setContentType("text/html; charset=UTF-8");
                    response.getWriter().write(
                            "<h2 style='text-align:center;font-family:sans-serif;margin-top:50px'>" +
                                    "محاولات كثيرة جدا. الرجاء الانتظار قليلا قبل إعادة المحاولة." +
                                    "</h2>"
                    );
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
    private String extraireIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static class Compteur {
        volatile long debutFenetre = System.currentTimeMillis();
        AtomicInteger nombre = new AtomicInteger(0);
    }
}