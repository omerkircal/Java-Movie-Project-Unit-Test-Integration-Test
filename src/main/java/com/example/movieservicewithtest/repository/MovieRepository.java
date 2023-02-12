package com.example.movieservicewithtest.repository;

import com.example.movieservicewithtest.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByGenera(String genera);
}