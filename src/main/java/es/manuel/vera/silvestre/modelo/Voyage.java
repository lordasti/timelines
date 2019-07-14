package es.manuel.vera.silvestre.modelo;

import lombok.Data;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

@Data
public class Voyage{
    private final Map<Stats,Supplier<Integer>> getters;
    private final Slot firstOfficer;
    private final Slot helmOfficer;
    private final Slot diplomat;
    private final Slot communicationsOfficer;
    private final Slot chiefSecurityOfficer;
    private final Slot tacticalOfficer;
    private final Slot chiefEngineer;
    private final Slot engineer;
    private final Slot chiefScienceOfficer;
    private final Slot deputyScienceOfficer;
    private final Slot chiefMedicalOfficer;
    private final Slot shipCounselor;
    private final BonusStats bonusStats;
    private final int antimatter;

    public Voyage(BonusStats bonusStats, int antimatter){
        this.bonusStats = bonusStats;

        firstOfficer = new Slot("First Officer", Stats.COMMAND);
        helmOfficer = new Slot("Helm Officer", Stats.COMMAND);

        diplomat = new Slot("Diplomat", Stats.DIPLOMACY);
        communicationsOfficer = new Slot("Communications Officer", Stats.DIPLOMACY);

        chiefSecurityOfficer = new Slot("Chief Security", Stats.SECURITY);
        tacticalOfficer = new Slot("Tactical Officer", Stats.SECURITY);

        chiefEngineer = new Slot("Chief Engineer", Stats.ENGINEERING);
        engineer = new Slot("Engineer", Stats.ENGINEERING);

        chiefScienceOfficer = new Slot("Chief Science Officer", Stats.SCIENCE);
        deputyScienceOfficer = new Slot("Deputy Science Officer", Stats.SCIENCE);

        chiefMedicalOfficer = new Slot("Chief Medical Officer", Stats.MEDICINE);
        shipCounselor = new Slot("Ship Counselor", Stats.MEDICINE);

        this.antimatter = antimatter;

        getters = new HashMap<>();
        getters.put(Stats.COMMAND, this::getCommand);
        getters.put(Stats.DIPLOMACY, this::getDiplomacy);
        getters.put(Stats.SECURITY, this::getSecurity);
        getters.put(Stats.ENGINEERING, this::getEngineering);
        getters.put(Stats.SCIENCE, this::getScience);
        getters.put(Stats.MEDICINE, this::getMedicine);
    }

    public int getCommand(){
        return getProficiencyScore(Crew::getCommand);
    }

    public int getDiplomacy(){
        return getProficiencyScore(Crew::getDiplomacy);
    }

    public int getSecurity(){
        return getProficiencyScore(Crew::getSecurity);
    }

    public int getEngineering(){
        return getProficiencyScore(Crew::getEngineering);
    }

    public int getScience(){
        return getProficiencyScore(Crew::getScience);
    }

    public int getMedicine(){
        return getProficiencyScore(Crew::getMedicine);
    }

    @Override
    public String toString(){
        return getVoyageSlots().toString();
    }

    public List<Slot> getVoyageSlots(){
        return Arrays.asList(firstOfficer, helmOfficer,
            diplomat, communicationsOfficer,
            chiefSecurityOfficer, tacticalOfficer,
            chiefEngineer, engineer,
            chiefScienceOfficer, deputyScienceOfficer,
            chiefMedicalOfficer, shipCounselor);
    }

    private int getProficiencyScore(ToIntFunction<Crew> mapper){
        return getVoyageSlots().stream().filter(Slot::isNotEmpty).map(Slot::getCrew).mapToInt(mapper).sum();
    }

