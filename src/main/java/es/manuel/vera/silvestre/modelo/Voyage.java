package es.manuel.vera.silvestre.modelo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class Voyage implements Comparable<Voyage>{
    private List<Slot> slots;
    private Double voyageEstimate;

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
