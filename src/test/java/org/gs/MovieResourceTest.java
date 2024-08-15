package org.gs;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

@QuarkusTest
class MovieResourceTest {

  @InjectMock
  MovieRepository movieRepository;

  @Inject
  MovieResource movieResource;

  private Movie movie;

  @BeforeEach
  void setUp() {

    MockitoAnnotations.openMocks(this);

    movie = new Movie();
    movie.setId(1L);
    movie.setTitle("Test Movie");
    movie.setDescription("Description Test");
    movie.setDirector("Director Test");
    movie.setCountry("Brazil");

  }

  /**
   * Testa o método {@link MovieResource#getAll()} para garantir que ele retorne
   * todos os filmes persistidos.
   * 
   * Cenário: O repositório retorna uma lista de filmes.
   * 
   * Expectativa: O método deve retornar uma reposta com status 200 OK e a lista
   * de filmes.
   */
  @Test
  void getAll() {

    // Given
    List<Movie> movies = Arrays.asList(movie);
    when(movieRepository.listAll()).thenReturn(movies);

    // When
    Response response = movieResource.getAll();

    // Then
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertEquals(movies, response.getEntity());

  }

  /**
   * Testa o método {@link MovieResource#getById(Long)} para garantir que ele
   * retorne um filme pelo ID.
   * 
   * Cenário: O repositório retorna um filme pelo ID especificado.
   * 
   * Expectativa: O método deve retornar uma reposta com status 200 OK e o filme
   * encontrado.
   */
  @Test
  void getByIdOK() {

    // Given
    when(movieRepository.findByIdOptional(1L)).thenReturn(Optional.of(movie));

    // When
    Response response = movieResource.getById(1L);

    // Then
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertEquals(movie, response.getEntity());

  }

