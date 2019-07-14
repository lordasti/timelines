package es.manuel.vera.silvestre.modelo;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Data
public class Voyage{
    private final List<Slot> slots;
    private final BonusStats bonusStats;
    private final int antimatter;

    public Voyage(BonusStats bonusStats, int antimatter){
        Slot firstOfficer = new Slot("First Officer", Stats.COMMAND);
        Slot helmOfficer = new Slot("Helm Officer", Stats.COMMAND);

        Slot diplomat = new Slot("Diplomat", Stats.DIPLOMACY);
        Slot communicationsOfficer = new Slot("Communications Officer", Stats.DIPLOMACY);

        Slot chiefSecurityOfficer = new Slot("Chief Security", Stats.SECURITY);
        Slot tacticalOfficer = new Slot("Tactical Officer", Stats.SECURITY);

        Slot chiefEngineer = new Slot("Chief Engineer", Stats.ENGINEERING);
        Slot engineer = new Slot("Engineer", Stats.ENGINEERING);

        Slot chiefScienceOfficer = new Slot("Chief Science Officer", Stats.SCIENCE);
        Slot deputyScienceOfficer = new Slot("Deputy Science Officer", Stats.SCIENCE);

        Slot chiefMedicalOfficer = new Slot("Chief Medical Officer", Stats.MEDICINE);
        Slot shipCounselor = new Slot("Ship Counselor", Stats.MEDICINE);

        slots = Arrays
            .asList(firstOfficer, helmOfficer, diplomat, communicationsOfficer, chiefSecurityOfficer, tacticalOfficer,
                chiefEngineer, engineer, chiefScienceOfficer, deputyScienceOfficer, chiefMedicalOfficer, shipCounselor);
        this.bonusStats = bonusStats;
        this.antimatter = antimatter;
    }

    public int getCommand(){
        return getSkillScore(Stats.COMMAND);
    }

    private int getSkillScore(Stats stat){
        return slots.stream().filter(Slot::isNotEmpty).map(Slot::getCrew).map(crew -> crew.getSkill(stat))
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
        return slots.toString();
    }

    public Double calculateDuration(){

        //(increase for accuracy, decrease for speed!)
        int numSims = 5000;
        List<Skill> skills = Arrays.asList(getPrimary(), getSecondary(), getOthers().get(0),
            getOthers().get(1), getOthers().get(2), getOthers().get(3));
        List<Integer> results = new ArrayList<>(numSims);

        for(int iSim = 0; iSim < numSims; iSim++){
            results.add(doSimulation(skills));
        }

        return results.stream().mapToInt(f -> f).average().orElse(0);
    }

    private int doSimulation(List<Skill> skills){
        int secondsPerTick = 20;
        int hazardTick = 4;
        int rewardTick = 7;
        int hazardAsRewardTick = 28;
        int amPerActivity = 1;
        int ticksBetweenDilemmas = 360;
        int hazSkillPerTick = 7;
        int hazAmPass = 5;
        int hazAmFail = 30;
        int tick = 0;
        int am = getAntimatter();

        while(tick < 10000 && am > 0){
            ++tick;

            // hazard && not dilemma
            if(tick % hazardTick == 0 && tick % hazardAsRewardTick != 0 && tick % ticksBetweenDilemmas != 0){
                Skill skill = pickSkill(skills);

                // check (roll if necessary)
                int hazDiff = tick * hazSkillPerTick;
                float skillMin = skill.getBase() + skill.getMin();
                if(hazDiff < skillMin){ // automatic success
                    am += hazAmPass;
                }else{
                    float skillMax = skill.getBase() + skill.getMax();
                    if(hazDiff >= skillMax){ // automatic fail
                        am -= hazAmFail;
                    }else{ // roll for it
                        double skillRoll = randomRange(skillMin, skillMax);
                        if(skillRoll >= hazDiff){
                            am += hazAmPass;
                        }else{
                            am -= hazAmFail;
                        }
                    }
                }
            }else if(tick % rewardTick != 0 && tick % hazardAsRewardTick != 0 && tick % ticksBetweenDilemmas != 0){
                am -= amPerActivity;
            }
        }

        return tick * secondsPerTick;
    }

    private Skill pickSkill(List<Skill> skills){
        float psChance = 0.35f;
        float ssChance = 0.25f;
        double skillPickRoll = Math.random();

        int index;
        if(skillPickRoll < psChance){
            index = 0;
        }else if(skillPickRoll < psChance + ssChance){
            index = 1;
        }else{
            index = 2 + ThreadLocalRandom.current().nextInt(4);
        }

        return skills.get(index);
    }

    private double randomRange(float min, float max){
        return min + Math.random() * (max - min);
    }

    private Skill getPrimary(){
        return getTotalSkillScores(bonusStats.getPrimary());
    }

    private Skill getTotalSkillScores(Stats stat){
        return slots.stream().filter(Slot::isNotEmpty).map(Slot::getCrew).map(crew -> crew.getSkill(stat))
            .reduce(Skill::sum).orElse(null);
    }

    private Skill getSecondary(){
        return getTotalSkillScores(bonusStats.getSecondary());
    }

    private List<Skill> getOthers(){
        return Arrays.stream(Stats.values())
            .filter(stat -> stat != bonusStats.getPrimary() && stat != bonusStats.getSecondary())
            .map(this::getTotalSkillScores).collect(Collectors.toList());
    }
}
