package es.manuel.vera.silvestre.util;

import es.manuel.vera.silvestre.modelo.*;

import java.util.*;
import java.util.stream.Collectors;

public class VoyageUtil {
    public static Voyage calculateVoyage(BonusStats bonusStats, List<Crew> roster) {
        Voyage voyage = new Voyage(bonusStats);

        List<Slot> voyageSlots = voyage.getVoyageSlots();
        Set<Crew> selectedCrew = new HashSet<>();
        List<Crew> bestCrew = roster.stream().map(crew -> crew.applyVoyageBonuses(bonusStats)).sorted().collect(Collectors.toList());


        voyageSlots.forEach(slot -> {
            Crew best = bestCrew.stream().filter(crew -> crew.hasProficiency(slot.getStat()))
                    .filter(crew -> !selectedCrew.contains(crew)).findFirst().orElse(null);
            slot.setCrew(best);
            selectedCrew.add(best);
        });

        int bestTotal = 0;
        int newBest = voyage.getTotal();

        while (newBest > bestTotal) {
            bestTotal = newBest;
            System.out.println("New Best: " + bestTotal);

            voyageSlots.stream().sorted().forEach(slot -> {
                List<Crew> candidates = bestCrew.stream().filter(crew -> crew.hasProficiency(slot.getStat()))
                        .filter(crew -> crew.getTotal() > slot.getCrew().getTotal()).collect(Collectors.toList());
                findReplacement(voyageSlots, selectedCrew, bestCrew, slot, candidates);
            });

            newBest = voyage.getTotal();
        }

        voyageSlots.forEach(slot -> {
            Crew original = roster.stream().filter(crew -> crew.equals(slot.getCrew())).findFirst().orElse(null);
            slot.setCrew(original);
        });

        return voyage;
    }

    private static void findReplacement(List<Slot> voyageSlots, Set<Crew> selectedCrew, List<Crew> bestCrew, Slot selectedSlot, List<Crew> candidates) {
        for (Crew candidate : candidates) {
            if (selectedCrew.contains(candidate)) {
                Slot alreadySelected = voyageSlots.stream().filter(slot -> slot.getCrew().equals(candidate)).findAny().orElse(null);
                if (alreadySelected.getStat() != selectedSlot.getStat()) {
                    Crew replacement = bestCrew.stream().filter(crew -> crew.hasProficiency(alreadySelected.getStat())).filter(crew -> !selectedCrew.contains(crew)).findFirst().orElse(null);
                    if (replacement != null && replacement.getTotal() > selectedSlot.getCrew().getTotal()) {
                        selectedCrew.remove(selectedSlot.getCrew());
                        System.out.println("Out: " + selectedSlot.getCrew());
                        selectedSlot.setCrew(candidate);
                        alreadySelected.setCrew(replacement);
                        selectedCrew.add(replacement);
                        System.out.println("In: " + replacement);
                        return;
                    }
                }
            } else {
                if (candidate.getTotal() > selectedSlot.getCrew().getTotal()) {
                    selectedCrew.remove(selectedSlot.getCrew());
                    System.out.println("Out: " + selectedSlot.getCrew());
                    selectedSlot.setCrew(candidate);
                    selectedCrew.add(candidate);
                    System.out.println("In: " + candidate);
                    return;
                }
            }
        }

        System.out.println("Replacement not found for slot: " + selectedSlot);
    }

    public static Map<String, Integer> calculateBestCrew(List<Crew> roster) {
        Map<String, Integer> bestCrew = new LinkedHashMap<>();
        List<BonusStats> allPossibleCombinations = Arrays.asList(
                new BonusStats(Stats.COMMAND, Stats.DIPLOMACY),
                new BonusStats(Stats.COMMAND, Stats.ENGINEERING),
                new BonusStats(Stats.COMMAND, Stats.SECURITY),
                new BonusStats(Stats.COMMAND, Stats.SCIENCE),
                new BonusStats(Stats.COMMAND, Stats.MEDICINE),

                new BonusStats(Stats.DIPLOMACY, Stats.COMMAND),
                new BonusStats(Stats.DIPLOMACY, Stats.ENGINEERING),
                new BonusStats(Stats.DIPLOMACY, Stats.SECURITY),
                new BonusStats(Stats.DIPLOMACY, Stats.SCIENCE),
                new BonusStats(Stats.DIPLOMACY, Stats.MEDICINE),

                new BonusStats(Stats.ENGINEERING, Stats.COMMAND),
                new BonusStats(Stats.ENGINEERING, Stats.DIPLOMACY),
                new BonusStats(Stats.ENGINEERING, Stats.SECURITY),
                new BonusStats(Stats.ENGINEERING, Stats.SCIENCE),
                new BonusStats(Stats.ENGINEERING, Stats.MEDICINE),

                new BonusStats(Stats.SECURITY, Stats.COMMAND),
                new BonusStats(Stats.SECURITY, Stats.DIPLOMACY),
                new BonusStats(Stats.SECURITY, Stats.ENGINEERING),
                new BonusStats(Stats.SECURITY, Stats.SCIENCE),
                new BonusStats(Stats.SECURITY, Stats.MEDICINE),

                new BonusStats(Stats.SCIENCE, Stats.COMMAND),
                new BonusStats(Stats.SCIENCE, Stats.DIPLOMACY),
                new BonusStats(Stats.SCIENCE, Stats.ENGINEERING),
                new BonusStats(Stats.SCIENCE, Stats.SECURITY),
                new BonusStats(Stats.SCIENCE, Stats.MEDICINE),

                new BonusStats(Stats.MEDICINE, Stats.COMMAND),
                new BonusStats(Stats.MEDICINE, Stats.DIPLOMACY),
                new BonusStats(Stats.MEDICINE, Stats.ENGINEERING),
                new BonusStats(Stats.MEDICINE, Stats.SECURITY),
                new BonusStats(Stats.MEDICINE, Stats.SCIENCE)
        );

        allPossibleCombinations.forEach(bonusStats -> {
            Voyage voyage = calculateVoyage(bonusStats, roster);
            List<Crew> selectedCrew = voyage.getVoyageSlots().stream().map(Slot::getCrew).collect(Collectors.toList());
            selectedCrew.forEach(crew -> {
                if (bestCrew.containsKey(crew.getName())) {
                    bestCrew.put(crew.getName(), bestCrew.get(crew.getName()) + 1);
                } else {
                    bestCrew.put(crew.getName(), 1);
                }
            });
        });

        return bestCrew
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));
    }
}
