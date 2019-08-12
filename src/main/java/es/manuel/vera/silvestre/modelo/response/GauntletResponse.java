package es.manuel.vera.silvestre.modelo.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import es.manuel.vera.silvestre.modelo.Gauntlet;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GauntletResponse{
    private final Gauntlet gauntlet;

    @JsonCreator
    public GauntletResponse(Gauntlet gauntlet){
        this.gauntlet = gauntlet;
    }
}
