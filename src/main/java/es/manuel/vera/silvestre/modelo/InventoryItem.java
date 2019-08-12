package es.manuel.vera.silvestre.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryItem extends Item{
    private final int quantity;

    public InventoryItem(@JsonProperty("type") int type, @JsonProperty("symbol") String symbol,
        @JsonProperty("name") String name, @JsonProperty("flavor") String flavor,
        @JsonProperty("archetype_id") long archetypeId, @JsonProperty("quantity") int quantity,
        @JsonProperty("rarity") int stars){
        super(type, symbol, name, flavor, archetypeId, stars);
        this.quantity = quantity;
    }
}
