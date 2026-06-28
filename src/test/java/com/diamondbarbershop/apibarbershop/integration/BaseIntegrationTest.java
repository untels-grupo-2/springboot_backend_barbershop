package com.diamondbarbershop.apibarbershop.integration;

import com.diamondbarbershop.apibarbershop.shared.domain.port.out.SubidorImagen;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @MockBean
    protected SubidorImagen subidorImagen;

    @MockBean
    protected JavaMailSender javaMailSender;

    static final MySQLContainer<?> mysql;

    static {
        mysql = new MySQLContainer<>("mysql:8.0.3")
                .withDatabaseName("barbershop_test")
                .withUsername("test")
                .withPassword("test")
                .withUrlParam("useSSL", "false")
                .withUrlParam("allowPublicKeyRetrieval", "true");
        mysql.start();
    }

    @DynamicPropertySource
    static void configurarDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }
}
