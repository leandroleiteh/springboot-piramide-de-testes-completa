package com.example.swplanetapi.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface PlanetRepository extends CrudRepository<Planet, Long>, QueryByExampleExecutor<Planet> {

  Optional<Planet> findByName(String name);

  @Override
  <S extends Planet> List<S> findAll(Example<S> example);

  @Query("SELECT p " +
          "FROM Planet p " +
          "WHERE (:climate IS NULL OR p.climate = :climate)" +
          " AND (:terrain IS NULL OR p.terrain = :terrain)")
  List<Planet> findAllWithJpqlAndFilter(@Param("climate") String climate, @Param("terrain") String terrain);

  List<Planet> findByClimateContainingIgnoreCaseAndTerrainContainingIgnoreCase(String climate, String terrain);


}
