package es.manuel.vera.silvestre.util;

import es.manuel.vera.silvestre.modelo.*;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GauntletUtil{
    private static final Logger LOGGER = Logger.getLogger(VoyageUtil.class);

    public static Map<String,Integer> calculateBestCrew(List<? extends Crew> roster){
        Map<String,Integer> bestCrew = new LinkedHashMap<>();
        Map<Stats,Integer> gauntlets = new LinkedHashMap<>();
        List<Stats> allPossibleCombinations = Arrays.asList(Stats.values());

        allPossibleCombinations.forEach(stat -> {
            StopWatch watch = StopWatch.createStarted();

            Gauntlet gauntlet = calculateGauntlet(stat, roster, new ArrayList<>());

            gauntlets.put(stat, gauntlet.getScore());

            List<Crew> selectedCrew =
                gauntlet.getSlots().stream().map(Slot::getCrew).collect(Collectors.toList());
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

    public static Gauntlet calculateGauntlet(Stats primary, List<? extends Crew> roster, List<String> gauntletTraits){
        Map<Stats,List<Crew>> bestCandidates = getBestCandidates(primary, roster, gauntletTraits);
        List<BonusStats> pairs =
            CombinationUtil.getCombinations().stream().filter(bonus -> bonus.getPrimary() == primary).collect(
                Collectors.toList());
        List<List<BonusStats>> permutations = PermutationUtil.of(pairs);

        return permutations.stream().map(permutation -> {
            LOGGER.debug("PermutationUtil: " + permutation);
            Gauntlet bestSoFar = new Gauntlet(primary, gauntletTraits, new ArrayList<>());
            Set<Crew> selectedSheetCrew =
                bestSoFar.getSlots().stream().map(Slot::getCrew).collect(Collectors.toSet());
            permutation.forEach(bonusStats -> {
                List<Crew> bestSheetCrewForStat = bestCandidates.get(bonusStats.getSecondary());
                Crew candidate =
                    bestSheetCrewForStat.stream().filter(crew -> !selectedSheetCrew.contains(crew)).findFirst()
                        .orElseThrow(
                            NoSuchElementException::new);
                LOGGER.debug("Candidate found for slot [" + bonusStats.getSecondary() + "] : " + candidate);
                selectedSheetCrew.add(candidate);
                List<Slot> slots = bestSoFar.getSlots();
                slots.add(Slot.builder().index(bonusStats.getSecondary().getIndex()).stat(bonusStats.getSecondary())
                    .crew(candidate).build());
            });
            return bestSoFar;
        }).reduce((gauntlet1, gauntlet2) -> gauntlet2.getScore() > gauntlet1.getScore() ? gauntlet2 : gauntlet1)
            .orElseThrow(NoSuchElementException::new);
    }

    private static Map<Stats,List<Crew>> getBestCandidates(Stats primary, List<? extends Crew> roster,
        List<String> gauntletTraits){
        StopWatch watch = StopWatch.createStarted();

        Map<Stats,List<Crew>> bestCandidates = new HashMap<>();
        Arrays.stream(Stats.values()).forEach(stat -> {
            if(stat == primary){
                bestCandidates.put(stat, new ArrayList<>());
            }else{
                bestCandidates.put(stat, getBestCandidates(new BonusStats(primary, stat), roster, gauntletTraits));
            }
        });

        watch.stop();
        LOGGER.debug("Candidates init took " + watch.getTime(TimeUnit.MILLISECONDS) + " ms");

        return bestCandidates;
    }

    private static List<Crew> getBestCandidates(BonusStats bonusStats, List<? extends Crew> roster,
        List<String> gauntletTraits){
        return roster.stream().filter(crew -> crew.getSkill(bonusStats.getPrimary()).getBase() > 0)
            .filter(crew -> crew.getSkill(bonusStats.getSecondary()).getBase() > 0)
            .sorted((o1, o2) -> Integer.compare(o2.getGauntletPairScore(bonusStats, gauntletTraits),
                o1.getGauntletPairScore(bonusStats, gauntletTraits)))
            .collect(Collectors.toList());
    }
}
