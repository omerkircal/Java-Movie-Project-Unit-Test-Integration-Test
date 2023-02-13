package com.example.movieservicewithtest.controller;

import com.example.movieservicewithtest.entity.Movie;
import com.example.movieservicewithtest.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class )
class MovieControllerTest {


    private MockMvc mockMvc;


    ObjectMapper objectMapper=new ObjectMapper().registerModule(new JavaTimeModule());



    @Mock
    private MovieService movieService;

    @InjectMocks
    private MovieController movieController;

   Movie avatarMovie=new Movie(1L,"Avatar","Action",LocalDate.of(1999, Month.APRIL, 22));
   Movie titanicMovie=new Movie(2L,"Titanic","Romance",LocalDate.of(2004, Month.JANUARY, 10));

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); 

        this.mockMvc= MockMvcBuilders.standaloneSetup(movieController).build();
    }

    @Test
    void shouldCreateNewMovie() throws Exception{
        when(movieService.save(any(Movie.class))).thenReturn(avatarMovie);

        mockMvc.perform(post("/movie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(avatarMovie)))
                .andExpect(status().isCreated()) 
                .andExpect(jsonPath("$.name", is(avatarMovie.getName())))
                .andExpect(jsonPath("$.genera", is(avatarMovie.getGenera())))
                .andExpect(jsonPath("$.releaseDate", Matchers.is(avatarMovie.getReleaseDate().toString()))); 
    }


    @Test
    void shouldFetchAllMovies() throws Exception {

        List<Movie> list = new ArrayList<>();
        list.add(avatarMovie);
        list.add(titanicMovie);

        when(movieService.getAllMovies()).thenReturn(list);

        this.mockMvc.perform(get("/movie"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(list.size())));
    }

    @Test
    void shouldFetchOneMovieById() throws Exception {

        when(movieService.getMovieById(anyLong())).thenReturn(avatarMovie);

        this.mockMvc.perform(get("/movie/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(avatarMovie.getName())))
                .andExpect(jsonPath("$.genera", is(avatarMovie.getGenera())));
    }

    @Test
    void shouldDeleteMovie() throws Exception {

        doNothing().when(movieService).deleteMovie(anyLong());

        this.mockMvc.perform(delete("/movie/{id}", 1L))
                .andExpect(status().isNoContent());

    }

    @Test
    void shouldUpdateMovie() throws Exception {

        when(movieService.updateMovie(any(Movie.class), anyLong())).thenReturn(avatarMovie);
        this.mockMvc.perform(put("/movie/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(avatarMovie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(avatarMovie.getName())))
                .andExpect(jsonPath("$.genera", is(avatarMovie.getGenera())));
    }
}
