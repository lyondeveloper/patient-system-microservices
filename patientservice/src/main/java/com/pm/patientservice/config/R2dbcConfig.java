package com.pm.patientservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;

import java.util.List;

@Configuration
public class R2dbcConfig extends AbstractR2dbcConfiguration {

    @Override
    protected List<Object> getCustomConverters() {
        return List.of(new IntegerToLongConverter());
    }

    @Override
    public io.r2dbc.spi.ConnectionFactory connectionFactory() {
        // Este método debería devolver tu ConnectionFactory
        // Pero Spring Boot lo configurará automáticamente si usas spring.r2dbc.* properties
        // Por lo tanto, podemos devolver null en este caso
        return null;
    }
}
