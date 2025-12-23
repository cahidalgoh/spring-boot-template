package es.nextdigital.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI() .info(new Info()
                .title("API de Cajero Bancario")
                .version("1.0")
                .description("Endpoints para depósitos, retiros, transferencias y consulta de movimientos")
                .contact(new Contact() .name("Equipo NextDigital") .email("soporte@nextdigital.es"))
        );
    }
}
