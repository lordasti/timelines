package es.manuel.vera.silvestre.modelo.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.manuel.vera.silvestre.modelo.Voyage;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VoyageResponse{
    private final Voyage voyage;

    @JsonCreator
    public VoyageResponse(@JsonProperty("voyage_descriptions") Voyage voyage){
        this.voyage = voyage;
    }
}
