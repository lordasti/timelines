package es.manuel.vera.silvestre.modelo.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.manuel.vera.silvestre.modelo.ArchetypeItem;
import es.manuel.vera.silvestre.modelo.Player;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerResponse{
    private final Player player;
    private final List<ArchetypeItem> recipes;

    public PlayerResponse(@JsonProperty("player") Player player,
        @JsonProperty("item_archetype_cache") ItemArchetypeCache itemArchetypeCache){
        this.player = player;
        recipes = itemArchetypeCache.getRecipes();
    }
}

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
class ItemArchetypeCache{
    private final List<ArchetypeItem> recipes;

    public ItemArchetypeCache(@JsonProperty("archetypes") List<ArchetypeItem> recipes){
        this.recipes = recipes;
    }
}

