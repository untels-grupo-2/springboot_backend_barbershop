package com.diamondbarbershop.apibarbershop.shared.infrastructure.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RateLimitFilterTest {

    private RateLimitFilter filter;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new RateLimitFilter();
        filterChain = mock(FilterChain.class);
    }

    @Test
    @DisplayName("Requests dentro del limite pasan correctamente")
    void should_allowRequests_withinLimit() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/servicios");
        request.setRemoteAddr("192.168.1.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("Login excede limite estricto de 10 req/min y recibe 429")
    void should_block_when_loginExceedsLimit() throws ServletException, IOException {
        for (int i = 0; i < 10; i++) {
            MockHttpServletRequest request = new MockHttpServletRequest("POST", "/autenticacion/login");
            request.setRemoteAddr("10.0.0.1");
            MockHttpServletResponse response = new MockHttpServletResponse();
            filter.doFilterInternal(request, response, filterChain);
        }

        MockHttpServletRequest blockedRequest = new MockHttpServletRequest("POST", "/autenticacion/login");
        blockedRequest.setRemoteAddr("10.0.0.1");
        MockHttpServletResponse blockedResponse = new MockHttpServletResponse();
        filter.doFilterInternal(blockedRequest, blockedResponse, filterChain);

        assertThat(blockedResponse.getStatus()).isEqualTo(429);
        assertThat(blockedResponse.getContentAsString()).contains("Demasiadas solicitudes");
    }

    @Test
    @DisplayName("Email password excede limite de 3 req/min y recibe 429")
    void should_block_when_emailExceedsLimit() throws ServletException, IOException {
        for (int i = 0; i < 3; i++) {
            MockHttpServletRequest request = new MockHttpServletRequest("POST", "/email/password");
            request.setRemoteAddr("10.0.0.2");
            MockHttpServletResponse response = new MockHttpServletResponse();
            filter.doFilterInternal(request, response, filterChain);
        }

        MockHttpServletRequest blockedRequest = new MockHttpServletRequest("POST", "/email/password");
        blockedRequest.setRemoteAddr("10.0.0.2");
        MockHttpServletResponse blockedResponse = new MockHttpServletResponse();
        filter.doFilterInternal(blockedRequest, blockedResponse, filterChain);

        assertThat(blockedResponse.getStatus()).isEqualTo(429);
    }

    @Test
    @DisplayName("IPs diferentes tienen limites independientes")
    void should_trackLimits_independently_perIp() throws ServletException, IOException {
        for (int i = 0; i < 10; i++) {
            MockHttpServletRequest request = new MockHttpServletRequest("POST", "/autenticacion/login");
            request.setRemoteAddr("10.0.0.10");
            filter.doFilterInternal(request, new MockHttpServletResponse(), filterChain);
        }

        MockHttpServletRequest otherIpRequest = new MockHttpServletRequest("POST", "/autenticacion/login");
        otherIpRequest.setRemoteAddr("10.0.0.99");
        MockHttpServletResponse otherIpResponse = new MockHttpServletResponse();
        filter.doFilterInternal(otherIpRequest, otherIpResponse, filterChain);

        assertThat(otherIpResponse.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("OPTIONS requests no se limitan (CORS preflight)")
    void should_skip_optionsRequests() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/autenticacion/login");
        request.setRemoteAddr("10.0.0.3");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Actuator y Swagger no pasan por el filtro")
    void should_notFilter_actuatorAndSwagger() {
        MockHttpServletRequest actuator = new MockHttpServletRequest("GET", "/actuator/health");
        MockHttpServletRequest swagger = new MockHttpServletRequest("GET", "/swagger-ui/index.html");
        MockHttpServletRequest apiDocs = new MockHttpServletRequest("GET", "/v3/api-docs/openapi.json");

        assertThat(filter.shouldNotFilter(actuator)).isTrue();
        assertThat(filter.shouldNotFilter(swagger)).isTrue();
        assertThat(filter.shouldNotFilter(apiDocs)).isTrue();
    }

    @Test
    @DisplayName("X-Forwarded-For se usa como IP del cliente cuando esta presente")
    void should_useXForwardedFor_whenPresent() throws ServletException, IOException {
        for (int i = 0; i < 10; i++) {
            MockHttpServletRequest request = new MockHttpServletRequest("POST", "/autenticacion/login");
            request.setRemoteAddr("127.0.0.1");
            request.addHeader("X-Forwarded-For", "203.0.113.50, 70.41.3.18");
            filter.doFilterInternal(request, new MockHttpServletResponse(), filterChain);
        }

        MockHttpServletRequest blockedRequest = new MockHttpServletRequest("POST", "/autenticacion/login");
        blockedRequest.setRemoteAddr("127.0.0.1");
        blockedRequest.addHeader("X-Forwarded-For", "203.0.113.50");
        MockHttpServletResponse blockedResponse = new MockHttpServletResponse();
        filter.doFilterInternal(blockedRequest, blockedResponse, filterChain);

        assertThat(blockedResponse.getStatus()).isEqualTo(429);

        MockHttpServletRequest differentIp = new MockHttpServletRequest("POST", "/autenticacion/login");
        differentIp.setRemoteAddr("127.0.0.1");
        differentIp.addHeader("X-Forwarded-For", "198.51.100.1");
        MockHttpServletResponse differentIpResponse = new MockHttpServletResponse();
        filter.doFilterInternal(differentIp, differentIpResponse, filterChain);

        assertThat(differentIpResponse.getStatus()).isEqualTo(200);
    }
}
