package com.example.swplanetapi;

import com.example.swplanetapi.domain.Planet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;


import static com.example.swplanetapi.common.PlanetConstants.PLANET;
import static com.example.swplanetapi.common.PlanetConstants.TATOOINE;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("it")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = {"/remove_planets.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = {"/import_planets.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class PlanetIT {

    @Autowired
    private TestRestTemplate restTemplate;

//    @Autowired
//    private WebTestClient webClient;

    @Test
    public void createPlanet_ReturnsCreatedPlanet() {

        ResponseEntity<Planet> sut = restTemplate.postForEntity("/planets", PLANET, Planet.class);

        assertThat(sut).isNotNull();
        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(sut.getBody().getId()).isNotNull();
        assertThat(sut.getBody().getName()).isEqualTo(PLANET.getName());
        assertThat(sut.getBody().getTerrain()).isEqualTo(PLANET.getTerrain());
        assertThat(sut.getBody().getClimate()).isEqualTo(PLANET.getClimate());
    }

    @Test
    public void getPlanet_ReturnsPlanet() {
        ResponseEntity<Planet> sut = restTemplate.getForEntity("/planets/" + TATOOINE.getId(), Planet.class);

        assertThat(sut).isNotNull();
        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody()).isEqualTo(TATOOINE);
    }

    @Test
    public void getPlanetsByName_ReturnsPlanet() {
//       var sut = webClient.get()
//                .uri("/planets/" + TATOOINE.getName())
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(Planet.class)
//                .returnResult().getResponseBody();

        ResponseEntity<Planet> sut = restTemplate.getForEntity("/planets/name/" + TATOOINE.getName(), Planet.class);


        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut).isNotNull();
        assertThat(sut.getBody()).isEqualTo(TATOOINE);
    }

    @Test
    public void listPlanets_ReturnAllPlanets() {
        ResponseEntity<Planet[]> sut = restTemplate.getForEntity("/planets", Planet[].class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody()).hasSize(3);
        assertThat(sut.getBody()[0]).isEqualTo(TATOOINE);

    }

    @Test
    public void listPlanetsByTerrain_ReturnsPlanet() {

        ResponseEntity<Planet[]> sut = restTemplate.getForEntity("/planets?terrain=" + TATOOINE.getTerrain(), Planet[].class);

        assertThat(sut).isNotNull();
        assertThat(sut.getBody()).hasSize(1);
        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody()[0]).isEqualTo(TATOOINE);

    }

    @Test
    public void listPlanetsByClimate_ReturnsPlanet() {

        ResponseEntity<Planet[]> sut = restTemplate.getForEntity("/planets?climate=" + TATOOINE.getClimate(), Planet[].class);

        assertThat(sut).isNotNull();
        assertThat(sut.getBody()).hasSize(1);
        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody()[0]).isEqualTo(TATOOINE);
    }

    @Test
    public void listPlanetsByTerrainAndClimate_ReturnsPlanet() {

        ResponseEntity<Planet[]> sut = restTemplate
                .getForEntity(String.format("/planets?climate=%s&terrain=%s", TATOOINE.getClimate(), TATOOINE.getTerrain()), Planet[].class);

        assertThat(sut).isNotNull();
        assertThat(sut.getBody()).hasSize(1);
        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody()[0]).isEqualTo(TATOOINE);
    }

    @Test
    public void removePlanetsById_ReturnsPlanet() {

        ResponseEntity<Void> sut = restTemplate.exchange("/planets/" + TATOOINE.getId(), HttpMethod.DELETE, null, Void.class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

}
