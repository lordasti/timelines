package es.manuel.vera.silvestre.util;

import es.manuel.vera.silvestre.modelo.*;
import org.apache.commons.lang3.time.StopWatch;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static es.manuel.vera.silvestre.modelo.Stats.*;

public class VoyageUtil{

    public static Map<String,Integer> calculateBestCrew(List<Crew> roster){
        Map<String,Integer> bestCrew = new LinkedHashMap<>();
        List<BonusStats> allPossibleCombinations = Arrays.asList(
            new BonusStats(COMMAND, Stats.DIPLOMACY),
            new BonusStats(COMMAND, Stats.ENGINEERING),
            new BonusStats(COMMAND, Stats.SECURITY),
            new BonusStats(COMMAND, Stats.SCIENCE),
            new BonusStats(COMMAND, Stats.MEDICINE),

            new BonusStats(Stats.DIPLOMACY, COMMAND),
            new BonusStats(Stats.DIPLOMACY, Stats.ENGINEERING),
            new BonusStats(Stats.DIPLOMACY, Stats.SECURITY),
            new BonusStats(Stats.DIPLOMACY, Stats.SCIENCE),
            new BonusStats(Stats.DIPLOMACY, Stats.MEDICINE),

            new BonusStats(Stats.ENGINEERING, COMMAND),
            new BonusStats(Stats.ENGINEERING, Stats.DIPLOMACY),
            new BonusStats(Stats.ENGINEERING, Stats.SECURITY),
            new BonusStats(Stats.ENGINEERING, Stats.SCIENCE),
            new BonusStats(Stats.ENGINEERING, Stats.MEDICINE),

            new BonusStats(Stats.SECURITY, COMMAND),
            new BonusStats(Stats.SECURITY, Stats.DIPLOMACY),
            new BonusStats(Stats.SECURITY, Stats.ENGINEERING),
            new BonusStats(Stats.SECURITY, Stats.SCIENCE),
            new BonusStats(Stats.SECURITY, Stats.MEDICINE),

            new BonusStats(Stats.SCIENCE, COMMAND),
            new BonusStats(Stats.SCIENCE, Stats.DIPLOMACY),
            new BonusStats(Stats.SCIENCE, Stats.ENGINEERING),
            new BonusStats(Stats.SCIENCE, Stats.SECURITY),
            new BonusStats(Stats.SCIENCE, Stats.MEDICINE),

            new BonusStats(Stats.MEDICINE, COMMAND),
            new BonusStats(Stats.MEDICINE, Stats.DIPLOMACY),
            new BonusStats(Stats.MEDICINE, Stats.ENGINEERING),
            new BonusStats(Stats.MEDICINE, Stats.SECURITY),
            new BonusStats(Stats.MEDICINE, Stats.SCIENCE)
        );

        allPossibleCombinations.forEach(bonusStats -> {
            Voyage voyage = calculateVoyage(bonusStats, 2500, roster);
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

    public static Voyage calculateVoyage(BonusStats bonusStats, int antimatter, List<Crew> roster){
        Voyage voyage = new Voyage(bonusStats, antimatter);

        List<Slot> voyageSlots = voyage.getSlots();
        Set<Crew> selectedCrew = new HashSet<>();
        List<Crew> commandCandidates = getBestCandidates(roster, COMMAND);
        List<Crew> diplomacyCandidates = getBestCandidates(roster, DIPLOMACY);
        List<Crew> engineeringCandidates = getBestCandidates(roster, ENGINEERING);
        List<Crew> securityCandidates = getBestCandidates(roster, SECURITY);
        List<Crew> scienceCandidates = getBestCandidates(roster, SCIENCE);
        List<Crew> medicineCandidates = getBestCandidates(roster, MEDICINE);

        voyageSlots.forEach(slot -> {
            StopWatch watch = StopWatch.createStarted();
            double best = 0;

            List<Crew> candidates =
                getBestCandidates(slot.getStat(), selectedCrew, commandCandidates, diplomacyCandidates,
                    engineeringCandidates, securityCandidates, scienceCandidates, medicineCandidates);

            for(Crew candidate: candidates){
                Crew oldCrew = slot.getCrew();
                slot.setCrew(candidate);
                double newTry = voyage.calculateDuration();
                if(newTry > best){
                    best = newTry;
                    slot.setCrew(candidate);
                    selectedCrew.add(candidate);
                    if(oldCrew != null){
                        selectedCrew.remove(oldCrew);
                    }
                }else{
                    slot.setCrew(oldCrew);
                }
            }
            watch.stop();
            System.out.println("Time for slot " + slot + " took: " + watch.getTime(TimeUnit.SECONDS));
        });

        return voyage;
    }

    private static List<Crew> getBestCandidates(Stats stat, Set<Crew> selected, List<Crew> commandCandidates,
        List<Crew> diplomacyCandidates, List<Crew> engineeringCandidates, List<Crew> securityCandidates,
        List<Crew> scienceCandidates, List<Crew> medicineCandidates){
        switch(stat){
            case COMMAND:
                return removeSelected(commandCandidates, selected);
            case DIPLOMACY:
                return removeSelected(diplomacyCandidates, selected);
            case ENGINEERING:
                return removeSelected(engineeringCandidates, selected);
            case SECURITY:
                return removeSelected(securityCandidates, selected);
            case SCIENCE:
                return removeSelected(scienceCandidates, selected);
            case MEDICINE:
                return removeSelected(medicineCandidates, selected);
        }

        return Collections.emptyList();
    }

    private static List<Crew> removeSelected(List<Crew> candidates, Set<Crew> selected){
        return candidates.stream().filter(candidate -> !selected.contains(candidate)).collect(Collectors.toList());
    }

    private static List<Crew> getBestCandidates(List<Crew> roster, Stats stat){
        return roster.stream()
            .sorted((o1, o2) -> Integer.compare(o2.getSkill(stat).getAvgTotal(), o1.getSkill(stat).getAvgTotal()))
            .limit(15)
            .collect(Collectors.toList());
    }
}
