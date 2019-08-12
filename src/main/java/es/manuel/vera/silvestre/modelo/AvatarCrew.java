package es.manuel.vera.silvestre.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AvatarCrew extends Crew{

    public AvatarCrew(@JsonProperty("id") long id, @JsonProperty("symbol") String symbol,
        @JsonProperty("name") String name, @JsonProperty("short_name") String shortName,
        @JsonProperty("max_rarity") int maxStars, @JsonProperty("traits") List<String> traits,
        @JsonProperty("traits_hidden") List<String> hiddenTraits, @JsonProperty("skills") List<String> skills){
        super(id, 0, symbol, name, shortName, "", 0, 0, maxStars, false, traits, hiddenTraits,
            new ArrayList<>(), new ArrayList<>(), null, true);
    }
}
