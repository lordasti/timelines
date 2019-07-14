package es.manuel.vera.silvestre.util;

import es.manuel.vera.silvestre.modelo.*;

import java.util.*;
import java.util.stream.Collectors;

import static es.manuel.vera.silvestre.modelo.Stats.*;

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
        Voyage bestVoyage = null;
        Double bestVoyageTime = 0D;
        List<Crew> commandCandidates = getBestCandidates(roster, COMMAND);
        List<Crew> diplomacyCandidates = getBestCandidates(roster, DIPLOMACY);
        List<Crew> engineeringCandidates = getBestCandidates(roster, ENGINEERING);
        List<Crew> securityCandidates = getBestCandidates(roster, SECURITY);
        List<Crew> scienceCandidates = getBestCandidates(roster, SCIENCE);
        List<Crew> medicineCandidates = getBestCandidates(roster, MEDICINE);

        for(int i = 0; i < 50; i++){
            //StopWatch watch = StopWatch.createStarted();
            Voyage actual = new Voyage(bonusStats, antimatter);
            List<Slot> voyageSlots = actual.getSlots();
            Set<Crew> selectedCrew = new HashSet<>();
            Collections.shuffle(voyageSlots);

            voyageSlots.forEach(slot -> {
                //StopWatch watch = StopWatch.createStarted();
                double bestTime = 0;

                List<Crew> candidates =
                    getBestCandidates(slot.getStat(), selectedCrew, commandCandidates, diplomacyCandidates,
                        engineeringCandidates, securityCandidates, scienceCandidates, medicineCandidates);

                for(Crew candidate: candidates){
                    Crew oldCrew = slot.getCrew();
                    slot.setCrew(candidate);
                    double newTry = actual.calculateDuration();
                    if(newTry > bestTime){
                        bestTime = newTry;
                        slot.setCrew(candidate);
                        selectedCrew.add(candidate);
                        if(oldCrew != null){
                            selectedCrew.remove(oldCrew);
                        }
                    }else{
                        slot.setCrew(oldCrew);
                    }
                }
                //watch.stop();
                //System.out.println("Slot " + slot + " took: " + watch.getTime(TimeUnit.SECONDS));
            });

            Double actualTime = actual.calculateDuration();
            if(actualTime > bestVoyageTime){
                /*System.out.println("Found BETTER estimation: old(" +
                    LocalTime.ofSecondOfDay(bestVoyageTime.longValue()) + ") < new(" +
                    LocalTime.ofSecondOfDay(actualTime.longValue()) + ")");*
                if(actual.equals(bestVoyage)){
                    System.out.println("BUT CREW WAS IDENTICAL");
                }else{
                    System.out.println("Best crew: " + bestVoyage);
                    System.out.println("Actual crew: " + actual);
                }*/
                bestVoyage = actual;
                bestVoyageTime = actualTime;
            }else{
                /*System.out.println("Found WORST estimation: new(" +
                    LocalTime.ofSecondOfDay(actualTime.longValue()) + ") < old(" +
                    LocalTime.ofSecondOfDay(bestVoyageTime.longValue()) + ")");
                if(actual.equals(bestVoyage)){
                    System.out.println("BUT CREW WAS IDENTICAL");
                }else{
                    System.out.println("Best crew: " + bestVoyage);
                    System.out.println("Actual crew: " + actual);
                }*/
            }

            //watch.stop();
            //System.out.println("Iteration " + i + " took: " + watch.getTime(TimeUnit.SECONDS));
        }

        return bestVoyage;
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
