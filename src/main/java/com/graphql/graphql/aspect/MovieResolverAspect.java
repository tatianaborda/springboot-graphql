package com.graphql.graphql.aspect;

import com.graphql.graphql.repository.MovieRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Aspect
@Component
public class MovieResolverAspect {
    private final MovieRepository movieRepository;

    public MovieResolverAspect(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Before("execution(public * com.graphql.graphql.resolver.MovieResolver.*(..))")
    public void logBeforeAllMethods(JoinPoint joinPoint) {
        System.out.println("Ejecutando método: " + joinPoint.getSignature().getName());
        System.out.println("Argumentos: ");
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            System.out.println("  - " + arg);
        }
    }

    @Before("execution(public * com.graphql.graphql.resolver.MovieResolver.createMovie(..)) || execution(public * com.graphql.graphql.resolver.MovieResolver.updateMovie(..)) || execution(public * com.graphql.graphql.resolver.MovieResolver.deleteMovie(..))")
    public void logBeforeMutations(JoinPoint joinPoint) {
        System.out.println("Ejecutando mutación: " + joinPoint.getSignature().getName());
    }

    @Around("execution(public * com.graphql.graphql.resolver.MovieResolver.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        System.out.println(joinPoint.getSignature() + " ejecutado en " + executionTime + "ms");
        return result;
    }

    @Before("execution(* com.graphql.graphql.resolver.MovieResolver.createMovie(..)) && args(title, director, releaseYear)")
    public void validateCreateMovie(String title, String director, int releaseYear) {
        if (releaseYear > LocalDate.now().getYear()) {
            throw new IllegalArgumentException("El año de lanzamiento no es válido");
        }
    }

    @Before("execution(* com.graphql.graphql.resolver.MovieResolver.deleteMovie(..)) && args(id)")
    public void checkMovieExistsBeforeDelete(JoinPoint joinPoint, Long id) {
        // Verifica si el ID existe en la base de datos
        boolean exists = movieRepository.existsById(id);
        if (!exists) {
            throw new IllegalArgumentException("No se puede eliminar: La película con ID " + id + " no existe.");
        }
        System.out.println("Intentando eliminar la película con ID: " + id);
    }
}

