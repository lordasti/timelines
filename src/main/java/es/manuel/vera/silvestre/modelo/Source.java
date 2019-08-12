package es.manuel.vera.silvestre.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Source{
    private final int type;//0-Away Team, 1-Transmission, 2-Ship Battle, 3-Node Reward
    private final long id;
    private final String name;
    private final double energyQuotient;
    private final int chanceGrade;
    private final int mission;
    private final int dispute;
    private final int mastery;

    public Source(@JsonProperty("type") int type, @JsonProperty("id") long id, @JsonProperty("name") String name,
        @JsonProperty("energy_quotient") double energyQuotient, @JsonProperty("chance_grade") int chanceGrade,
        @JsonProperty("mission") int mission, @JsonProperty("dispute") int dispute,
        @JsonProperty("mastery") int mastery){
        this.type = type;
        this.id = id;
        this.name = name;
        this.energyQuotient = energyQuotient;
        this.chanceGrade = chanceGrade;
        this.mission = mission;
        this.dispute = dispute;
        this.mastery = mastery;
    }
}