    public Double calculateDuration(){
        // input check
        int ps = getPrimary();
        int ss = getSecondary();
        int o1 = getOthers().get(0);
        int o2 = getOthers().get(1);
        int o3 = getOthers().get(2);
        int o4 = getOthers().get(3);

        //int startAm = voyage.getAntimatter();

        // variables
        //int ticksPerCycle = 28;
        int secondsPerTick = 20;
        //int secondsInMinute = 60;
        //int minutesInHour = 60;
        int hazardTick = 4;
        int rewardTick = 7;
        int hazardAsRewardTick = 28;
        //int ticksPerMinute = 3; //secondsInMinute / secondsPerTick
        int ticksPerHour = 180; //ticksPerMinute * minutesInHour;
        //int cycleSeconds = 560; //ticksPerCycle * secondsPerTick
        //float cyclesPerHour = minutesInHour * secondsInMinute / cycleSeconds;
        //int hazPerCycle = 6;
        int amPerActivity = 1;
        //int activityPerCycle = 18;
        //int hoursBetweenDilemmas = 2;
        //float dilemmasPerHour = 0.5f;
        int ticksBetweenDilemmas = 360; //hoursBetweenDilemmas * minutesInHour * ticksPerMinute;
        //float hazPerHour = hazPerCycle * cyclesPerHour - dilemmasPerHour;
        //int hazSkillPerHour = 1260;
        int hazSkillPerTick = 7;// hazSkillPerHour / ticksPerHour // 7
        //float hazSkillintiance = 0.15f; // overwritten from input
        int hazAmPass = 5;
        int hazAmFail = 30;
        //float activityAmPerHour = activityPerCycle * cyclesPerHour * amPerActivity;
        //int minPerHour = 60;
        float psChance = 0.35f;
        float ssChance = 0.25f;
        //float osChance = 0.1f;
        //float[] skillChances = {psChance, ssChance, osChance, osChance, osChance, osChance};
        //int dilPerMin = 5;

        //(increase for accuracy, decrease for speed!)
        int numSims = 5000;

        //TODO adjust variance
        float hazSkillVariance = 0.2f;// ParseInt(document.getElementById("prof").value) / 100;
        List<Integer> skills = Arrays.asList(ps, ss, o1, o2, o3, o4);

        //int maxSkill = skills.stream().mapToInt(i -> i).max().orElse(0);
        //float endVoySkill = maxSkill * (1 + hazSkillVariance);
        List<Integer> results = new ArrayList<>(5000);

        for(int iSim = 0; iSim < numSims; iSim++){
            int tick = 0;
            int am = getAntimatter();

            while(tick < 10000 && am > 0){
                ++tick;

                // hazard && not dilemma
                if(tick % hazardTick == 0
                    && tick % hazardAsRewardTick != 0
                    && tick % ticksBetweenDilemmas != 0){
                    int hazDiff = tick * hazSkillPerTick;

                    // pick the skill
                    double skillPickRoll = Math.random();
                    int skill;
                    if(skillPickRoll < psChance){
                        skill = ps;
                    }else if(skillPickRoll < psChance + ssChance){
                        skill = ss;
                    }else{
                        int index = 2 + ThreadLocalRandom.current().nextInt(4);
                        skill = skills.get(index);
                    }

                    // check (roll if necessary)
                    float skillVar = hazSkillVariance * skill;
                    float skillMin = skill - skillVar;
                    if(hazDiff < skillMin){ // automatic success
                        am += hazAmPass;
                    }else{
                        float skillMax = skill + skillVar;
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
                }else if(tick % rewardTick != 0
                    && tick % hazardAsRewardTick != 0
                    && tick % ticksBetweenDilemmas != 0){
                    am -= amPerActivity;
                }

                if(am <= 0){
                    int result = tick * secondsPerTick;
                    results.add(result);
                }
            } // foreach tick
        } // foreach sim

        return results.stream().mapToInt(f -> f).average().orElse(0);
    }

    private int getPrimary(){
        return getters.get(bonusStats.getPrimary()).get();
    }

    private int getSecondary(){
        return getters.get(bonusStats.getSecondary()).get();
    }

    private List<Integer> getOthers(){
        List<Stats> others = Arrays.stream(Stats.values())
                                         .filter(stat -> stat != bonusStats.getPrimary() && stat != bonusStats
                                             .getSecondary())
                                         .collect(Collectors.toList());

        return others.stream().map(stat -> getters.get(stat).get()).collect(Collectors.toList());
    }

    private double randomRange(float min, float max){
        return min + Math.random() * (max - min);
    }
}
