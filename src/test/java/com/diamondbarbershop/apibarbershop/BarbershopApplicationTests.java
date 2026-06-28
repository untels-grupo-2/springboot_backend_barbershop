package com.diamondbarbershop.apibarbershop;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requiere BD y variables de entorno — se habilitará en PB-23 con Testcontainers")
class BarbershopApplicationTests {

    @Test
    void contextLoads() {
    }

}
