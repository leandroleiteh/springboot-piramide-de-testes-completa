package com.example.swplanetapi.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.test.context.jdbc.Sql;

import java.util.stream.Stream;

import static com.example.swplanetapi.common.PlanetConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
public class PlanetRepositoryTest {

    @Autowired
    private PlanetRepository planetRepository;

    @Autowired
    private TestEntityManager entityManager;

    @AfterEach
    public void afterEach() {
        PLANET.setId(null);
    }

    @DisplayName("Teste de ingregração: Testa o metodo que cria o planet")
    @Test
    public void createPlanet_WithValidData_ReturnPlanet() {
        Planet planet = planetRepository.save(PLANET);

        var sut = entityManager.find(Planet.class, planet.getId());

        assertThat(sut).isNotNull();
        assertThat(sut.getName()).isEqualTo(PLANET.getName());
        assertThat(sut.getClimate()).isEqualTo(PLANET.getClimate());
        assertThat(sut.getTerrain()).isEqualTo(PLANET.getTerrain());

    }

    @DisplayName("Teste de integração: Testa o metodo que lança uma exceção quando o planeta é inválido")
    @Test
    public void createPlanet_WithInvalidData_ThrowException() {

        assertThatThrownBy(() -> planetRepository.save(EMPTY_PLANET)).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> planetRepository.save(INVALID_PLANET)).isInstanceOf(RuntimeException.class);

    }
    @DisplayName("Teste Parametrizado: Testa o metodo que lança uma exceção quando o planeta é inválido parametrizandos os dados")
    @ParameterizedTest
    @MethodSource("providersInvalidPlanets")
    public void createPlanet_WithInvalidData_ThrowException_TestParameterized(Planet planetParameterized) {

        assertThatThrownBy(() -> planetRepository.save(planetParameterized)).isInstanceOf(RuntimeException.class);
    }

    private static Stream<Arguments> providersInvalidPlanets(){
        return Stream.of(
          Arguments.of(new Planet(null, "climate", "terrain")),
          Arguments.of(new Planet("name", null, "terrain")),
          Arguments.of(new Planet("name", "climate", null)),
          Arguments.of(new Planet(null, null, "terrain")),
          Arguments.of(new Planet(null, "climate", null)),
          Arguments.of(new Planet("name", null, null)),
          Arguments.of(new Planet(null, null, null)),
          Arguments.of(new Planet("", "", "")),
          Arguments.of(new Planet("name", "", "")),
          Arguments.of(new Planet("", "climate", "")),
          Arguments.of(new Planet("", "", "terrain"))
        );
    }

    @DisplayName("Teste de integração: Testa a impossibilidade de salvar um planeta com o nome já existente")
    @Test
    public void createPlanet_ExistingName_ThrowException() {
        var sut = entityManager.persistFlushFind(PLANET);
        entityManager.detach(sut);
        sut.setId(null);

        assertThatThrownBy(() -> planetRepository.save(sut)).isInstanceOf(RuntimeException.class);

    }

    @DisplayName("Teste de integração: Testa a busca por id e retorna um planet")
    @Test
    public void getPlanet_ByExistingId_ReturnsPlanet() {
        var sut = entityManager.persistFlushFind(PLANET);
        var planetOptional = planetRepository.findById(sut.getId());

        assertThat(planetOptional).isNotEmpty();
        assertThat(planetOptional.get()).isEqualTo(PLANET);

    }

    @DisplayName("Teste de integração: Testa a busca por id e retorna vazio")
    @Test
    public void getPlanet_ByUnexistingId_ReturnsEmpty() {
        var planetOptional = planetRepository.findById(1L);

        assertThat(planetOptional).isEmpty();

    }

    @DisplayName("Teste de integração: Testa a busca por nome retornando um planet")
    @Test
    public void getPlanet_ByExistingName_ReturnPlanet() throws Exception {

        var sut = entityManager.persistFlushFind(PLANET);
        var planetOptional = planetRepository.findByName(sut.getName());

        assertThat(planetOptional).isNotEmpty();
        assertThat(planetOptional.get()).isEqualTo(PLANET);


    }

    @DisplayName("Teste de integração: Testa a busca por nome retornando vazio")
    @Test
    public void getPlanet_ByUneExistingName_ReturnEmpty() throws Exception {
        var planetOptional = planetRepository.findByName(PLANET.getName());

        assertThat(planetOptional).isEmpty();

    }

    @DisplayName("Teste de integração: Testa a busca por filtro retornando planets")
    @Sql(scripts = "/import_planets.sql")
    @Test
    public void listPlanets_ReturnsFilteredPlanets() {
        Example<Planet> queryWithoutFilters = QueryBuilder.makeQuery(new Planet());
        Example<Planet> queryWithFilters = QueryBuilder.makeQuery(new Planet(TATOOINE.getClimate(), TATOOINE.getTerrain()));

        var responseWithoutFilters = planetRepository.findAll(queryWithoutFilters);
        var responseWithFilters = planetRepository.findAll(queryWithFilters);

        assertThat(responseWithoutFilters).isNotEmpty();
        assertThat(responseWithoutFilters).hasSize(3);
        assertThat(responseWithFilters).isNotEmpty();
        assertThat(responseWithFilters).hasSize(1);
        assertThat(responseWithFilters.get(0)).isEqualTo(TATOOINE);

    }

    @DisplayName("Teste de integração: Testa a busca por filtros retornando vazio")
    @Test
    public void listPlanets_ReturnsNoPlanets() {
        Example<Planet> queryWithoutFilters = QueryBuilder.makeQuery(new Planet());

        var response = planetRepository.findAll(queryWithoutFilters);

        assertThat(response).isEmpty();
    }

    @DisplayName("Teste de integração: Testa a remoção de um planet do banco de dados")
    @Test
    public void removePlanet_WithExistingId_RemovePlanetFromDatabase() {
        var sut = entityManager.persistFlushFind(PLANET);

        planetRepository.deleteById(sut.getId());
        assertThat(entityManager.find(Planet.class, sut.getId())).isNull();
        assertThat(planetRepository.findById(sut.getId())).isEmpty();

    }

    @DisplayName("Teste de integração: Testa a remoção de um planet passando um ID invalido")
    @Test
    public void removePlanet_WithInvalidId_ThrowException() {
        final long id = 1L;
        assertThat(entityManager.find(Planet.class, id)).isNull();
        assertThat(planetRepository.findById(id)).isEmpty();
        assertThatThrownBy(() -> planetRepository.deleteById(id)).isInstanceOf(EmptyResultDataAccessException.class);

    }

}
