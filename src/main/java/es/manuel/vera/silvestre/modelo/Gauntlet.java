package es.manuel.vera.silvestre.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Gauntlet{
    private final Stats primary;
    private final List<String> traits;
    private final List<Slot> slots;

    public Gauntlet(@JsonProperty("contest_data") Map<String,Object> contestData){
        primary = Stats.getStatFromSkillName((String) contestData.get("featured_skill"));
        traits = (List<String>) contestData.get("traits");
        slots = new ArrayList<>();

        getTraits().replaceAll(t -> t.replace('_', ' '));
    }

    public int getScore(){
        return slots.stream().map(Slot::getCrew).mapToInt(crew -> crew.getGauntletScore(traits)).sum();
    }

    @Override
    public String toString(){
        getSlots().sort(Slot::compareTo);
        return getSlots().toString();
    }
}
