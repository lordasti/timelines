package es.manuel.vera.silvestre.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Voyage implements Comparable<Voyage>{
    private final List<Slot> slots;
    private final String shipTrait;
    private final BonusStats bonusStats;
    private Integer voyageEstimate;

    public Voyage(@JsonProperty("crew_slots") List<Map<String,String>> slotsMap,
        @JsonProperty("ship_trait") String shipTrait,
        @JsonProperty("skills") Map<String,String> bonusSkills){
        slots = IntStream.range(0, slotsMap.size()).mapToObj(i -> {
            Map<String,String> slotMap = slotsMap.get(i);
            Stats stat = Stats.getStatFromSkillName(slotMap.get("skill"));
            return new Slot(i, slotMap.get("name"), stat, slotMap.get("trait"));
        }).collect(Collectors.toList());
        this.shipTrait = shipTrait.replace('_', ' ');
        bonusStats = new BonusStats(Stats.getStatFromSkillName(bonusSkills.get("primary_skill")),
            Stats.getStatFromSkillName(bonusSkills.get("secondary_skill")));
        voyageEstimate = 0;
    }

    public int getCommand(){
        return getSkillScore(Stats.COMMAND);
    }

    private int getSkillScore(Stats stat){
        return getSlots().stream().map(Slot::getCrew).map(crew -> crew.getSkill(stat))
            .mapToInt(Skill::getAvgTotal).sum();
    }

    public int getDiplomacy(){
        return getSkillScore(Stats.DIPLOMACY);
    }

    public int getSecurity(){
        return getSkillScore(Stats.SECURITY);
    }

    public int getEngineering(){
        return getSkillScore(Stats.ENGINEERING);
    }

    public int getScience(){
        return getSkillScore(Stats.SCIENCE);
    }

    public int getMedicine(){
        return getSkillScore(Stats.MEDICINE);
    }

    public List<String> getTraits(){
        return getSlots().stream().map(Slot::getTrait).collect(Collectors.toList());
    }

    @Override
    public String toString(){
        getSlots().sort(Slot::compareTo);
        return getSlots().toString();
    }

    @Override
    public int compareTo(Voyage other){
        return Double.compare(other.getVoyageEstimate(), getVoyageEstimate());
    }
}
