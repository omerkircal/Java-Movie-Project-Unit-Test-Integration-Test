package com.example.movieservicewithtest.service;

import com.example.movieservicewithtest.entity.Movie;
import com.example.movieservicewithtest.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie avatarMovie;
    private Movie titanicMovie;

    @BeforeEach
    void setUp() {
        avatarMovie = new Movie();
        avatarMovie.setId(1L);
        avatarMovie.setName("Avatar");
        avatarMovie.setGenera("Action");
        avatarMovie.setReleaseDate(LocalDate.of(2000, Month.APRIL, 23));

        titanicMovie = new Movie();
        titanicMovie.setId(2L);
        titanicMovie.setName("Titanic");
        titanicMovie.setGenera("Romance");
        titanicMovie.setReleaseDate(LocalDate.of(2004, Month.JANUARY, 10));

    }

    @Test
    void save() {
        when(movieRepository.save(any(Movie.class))).thenReturn(avatarMovie);

        Movie newMovie=movieService.save(avatarMovie);

        assertNotNull(newMovie);

        assertThat(newMovie.getName()).isEqualTo("Avatar");


    }

    @Test
    void getAllMovies() {
        List<Movie> list=new ArrayList<>();
        list.add(avatarMovie);
        list.add(titanicMovie);

        when(movieRepository.findAll()).thenReturn(list);

        List<Movie> movies=movieService.getAllMovies();

        assertEquals(2,movies.size());
        assertNotNull(movies);
    }

    @Test
    void getMovieById() {
        when(movieRepository.findById(anyLong())).thenReturn(Optional.of(avatarMovie));
        Movie existingMovie=movieService.getMovieById(avatarMovie.getId());

        assertNotNull(existingMovie);
        assertThat(existingMovie.getId()).isNotEqualTo(null);
    }

    @Test
    void getMovieByIdForException(){
        when(movieRepository.findById(2L)).thenReturn(Optional.of(avatarMovie));

        assertThrows(RuntimeException.class,()-> movieService.getMovieById(avatarMovie.getId()));
    }

    @Test
    void updateMovie() {
        when(movieRepository.findById(anyLong())).thenReturn(Optional.of(avatarMovie));

        when(movieRepository.save(any(Movie.class))).thenReturn(avatarMovie);
        avatarMovie.setGenera("Fantacy");
        Movie existingMovie=movieService.updateMovie(avatarMovie, avatarMovie.getId());

        assertNotNull(existingMovie);
        assertEquals("Fantacy",avatarMovie.getGenera());
    }

    @Test
    void deleteMovie() {
        Long movieId=1L;

        when(movieRepository.findById(anyLong())).thenReturn(Optional.of(avatarMovie));
        doNothing().when(movieRepository).delete(any(Movie.class)); //void dönen metodlarda doNothing()

        movieService.deleteMovie(movieId);

        verify(movieRepository,times(1)).delete(avatarMovie);
    }
}
