package es.manuel.vera.silvestre.modelo.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.manuel.vera.silvestre.modelo.AvatarCrew;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CrewAvatarsResponse{
    private final List<AvatarCrew> avatars;

    @JsonCreator
    public CrewAvatarsResponse(@JsonProperty("crew_avatars") List<AvatarCrew> avatars){
        this.avatars = avatars;
    }
}
