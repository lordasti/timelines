package es.manuel.vera.silvestre.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArchetypeItem extends Item{
    private final Recipe recipe;
    private final List<Source> sources;

    public ArchetypeItem(@JsonProperty("type") int type, @JsonProperty("symbol") String symbol,
        @JsonProperty("name") String name, @JsonProperty("flavor") String flavor,
        @JsonProperty("id") long archetypeId, @JsonProperty("rarity") int stars,
        @JsonProperty("recipe") Recipe recipe, @JsonProperty("item_sources") List<Source> sources){
        super(type, symbol, name, flavor, archetypeId, stars);
        this.recipe = recipe;
        this.sources = sources;
    }
}
