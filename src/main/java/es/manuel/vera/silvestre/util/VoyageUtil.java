package es.manuel.vera.silvestre.util;

import es.manuel.vera.silvestre.App;
import es.manuel.vera.silvestre.modelo.*;
import org.apache.commons.lang3.time.StopWatch;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VoyageUtil{

    public static Map<String,Integer> calculateBestCrew(List<Crew> roster){
        Map<String,Integer> bestCrew = new LinkedHashMap<>();
        List<BonusStats> allPossibleCombinations = new ArrayList<>();

        Stats[] stats = Stats.values();
        for(int primary = 0; primary < 6; primary++){
            for(int secondary = 0; secondary < 6; secondary++){
                if(primary == secondary){
                    continue;
                }

                allPossibleCombinations.add(new BonusStats(stats[primary], stats[secondary]));
            }
        }

        allPossibleCombinations.forEach(bonusStats -> {
            Voyage voyage = calculateVoyage(bonusStats, roster);
            List<Crew> selectedCrew = voyage.getSlots().stream().map(Slot::getCrew).collect(Collectors.toList());
            selectedCrew.forEach(crew -> {
                if(bestCrew.containsKey(crew.getName())){
                    bestCrew.put(crew.getName(), bestCrew.get(crew.getName()) + 1);
                }else{
                    bestCrew.put(crew.getName(), 1);
                }
            });
        });

        return bestCrew
            .entrySet()
            .stream()
            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                LinkedHashMap::new));
    }

    public static Voyage calculateVoyage(BonusStats bonusStats, List<Crew> roster){
        //StopWatch watch = StopWatch.createStarted();

        List<List<Crew>> bestCandidates = getBestCandidates(roster);
        List<List<Stats>> permutations = getPermutations();
        Voyage result = calculateBestVoyage(bonusStats, bestCandidates, permutations);

        //watch.stop();
        //System.out.println("calculateVoyage took " + watch.getTime(TimeUnit.SECONDS));

        return result;
    }

    private static Voyage calculateBestVoyage(BonusStats bonusStats, List<List<Crew>> bestCandidates,
        List<List<Stats>> permutations){
        //StopWatch watch = StopWatch.createStarted();

        Voyage bestVoyage = permutations.parallelStream()
            .map(permutation -> doPermutation(bonusStats, bestCandidates, permutation))
            .reduce((voyage1, voyage2) -> voyage2.getVoyageEstimate() > voyage1.getVoyageEstimate() ? voyage2 :
                voyage1).orElseThrow(NoSuchElementException::new);

        //watch.stop();
        //System.out.println("calculateBestVoyage took " + watch.getTime(TimeUnit.SECONDS));

        return bestVoyage;
    }

    private static Voyage doPermutation(BonusStats bonusStats, List<List<Crew>> bestCandidates,
        List<Stats> permutation){
        StopWatch watch = StopWatch.createStarted();

        Voyage best = Voyage.builder().slots(Collections.emptyList()).voyageEstimate(0D).build();
        for(int i = 0; i < permutation.size(); i++){
            Stats stat = permutation.get(i);
            best = fillSlotForStat(stat, stat.getIndex() * 2, best, bonusStats, bestCandidates, permutation);
            best = fillSlotForStat(stat, (stat.getIndex() * 2) + 1, best, bonusStats, bestCandidates, permutation);
        }

        watch.stop();
        System.out.println("Permutation " + permutation + " took: " + watch.getTime(TimeUnit.MILLISECONDS) + " ms");
        return best;
    }

    private static Voyage fillSlotForStat(Stats stat, int index, Voyage bestSoFar, BonusStats bonusStats,
        List<List<Crew>> bestCandidates, List<Stats> permutation){
        //StopWatch watch = StopWatch.createStarted();

        Set<Crew> selectedCrew = bestSoFar.getSlots().stream().map(Slot::getCrew).collect(Collectors.toSet());
        List<Crew> candidates = getBestCandidates(stat, selectedCrew, bestCandidates);
        Slot.SlotBuilder slotBuilder = Slot.builder().stat(stat).index(index);

        Voyage bestResult =
            candidates.parallelStream().map(candidate -> slotBuilder.crew(candidate).build())
                .map(slot -> {
                    List<Slot> slotsSoFar = new ArrayList<>(bestSoFar.getSlots());
                    slotsSoFar.add(slot);
                    return slotsSoFar;
                })
                .map(slots -> Voyage.builder().slots(slots).voyageEstimate(
                    calculateVoyageEstimate(bonusStats, slots))
                    .build())
                .reduce(
                    (voyage1, voyage2) -> voyage2.getVoyageEstimate() > voyage1.getVoyageEstimate() ? voyage2 : voyage1)
                .orElseThrow(NoSuchElementException::new);

        //watch.stop();
        //System.out.println(
        //    "Fill Slot[" + index + "] from permutation " + permutation + " took: " +
        //        watch.getTime(TimeUnit.MILLISECONDS));

        return bestResult;
    }

    private static List<Crew> getBestCandidates(Stats stat, Set<Crew> selected, List<List<Crew>> bestCandidates){
        return removeSelected(bestCandidates.get(stat.getIndex()), selected);
    }

    private static List<Crew> removeSelected(List<Crew> candidates, Set<Crew> selected){
        return candidates.stream().filter(candidate -> !selected.contains(candidate)).collect(Collectors.toList());
    }

    private static Double calculateVoyageEstimate(BonusStats bonusStats, List<Slot> slots){
        Skill primary = getPrimary(bonusStats, slots);
        Skill secondary = getSecondary(bonusStats, slots);
        List<Skill> others = getOthers(bonusStats, slots);
        List<Skill> skills = Arrays.asList(primary, secondary, others.get(0), others.get(1), others.get(2),
            others.get(3));
        return doEstimation(skills);
    }

    private static double doEstimation(List<Skill> skills){
        if(App.MODE == 0){
            return doNumSim(skills, App.NUM_SIMS);
        }

        return doRanked(skills);
    }

    private static double doRanked(List<Skill> skills){
        int total = skills.stream().mapToInt(Skill::getAvgTotal).sum();

        return IntStream.range(0, skills.size()).mapToDouble(i -> {
            double ideal = i == 0 ? total * 0.35D : i == 1 ? total * 0.35D : total * 0.1D;
            double deviation = skills.get(i).getAvgTotal() / ideal;
            return deviation > 0.8D && deviation < 1.2D ? skills.get(i).getAvgTotal() : 0D;
        }).sum();
    }

    protected static double doNumSim(List<Skill> skills, Integer numSims){
        return IntStream.range(0, numSims).parallel().map(i -> doSimulation(skills)).average().getAsDouble();
    }

    private static int doSimulation(List<Skill> skills){
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
        int am = App.ANTIMATTER;

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

    private static Skill pickSkill(List<Skill> skills){
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

    private static double randomRange(float min, float max){
        return min + Math.random() * (max - min);
    }

    private static Skill getPrimary(BonusStats bonusStats, List<Slot> slots){
        return getTotalSkillScores(bonusStats.getPrimary(), slots);
    }

    private static Skill getTotalSkillScores(Stats stat, List<Slot> slots){
        return slots.stream().map(Slot::getCrew).map(crew -> crew.getSkill(stat))
            .reduce(Skill::sum).orElse(null);
    }

    private static Skill getSecondary(BonusStats bonusStats, List<Slot> slots){
        return getTotalSkillScores(bonusStats.getSecondary(), slots);
    }

    private static List<Skill> getOthers(BonusStats bonusStats, List<Slot> slots){
        return Arrays.stream(Stats.values())
            .filter(stat -> stat != bonusStats.getPrimary() && stat != bonusStats.getSecondary())
            .map(stat -> getTotalSkillScores(stat, slots)).collect(Collectors.toList());
    }

    private static List<List<Stats>> getPermutations(){
        StopWatch watch = StopWatch.createStarted();

        List<List<Stats>> permutations = Permutation.of(Arrays.asList(Stats.values()));

        watch.stop();
        System.out.println("Permutations init took " + watch.getTime(TimeUnit.MILLISECONDS));

        return permutations;
    }

    private static List<List<Crew>> getBestCandidates(List<Crew> roster){
        StopWatch watch = StopWatch.createStarted();

        List<List<Crew>> bestCandidates =
            Arrays.stream(Stats.values()).map(stat -> getBestCandidates(roster, stat))
                .collect(Collectors.toList());

        watch.stop();
        System.out.println("Candidates init took " + watch.getTime(TimeUnit.MILLISECONDS));

        return bestCandidates;
    }

    private static List<Crew> getBestCandidates(List<Crew> roster, Stats stat){
        return roster.stream()
            .sorted((o1, o2) -> Integer.compare(o2.getSkill(stat).getAvgTotal(), o1.getSkill(stat).getAvgTotal()))
            .limit(App.BEST_CREW_LIMIT)
            .collect(Collectors.toList());
    }

    public static Voyage calculateVoyage(List<Crew> roster){
        return calculateVoyage(App.BONUS_STATS, roster);
    }
}
