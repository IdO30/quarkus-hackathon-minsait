package org.gs;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.quarkus.test.junit.QuarkusTest;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;
import org.junit.jupiter.api.*;
import java.util.List;

@QuarkusTest
@Tag("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MovieResourceTestIT {

  private static final String BASE_URL = "/movies";

  /**
   * Testa a obtenção de todos os filmes.
   * Verifica se a resposta é 200(Bem-sucedida) e se a lista de filmes não é nula.
   */
  @Test
  @Order(1)
  void getAll() {
    List<Movie> movies = given()
        .when().get(BASE_URL)
        .then()
        .statusCode(200)
        .extract().jsonPath().getList("$", Movie.class);

    assertNotNull(movies);

  }

  /**
   * Testa a obtenção de um filme por ID válido.
   * Verifica se a resposta é 200(Bem-sucedida) e se o ID do filme corresponde ao
   * esperado.
   */
  @Test
  @Order(1)
  void getById() {
    given()
        .when().get(BASE_URL + "/1")
        .then()
        .statusCode(200)
        .body("id", equalTo(1));
  }

  /**
   * Testa a obtenção de um filme por ID inválido.
   * Verifica se a resposta é 404(Não Encontrado) quando o ID não existe.
   * esperado.
   */
  @Test
  @Order(1)
  void getByIdKO() {
    given()
        .when().get(BASE_URL + "/999")
        .then()
        .statusCode(404);
  }

  /**
   * Testa a obtenção de um filme por título válido.
   * Verifica se a resposta é 200(Bem-sucedida) e se o título do filme
   * corresponde ao esperado.
   */
  @Test
  @Order(1)
  void getByTitle() {
    given()
        .when().get(BASE_URL + "/title/FirstMovie")
        .then()
        .statusCode(200)
        .body("title", equalTo("FirstMovie"));
  }

  /**
   * Testa a obtenção de um filme por título inválido.
   * Verifica se a resposta é 404(Não Encontrado) quando o título não existe.
   * esperado.
   */
  @Test
  @Order(1)
  void getByTitleKO() {
    given()
        .when().get(BASE_URL + "/title/FirstMovie23")
        .then()
        .statusCode(404);
  }

  /**
   * Testa a obtenção de um filme por país válido.
   * Verifica se a resposta é 200(Bem-sucedida), se a lista de filmes não é vazia
   * e se o país do primeiro filme
   * corresponde ao esperado.
   */
  @Test
  @Order(2)
  void getByCountry() {

    List<Movie> movies = given()
        .when().get(BASE_URL + "/country/Planet")
        .then()
        .statusCode(200)
        .extract().jsonPath().getList("$", Movie.class);

    assertNotNull(movies);
    assertEquals("Planet", movies.get(0).getCountry());

  }

  /**
   * Testa a obtenção de um filme por país inválido.
   * Verifica se a resposta é é 200(Bem-sucedida), mas a lista de filmes estará
   * vazia.
   */
  @Test
  @Order(2)
  void getByCountryKO() {
    List<Movie> movies = given()
        .when().get(BASE_URL + "/country/Planet23")
        .then()
        .statusCode(200)
        .extract().jsonPath().getList("$", Movie.class);

    assertEquals(0, movies.size());

  }

  /**
   * Testa a criação de um novo filme.
   * Verifica se a resposta é 201 (Criado) e se o cabeçalho "Location" contém o
   * URI do novo filme.
   */
  @Test
  @Order(3)
  void create() {
    JsonObject newMovie = (JsonObject) Json.createObjectBuilder()
        .add("title", "New Movie Test")
        .add("description", "New Movie Test Description")
        .add("director", "Director Test New Movie")
        .add("country", "Brazil")
        .build();

    given()
        .contentType(MediaType.APPLICATION_JSON)
        .body(newMovie.toString())
        .when().post(BASE_URL)
        .then()
        .statusCode(201)
        .header("Location", containsString("/movies/"));

  }

  /**
   * Testa a atualização de um filme existente.
   * Verifica se a resposta é bem-sucedida e se o título do filme foi atualizado
   * corretamente.
   */
  @Test
  @Order(4)
  void updateById() {
    JsonObject updatedMovie = (JsonObject) Json.createObjectBuilder()
        .add("title", "Updated Movie Test")
        .add("description", "Updated Movie Test Description")
        .add("director", "Director Test Updated Movie")
        .add("country", "Brazil")
        .build();

    given()
        .contentType(MediaType.APPLICATION_JSON)
        .body(updatedMovie.toString())
        .when().put(BASE_URL + "/1")
        .then()
        .statusCode(200)
        .body("title", equalTo("Updated Movie Test"));
  }

  /**
   * Testa a atualização de um filme com ID inválido.
   * Verifica se a resposta é 404 (Não Encontrado) quando o ID não existe.
   */
  @Test
  @Order(4)
  void updateByIdKO() {
    JsonObject updatedMovie = (JsonObject) Json.createObjectBuilder()
        .add("title", "Updated Movie Test")
        .build();

    given()
        .contentType(MediaType.APPLICATION_JSON)
        .body(updatedMovie.toString())
        .when().put(BASE_URL + "/999")
        .then()
        .statusCode(404);
  }

  /**
   * Testa a exclusão de um filme existente.
   * Verifica se a resposta é 204 (Sem Conteúdo) após a exclusão.
   */
  @Test
  @Order(5)
  void deleteById() {

    given()
        .when().delete(BASE_URL + "/1")
        .then()
        .statusCode(204);
  }

  /**
   * Testa a exclusão de um filme com ID inválido.
   * Verifica se a resposta é 404 (Não Encontrado) quando o ID não existe.
   */
  @Test
  @Order(5)
  void deleteByIdKO() {
    given()
        .when().delete(BASE_URL + "/999")
        .then()
        .statusCode(404);
  }
}
