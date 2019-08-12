package es.manuel.vera.silvestre.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event{
    private final String name;
    private final List<String> featuredCrewSymbols;
    private final List<String> bonusCrewSymbols;

    public Event(@JsonProperty("name") String name,
        @JsonProperty("featured_crew") List<Map<String,Object>> featuredCrew,
        @JsonProperty("content") Map<String,Object> bonusCrew){
        this.name = name;
        featuredCrewSymbols =
            featuredCrew.stream().map(f -> f.get("symbol").toString()).collect(Collectors.toList());
        bonusCrewSymbols =
            new ArrayList<>(((Map<String,Integer>) bonusCrew.get("crew_bonuses")).keySet());
    }
}
