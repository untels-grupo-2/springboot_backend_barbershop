package com.diamondbarbershop.apibarbershop.shared.infrastructure.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        String clientIp = obtenerIpCliente(request);
        Plan plan = clasificar(path);

        String bucketKey = plan.name() + ":" + clientIp;
        Bucket bucket = buckets.computeIfAbsent(bucketKey, k -> crearBucket(plan));

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            log.warn("Rate limit excedido — IP: {}, plan: {}, path: {}", clientIp, plan, path);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                    "{\"status\":429,\"message\":\"Demasiadas solicitudes. Intente de nuevo en un momento.\",\"data\":null}"
            );
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }

    private enum Plan {
        AUTH_ESTRICTO,
        EMAIL,
        GENERAL
    }

    private Plan clasificar(String path) {
        if (path.startsWith("/autenticacion/login")
                || path.startsWith("/autenticacion/reset-password")
                || path.startsWith("/autenticacion/bootstrap")
                || path.startsWith("/autenticacion/refresh-token")) {
            return Plan.AUTH_ESTRICTO;
        }
        if (path.startsWith("/email/password")) {
            return Plan.EMAIL;
        }
        return Plan.GENERAL;
    }

    private Bucket crearBucket(Plan plan) {
        return switch (plan) {
            case AUTH_ESTRICTO -> Bucket.builder()
                    .addLimit(Bandwidth.builder()
                            .capacity(10)
                            .refillGreedy(10, Duration.ofMinutes(1))
                            .build())
                    .build();
            case EMAIL -> Bucket.builder()
                    .addLimit(Bandwidth.builder()
                            .capacity(3)
                            .refillGreedy(3, Duration.ofMinutes(1))
                            .build())
                    .build();
            case GENERAL -> Bucket.builder()
                    .addLimit(Bandwidth.builder()
                            .capacity(60)
                            .refillGreedy(60, Duration.ofMinutes(1))
                            .build())
                    .build();
        };
    }

    private String obtenerIpCliente(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
