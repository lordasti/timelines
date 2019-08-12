package es.manuel.vera.silvestre.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Demand{
    private final long archetypeId;
    private final int count;

    public Demand(@JsonProperty("archetype_id") long archetypeId, @JsonProperty("count") int count){
        this.archetypeId = archetypeId;
        this.count = count;
    }
}
