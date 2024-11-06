package com.graphql.graphql.actuator;

import com.graphql.graphql.repository.MovieRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class HealthChecker implements HealthIndicator {

    private final MovieRepository movieRepository;

    public HealthChecker(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public Health health() {
        if (movieRepository.count() > 0) {
            return Health.up().withDetail("message", "Base de datos de películas está operativa").build();
        } else {
            return Health.down().withDetail("message", "No se encontraron películas en la base de datos").build();
        }
    }
}