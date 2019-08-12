package es.manuel.vera.silvestre.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PvP{
    private final int accuracy;
    private final int evasion;
    private final int criticalChance;
    private final int criticalBonus;

    public PvP(@JsonProperty("accuracy") int accuracy, @JsonProperty("evasion") int evasion,
        @JsonProperty("crit_chance") int criticalChance, @JsonProperty("crit_bonus") int criticalBonus){
        this.accuracy = accuracy;
        this.evasion = evasion;
        this.criticalChance = criticalChance;
        this.criticalBonus = criticalBonus;
    }
}
