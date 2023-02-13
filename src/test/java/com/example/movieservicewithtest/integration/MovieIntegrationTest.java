package com.example.movieservicewithtest.integration;

import com.example.movieservicewithtest.entity.Movie;
import com.example.movieservicewithtest.repository.MovieRepository;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class MovieIntegrationTest {

    @LocalServerPort
    private int port;

    private String baseUrl="http://localhost";

    private static RestTemplate restTemplate;

    @Autowired
    private MovieRepository movieRepository;

    @BeforeAll
    public static void init(){
        restTemplate=new RestTemplate();
    }

    private Movie avatarMovie;
    private Movie titanicMovie;

    @BeforeEach
    public void setUp(){
        baseUrl=baseUrl + ":" + port + "/movie";

        avatarMovie = new Movie();
        avatarMovie.setName("Avatar");
        avatarMovie.setGenera("Action");
        avatarMovie.setReleaseDate(LocalDate.of(2000, Month.APRIL, 23));

        titanicMovie = new Movie();
        titanicMovie.setName("Titanic");
        titanicMovie.setGenera("Romance");
        titanicMovie.setReleaseDate(LocalDate.of(2004, Month.JANUARY, 10));

        avatarMovie = movieRepository.save(avatarMovie);
        titanicMovie = movieRepository.save(titanicMovie);
    }

    @AfterEach
    public void tearDown(){
      movieRepository.deleteAll();
    }

    @Test
    void shouldCreateMovieTest(){
        Movie avatarMovie = new Movie();
        avatarMovie.setName("Avatar");
        avatarMovie.setGenera("Action");
        avatarMovie.setReleaseDate(LocalDate.of(2000, Month.APRIL, 23));

        Movie newMovie=restTemplate.postForObject(baseUrl,avatarMovie,Movie.class); 

        assertNotNull(newMovie);
        assertThat(newMovie.getId()).isNotNull();
    }

    @Test
    void shouldFetchMovieTest(){

        List<Movie> list=restTemplate.getForObject(baseUrl, List.class);

        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    void shouldFetchOneMovieTest(){

        Movie existingMovie=restTemplate.getForObject(baseUrl+"/"+avatarMovie.getId(),Movie.class);

        assertNotNull(existingMovie);
        assertEquals("Avatar",existingMovie.getName());
    }

    @Test
    void shouldDeleteMovieTest(){

        restTemplate.delete(baseUrl+"/"+avatarMovie.getId());

        assertEquals(1,movieRepository.findAll().size());
    }

    @Test
    void shouldUpdateMovieTest(){
        avatarMovie.setGenera("Fantacy");

        restTemplate.put(baseUrl+"/{id}",avatarMovie,avatarMovie.getId());

        Movie existingMovie=restTemplate.getForObject(baseUrl+"/"+avatarMovie.getId(),Movie.class);

        assertNotNull(existingMovie);

        assertEquals("Fantacy",existingMovie.getGenera());
    }
}

