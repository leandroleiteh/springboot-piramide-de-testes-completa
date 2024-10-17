package com.example.swplanetapi.common;

import com.example.swplanetapi.domain.Planet;

import java.util.ArrayList;
import java.util.List;

public class PlanetConstants {
    public static final Planet PLANET = new Planet("name", "climate", "terrain");
    public static final Planet INVALID_PLANET = new Planet("", "", "");
    public static final Planet EMPTY_PLANET = new Planet();

    public static final Planet TATOOINE = new Planet(1L, "Tatooine", "arid", "desert");
    public static final Planet ALDERAAN = new Planet(2L, "Alderran", "temperate", "grass");
    public static final Planet YAVINIV = new Planet(3L, "Yaviniv", "temperate", "tropical");
    public static final List<Planet> PLANET_LIST = new ArrayList<>() {{
        add(TATOOINE);
        add(ALDERAAN);
        add(YAVINIV);
    }};
}
