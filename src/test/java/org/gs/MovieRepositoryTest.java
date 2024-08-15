package org.gs;

import static org.junit.jupiter.api.Assertions.*;

import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;

@QuarkusTest
class MovieRepositoryTest {

  @Inject
  MovieRepository movieRepository;

/**
 * Teste o método findByCountry() para garantir que ele retorne filmes corretamente quando exitem filmes persistidos com o país passado.
 * 
 * Cenário: O banco de dados contém um filme com o país "Brazil".
 * 
 * Expectativa: O método deve retornar uma lista contendo o filme com o país "Brazil".
 */

  @Test
  @Transactional
  void findByCountryOK() {
    
    //Given
    Movie movie = new Movie();
    movie.setTitle("Test Movie");
    movie.setDescription("Description");
    movie.setDirector("Director");
    movie.setCountry("Brazil");
    
    movieRepository.persist(movie);

    //When
    List<Movie> movies = movieRepository.findByCountry("Brazil");

    //Then

    assertNotNull(movies);
    assertEquals(1, movies.size());
    assertEquals("Brazil", movies.get(0).getCountry());
    assertEquals("Test Movie", movies.get(0).getTitle());

    
  }

/**
 * Testa o método findByCountry() para garantir que ele retorne uma lista vazia quando não existem filmes com o país passado.
 * 
 *Cenário: O banco de dados não contém filmes com o país "NonExistentCountry".
 * 
 *Expectativa: O método deve retornar uma lista vazia.
 */

  @Test
  void findByCountryKO() {

    //When
    List<Movie> movies = movieRepository.findByCountry("NonExistentCountry");

    //Then
    assertNotNull(movies);
    assertTrue(movies.isEmpty());

  }
}
