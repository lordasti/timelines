package es.manuel.vera.silvestre.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipe{
    private final List<Demand> demands;

    public Recipe(@JsonProperty("demands") List<Demand> demands){
        this.demands = demands;
    }
}
