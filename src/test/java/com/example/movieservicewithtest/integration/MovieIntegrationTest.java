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
//WebEnvironment.RANDOM_PORT bir porta açılarak test gerçekleşir.
//WebEnvironment.MOCK mevcut bir portun davranışları mock'lanarak(kopyalanarak) test edilir.
public class MovieIntegrationTest {

    @LocalServerPort
    private int port;

    private String baseUrl="http://localhost";

    private static RestTemplate restTemplate;
    //RestTemplate, client tarafında senkronize HTTP isteklerini yürütmek için Spring kütüphanesi içindeki default sınıftır.
    //RestTemplate Kısaca java üzerinden Rest servislere ulaşıp response almamızı sağlar.

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
        //unit test gibi değil. Veritabanı kaydı gerçekleşiyor. Id otomatik verildiği için boş bıraktık
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

        Movie newMovie=restTemplate.postForObject(baseUrl,avatarMovie,Movie.class); //url-request object-response type-uri variable

        assertNotNull(newMovie);
        assertThat(newMovie.getId()).isNotNull();
    }

    @Test
    void shouldFetchMovieTest(){

        List<Movie> list=restTemplate.getForObject(baseUrl, List.class);//url-response type

        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    void shouldFetchOneMovieTest(){

        Movie existingMovie=restTemplate.getForObject(baseUrl+"/"+avatarMovie.getId(),Movie.class);//url-response type

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

        restTemplate.put(baseUrl+"/{id}",avatarMovie,avatarMovie.getId());//url-request object-uri variable

        Movie existingMovie=restTemplate.getForObject(baseUrl+"/"+avatarMovie.getId(),Movie.class);

        assertNotNull(existingMovie);

        assertEquals("Fantacy",existingMovie.getGenera());
    }
}
/*
Oluşturulan yazılım modüllerinin, bir araya getirerek doğruluğunu sağlamaktır. Yazılım ürünü için oluşturulan tüm modüller bir araya getirilir ve bu şekilde test edilir. Burada ki amaç: metotlar birim başına testten geçerken, modüller halinde bir araya geldiğinde bazı hatalara sebep oluyor olabilirler. Entegrasyon testleri ile ise bu tarz yazılım ürünü problemlerinin henüz canlı (prod) ortama çıkmadan veya geliştirdiğimiz yeni bir modülün de sorunsuz çalışabileceğinden hızlı bir şekilde emin olabilmemizi sağlamaktadır.

1) Big Bang Integration Test:
En yaygın kullanılan entegrasyon test tipidir. Geliştirilmiş tüm modüller bir araya getirilerek yapılan testtir. Hızlı ve kolay bir şekilde birbirleri ile beraber çalıştıklarında anlam ifade eden modüllerin doğruluğunu sağlar fakat birim başı metot doğruluğunun gözden kaçınılması olasıdır.

2) Top-Down Integration Test:
Bu entegrasyon testindeki amaç ise modüller arası geçiş yapılırken hatalı olan modülün kolay bir şekilde bulunabilmesini sağlamaktır. Test işlemi yukarıdan aşağı doğru gerçekleşmektedir ve her birinin test işleminden başarılı bir şekilde geçerek ilerlemesi gerekmektedir. Her bir modül testleri stub olarak adlandırılmaktadır. Modül ağacının son bacaklarında ise her bir stub kendi içerisinde test edilerek test işlemi sonuçlandırılır.

3) Bottom-Up Integration Test:
Bu test yöntemi ise Unit Testler ile beraber ilerlemektedir. Alt tarafta bulunan tüm stublar, Unit Testlerden geçirilerek yukarıya doğru ilerlenir. Top-Down’da olduğu gibi yukarıya ilerlerken Unit Testler aracılığı ile her test başarılı olarak sonuçlanmalıdır. Tüm stublar için Unit Testler oluşturulduktan sonra bir üst seviyede hepsi bir ele alınarak test işlemi yapılır. Bu test tipindeki amaç ise stublardan başlayarak hataların en kısa sürede bulunabilmesidir.

4) Sandwich/Hybrid Integration Test:
Modüllerin bir kısmı Top-Down, bir diğer kısmı ise Bottom-Up tiplerini kullanılarak gerçekleştirilen test tipidir. Bu karma tipteki amaç ise bazı modülleri gruplara ayırabilirken diğer modülleri ise ayrı bir şekilde test edebilmektir.


 */