  /**
   * Testa o método {@link MovieResource#getById(Long)} para garantir que ele
   * retorne uma resposta 404 Not Found quando o filme não for encontrado pelo ID.
   * 
   * Cenário: O repositório não encontra um filme pelo ID especificado.
   * 
   * Expectativa: O método deve retornar uma reposta com status 404 Not Found.
   */
  @Test
  void getByIdKO() {

    // Given
    when(movieRepository.findByIdOptional(1L)).thenReturn(Optional.empty());

    // When
    Response response = movieResource.getById(1L);

    // Then
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());

  }

  /**
   * Testa o método {@link MovieResource#getByTitle(String)} para garantir que ele
   * retorne um filme pelo título.
   * 
   * Cenário: O repositório encontra um filme pelo título especificado.
   * 
   * Expectativa: O método deve retornar uma reposta com status 200 OK e o filme
   * encontrado pelo título.
   */
  @Test
  void getByTitleOK() {

    // Given
    when(movieRepository.find("title", "Test Movie")).thenReturn(mock(PanacheQuery.class));
    when(movieRepository.find("title", "Test Movie").singleResultOptional()).thenReturn(Optional.of(movie));
    // When
    Response response = movieResource.getByTitle("Test Movie");

    // Then
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertEquals(movie, response.getEntity());

  }

  /**
   * Testa o método {@link MovieResource#getByTitle(String)} para garantir que ele
   * retorne uma resposta 404 Not Found quando o filme não for encontrado pelo
   * título.
   * 
   * Cenário: O repositório não encontra um filme pelo título especificado.
   * 
   * Expectativa: O método deve retornar uma reposta com status 404 Not Found.
   */
  @Test
  void getByTitleKO() {

    // Given
    when(movieRepository.find("title", "Nonexistent Movie")).thenReturn(mock(PanacheQuery.class));
    when(movieRepository.find("title", "Nonexistent Movie")).thenReturn(mock(PanacheQuery.class));

    // When
    Response response = movieResource.getByTitle("Nonexistent Movie");

    // Then
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
  }

  /**
   * Testa o método {@link MovieResource#getByCountry(String)} para garantir que
   * ele
   * retorne um filme pelo país.
   * 
   * Cenário: O repositório encontra um filme pelo país especificado.
   * 
   * Expectativa: O método deve retornar uma reposta com status 200 OK e a lista
   * de filmes
   * encontrado pelo país.
   */
  @Test
  void getByCountry() {
    // Given
    List<Movie> movies = Arrays.asList(movie);
    when(movieRepository.findByCountry("Brazil")).thenReturn(movies);

    // When
    Response response = movieResource.getByCountry("Brazil");

    // Then
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertEquals(movies, response.getEntity());
  }

  /**
   * Testa o método {@link MovieResource#create(Movie)} para garantir que
   * ele
   * crie um filme com sucesso.
   * 
   * Cenário: O filme é persistido corretamente no repositório.
   * 
   * Expectativa: O método deve retornar uma reposta com status 201 Created e o
   * URI do novo recurso.
   */
  @Test
  void createOK() {
    // Given
    when(movieRepository.isPersistent(movie)).thenReturn(true);

    // When
    Response response = movieResource.create(movie);

    // Then
    assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    assertEquals(URI.create("/movies/1"), response.getLocation());
  }

  /**
   * Testa o método {@link MovieResource#create(Movie)} para garantir que
   * ele
   * retorne uma resposta 400 Bad Request quando o filme não for persistido.
   * 
   * Cenário: O filme não é persistido no repositório.
   * 
   * Expectativa: O método deve retornar uma reposta com status 400 Bad Request.
   */
  @Test
  void createKO() {
    // Given
    when(movieRepository.isPersistent(movie)).thenReturn(false);

    // When
    Response response = movieResource.create(movie);

    // Then
    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
  }

  /**
   * Testa o método {@link MovieResource#updateById(Long, Movie)} para
   * garantir que
   * ele
   * atualize um filme com sucesso pelo ID.
   * 
   * Cenário: O filme é encontrado pelo ID e atualizado com sucesso.
   * 
   * Expectativa: O método deve retornar uma reposta com status 200 OK e o filme
   * atualizado.
   */
  @Test
  void updateByIdOK() {
    // Given
    Movie updatedMovie = new Movie();

    updatedMovie.setId(1L);
    updatedMovie.setTitle("Updated Movie");

    when(movieRepository.findByIdOptional(1l)).thenReturn(Optional.of(movie));

    // When
    Response response = movieResource.updateById(1L, updatedMovie);

    // Then
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    Movie result = (Movie) response.getEntity();
    assertEquals("Updated Movie", result.getTitle());

  }

  /**
   * Testa o método {@link MovieResource#updateById(Long, Movie)} para
   * garantir que
   * ele
   * retorne um aresposta 404 Not Found quando o filme não for encontrado pelo ID.
   * 
   * Cenário: O filme não é encontrado pelo ID especificado.
   * 
   * Expectativa: O método deve retornar uma reposta com status 404 Not Found.
   */
  @Test
  void updateByIdKO() {
    // Given
    when(movieRepository.findByIdOptional(1l)).thenReturn(Optional.empty());

    // When
    Response response = movieResource.updateById(1L, movie);

    // Then
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());

  }

  /**
   * Testa o método {@link MovieResource#deleteById(Long)} para
   * garantir que
   * ele
   * delete um filme com sucesso pelo ID.
   * 
   * Cenário: O filme é deletado com sucesso pelo ID.
   * 
   * Expectativa: O método deve retornar uma reposta com status 204 No Content.
   */
  @Test
  void deleteByIdOK() {
    // Given
    when(movieRepository.deleteById(1L)).thenReturn(true);

    // When
    Response response = movieResource.deleteById(1L);

    // Then
    assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

  }

  /**
   * Testa o método {@link MovieResource#deleteById(Long)} para
   * garantir que
   * ele
   * retorne uma resposta 404 Not Found quando o filme não for encontrado pelo ID para exclusão.
   * 
   * Cenário: O filme não é encontrado pelo ID específicado.
   * 
   * Expectativa: O método deve retornar uma reposta com status 404 Not Found.
   */
  @Test
  void deleteByIdKO() {
    // Given
    when(movieRepository.deleteById(1L)).thenReturn(false);

    // When
    Response response = movieResource.deleteById(1L);

    // Then
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
  }
}
