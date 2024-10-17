package com.example.swplanetapi.web;

import com.example.swplanetapi.domain.PlanetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.swplanetapi.common.PlanetConstants.*;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlanetController.class)
class PlanetControllerTest {

    @MockBean
    private PlanetService planetService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("Teste de integração: Criando planetas retornando com sucesso na controller")
    @Test
    public void createPlanet_WithValidData_ReturnsPlanet() throws Exception {
        when(planetService.create(PLANET)).thenReturn(PLANET);

        mockMvc
                .perform(post("/planets").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(PLANET)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(PLANET));
    }

    @DisplayName("Teste de integração: lançando exceção por dados estarem inválidos")
    @Test
    public void createPlanet_WithIvalidData_ReturnsPlanet() throws Exception {

        mockMvc
                .perform(post("/planets").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(INVALID_PLANET)))
                .andExpect(status().isUnprocessableEntity());
        mockMvc
                .perform(post("/planets").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(EMPTY_PLANET)))
                .andExpect(status().isUnprocessableEntity());
    }

    @DisplayName("Teste de integração: : lançando exceção por conflito")
    @Test
    public void createPlanet_WithExistingName_ReturnsConflict() throws Exception {
        when(planetService.create(any())).thenThrow(DataIntegrityViolationException.class);

        mockMvc
                .perform(post("/planets").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(PLANET)))
                .andExpect(status().isConflict());

    }

    @DisplayName("Teste de integração: Testa a busca por id e retorna um planet e status code ok")
    @Test
    public void getPlanet_ByExistingId_ReturnsPlanet() throws Exception {
        when(planetService.get(1L)).thenReturn(Optional.of(PLANET));

        mockMvc
                .perform(get("/planets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(PLANET));

    }

    @DisplayName("Teste de integração: Testa a busca por id e retorna not found")
    @Test
    public void getPlanet_ByUnexistingId_ReturnsNotFound() throws Exception {
        mockMvc
                .perform(get("/planets/1"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Teste de integração: Testa a busca por nome retornando um planet e status code ok")
    @Test
    public void getPlanet_ByExistingName_ReturnPlanet() throws Exception {
        when(planetService.getByName("terra")).thenReturn(Optional.of(PLANET));

        mockMvc.perform(get("/planets/name/terra"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(PLANET));


    }

    @DisplayName("Teste de integração: Testa a busca por nome retornando not found")
    @Test
    public void getPlanet_ByUneExistingName_ReturnNotFound() throws Exception {

        mockMvc.perform(get("/planets/name/terra"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Teste de integração: Testa a busca por filtros e retorna lista e status code ok")
    @Test
    public void listPlanets_ReturnsFilteredPlanets() throws Exception {
        when(planetService.list(null, null)).thenReturn(PLANET_LIST);
        when(planetService.list(TATOOINE.getTerrain(), TATOOINE.getClimate())).thenReturn(List.of(TATOOINE));

        mockMvc.perform(get("/planets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        mockMvc.perform(get("/planets?" + String.format("terrain=%s&climate=%s", TATOOINE.getTerrain(), TATOOINE.getClimate())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]").value(TATOOINE));

    }

    @DisplayName("Teste de integração: Testa a busca por filtros e retorna lista vazia")
    @Test
    public void listPlanets_ReturnsEmpty() throws Exception {
        when(planetService.list(null, null)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/planets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @DisplayName("Teste de integração: Remove planets passando Id existente retorna NoContent")
    @Test
    public void removePlanet_WithExistingId_ReturnnoContent() throws Exception {

        mockMvc.perform(delete("/planets/1")).andExpect(status().isNoContent())
        .andExpect(jsonPath("$").doesNotExist());
    }

    @DisplayName("Teste de integração: Remove planets passando id inexistente retorna notFound")
    @Test
    public void removePlanet_WithUnexisitingId_ReturnNotFound() throws Exception {
        final long id = 1L;
        doThrow(new EmptyResultDataAccessException(1)).when(planetService).remove(id);

        mockMvc.perform(delete("/planets/" + id)).andExpect(status().isNotFound());

    }

}