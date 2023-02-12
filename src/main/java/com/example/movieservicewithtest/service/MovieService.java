package com.example.movieservicewithtest.service;

import com.example.movieservicewithtest.entity.Movie;
import com.example.movieservicewithtest.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Movie save(Movie movie) {
        return movieRepository.save(movie);
    }

    public List<Movie> getAllMovies(){
        return movieRepository.findAll();
    }

    public Movie getMovieById(Long id){
        return movieRepository.findById(id).
                orElseThrow(()->new RuntimeException("Movie could not found by id: "+id));
    }

    public Movie updateMovie(Movie movie,Long id){
        Movie existingMovie=movieRepository.findById(id).get();
        existingMovie.setGenera(movie.getGenera());
        existingMovie.setName(movie.getName());
        existingMovie.setReleaseDate(movie.getReleaseDate());
        return movieRepository.save(movie);
    }

    public void deleteMovie(Long id){
        Movie existingMovie = movieRepository.findById(id).get();
        movieRepository.delete(existingMovie);
    }


}
