package com.graphql.graphql.resolver;

import com.graphql.graphql.model.Movie;
import com.graphql.graphql.repository.MovieRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class MovieResolver {
    private final MovieRepository movieRepository;

    public MovieResolver(MovieRepository movieRepository){
        this.movieRepository = movieRepository;
    }

    @QueryMapping
    public List<Movie> getAllMovies(){
        return movieRepository.findAll();
    }
    @QueryMapping
    public Movie getMovie(@Argument Long id){
        return movieRepository.findById(id)
        .orElse(null);
    }

    @MutationMapping
    public  Movie createMovie(@Argument String title, @Argument String director, @Argument int releaseYear){
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setDirector(director);
        movie.setReleaseYear(releaseYear);
        return movieRepository.save(movie);
    }

    @MutationMapping
    public Movie updateMovie(@Argument Long id, @Argument String title, @Argument String director, @Argument int releaseYear){
        return movieRepository.findById(id).map(movie -> {
            if(title != null) movie.setTitle(title);
            if(director != null) movie.setDirector(director);
            if(releaseYear !=0) movie.setReleaseYear(releaseYear);
            return movieRepository.save(movie);
        }).orElse(null);
    }

    @MutationMapping
    public Boolean deleteMovie(@Argument Long id){
        if(movieRepository.existsById(id)){
            movieRepository.deleteById(id);
            return true;
        }
        return false;
    }
}



