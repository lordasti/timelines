package es.manuel.vera.silvestre.util;

import es.manuel.vera.silvestre.App;
import es.manuel.vera.silvestre.modelo.*;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GauntletUtil{
    private static final Logger LOGGER = Logger.getLogger(VoyageUtil.class);

    public static Map<String,Integer> calculateBestCrew(List<Crew> roster){
        Map<String,Integer> bestCrew = new LinkedHashMap<>();
        Map<Stats,Integer> gauntlets = new LinkedHashMap<>();
        List<Stats> allPossibleCombinations = Arrays.asList(Stats.values());

        allPossibleCombinations.forEach(stat -> {
            StopWatch watch = StopWatch.createStarted();

            Gauntlet gauntlet = calculateGauntlet(stat, new ArrayList<>(), roster);

            gauntlets.put(stat, gauntlet.getScore());

            List<Crew> selectedCrew = gauntlet.getSlots().stream().map(Slot::getCrew).collect(Collectors.toList());
            selectedCrew.forEach(crew -> {
                if(bestCrew.containsKey(crew.getName())){
                    bestCrew.put(crew.getName(), bestCrew.get(crew.getName()) + 1);
                }else{
                    bestCrew.put(crew.getName(), 1);
                }
            });

            watch.stop();
            LOGGER.info(stat + " took " + watch.getTime(TimeUnit.MILLISECONDS) + " ms");
        });

        Map<Stats,Integer> gauntletRank = MapUtil.sortMapByValues(gauntlets,
            Collections.reverseOrder(Map.Entry.comparingByValue()));
        LOGGER.info(gauntletRank);

        return MapUtil.sortMapByValues(bestCrew,
            Collections.reverseOrder(Map.Entry.comparingByValue()));
    }

    public static Gauntlet calculateGauntlet(Stats primary, List<String> traits, List<Crew> roster){
        List<List<Crew>> bestCandidates = getBestCandidates(primary, roster);
        List<BonusStats> pairs =
            Combination.getCombinations().stream().filter(bonus -> bonus.getPrimary() == primary).collect(
                Collectors.toList());
        List<List<BonusStats>> permutations = Permutation.of(pairs);

        return permutations.stream().map(permutation -> {
            LOGGER.debug("Permutation: " + permutation);
            Gauntlet bestSoFar = new Gauntlet(primary, traits, new ArrayList<>());
            Set<Crew> selectedCrew = bestSoFar.getSlots().stream().map(Slot::getCrew).collect(Collectors.toSet());
            permutation.forEach(bonusStats -> {
                List<Crew> bestCrewForStat = bestCandidates.get(bonusStats.getSecondary().getIndex());
                Crew candidate =
                    bestCrewForStat.stream().filter(crew -> !selectedCrew.contains(crew)).findFirst().orElseThrow(
                        NoSuchElementException::new);
                LOGGER.debug("Candidate found for slot [" + bonusStats.getSecondary() + "] : " + candidate);
                selectedCrew.add(candidate);
                List<Slot> slots = bestSoFar.getSlots();
                slots.add(Slot.builder().index(bonusStats.getSecondary().getIndex()).stat(bonusStats.getSecondary())
                    .crew(candidate).build());
            });
            return bestSoFar;
        }).reduce((gauntlet1, gauntlet2) -> gauntlet2.getScore() > gauntlet1.getScore() ? gauntlet2 : gauntlet1)
            .orElseThrow(NoSuchElementException::new);
    }

    private static List<List<Crew>> getBestCandidates(Stats primary, List<Crew> roster){
        StopWatch watch = StopWatch.createStarted();

        List<List<Crew>> bestCandidates =
            Arrays.stream(Stats.values()).map(stat -> {
                if(stat == primary){
                    return new ArrayList<Crew>();
                }
                return getBestCandidates(new BonusStats(primary, stat), roster);
            }).collect(Collectors.toList());

        watch.stop();
        LOGGER.debug("Candidates init took " + watch.getTime(TimeUnit.MILLISECONDS) + " ms");

        return bestCandidates;
    }

    private static List<Crew> getBestCandidates(BonusStats bonusStats, List<Crew> roster){
        return roster.stream().filter(crew -> crew.getSkill(bonusStats.getPrimary()).getBase() > 0)
            .filter(crew -> crew.getSkill(bonusStats.getSecondary()).getBase() > 0)
            .sorted((o1, o2) -> Integer.compare(o2.getGauntletPairScore(bonusStats),
                o1.getGauntletPairScore(bonusStats)))
            .limit(App.BEST_CREW_LIMIT)
            .collect(Collectors.toList());
    }
}
