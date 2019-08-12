package es.manuel.vera.silvestre.util;

import es.manuel.vera.silvestre.modelo.*;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class VoyageUtil{

    private static final Logger LOGGER = Logger.getLogger(VoyageUtil.class);

    public static Ship getBestShip(String shipTrait){
        return AppUtil.getShips().stream()
            .map(ship -> ship.toBuilder().antimatter(ship.getTraits().contains(shipTrait) ?
                ship.getAntimatter() + 150 : ship.getAntimatter()).build())
            .reduce((ship1, ship2) -> ship2.getAntimatter() > ship1.getAntimatter() ? ship2 : ship1)
            .orElseThrow(NoSuchElementException::new);
    }

    public static Map<String,Integer> calculateBestCrew(List<? extends Crew> roster){
        Map<String,Integer> bestCrew = new LinkedHashMap<>();
        Map<BonusStats,LocalTime> voyages = new LinkedHashMap<>();
        List<BonusStats> allPossibleCombinations = CombinationUtil.getCombinations();

        allPossibleCombinations.forEach(bonusStats -> {
            StopWatch watch = StopWatch.createStarted();

            Voyage voyage = calculateVoyage(bonusStats, roster, new ArrayList<>(), 2500);

            voyages.put(bonusStats, LocalTime.ofSecondOfDay(voyage.getVoyageEstimate()));

            List<Crew> selectedCrew =
                voyage.getSlots().stream().map(Slot::getCrew).collect(Collectors.toList());
            selectedCrew.forEach(crew -> {
                if(bestCrew.containsKey(crew.getName())){
                    bestCrew.put(crew.getName(), bestCrew.get(crew.getName()) + 1);
                }else{
                    bestCrew.put(crew.getName(), 1);
                }
            });

            watch.stop();
            LOGGER.info(bonusStats + " took " + watch.getTime(TimeUnit.MILLISECONDS) + " ms");
        });

        Map<BonusStats,LocalTime> voyageRank = MapUtil.sortMapByValues(voyages,
            Collections.reverseOrder(Map.Entry.comparingByValue()));
        LOGGER.info(voyageRank);

        return MapUtil.sortMapByValues(bestCrew,
            Collections.reverseOrder(Map.Entry.comparingByValue()));
    }

    public static Voyage calculateVoyage(BonusStats bonusStats, List<? extends Crew> roster, List<String> voyageTraits,
        int voyageAntimatter){
        StopWatch watch = StopWatch.createStarted();

        List<List<Crew>> bestCandidates = getBestCandidates(roster);
        List<List<Stats>> permutations = getPermutations();
        Voyage result = calculateBestVoyage(bonusStats, bestCandidates, permutations, voyageTraits, voyageAntimatter);

        watch.stop();
        LOGGER.debug("calculateVoyage took " + watch.getTime(TimeUnit.SECONDS) + " s");

        return result;
    }

    private static Voyage calculateBestVoyage(BonusStats bonusStats, List<List<Crew>> bestCandidates,
        List<List<Stats>> permutations, List<String> voyageTraits, int voyageAntimatter){
        StopWatch watch = StopWatch.createStarted();

        Voyage bestVoyage = permutations.parallelStream()
            .map(permutation -> doPermutation(bonusStats, bestCandidates, permutation, voyageTraits, voyageAntimatter))
            .reduce((voyage1, voyage2) -> voyage2.getVoyageEstimate() > voyage1.getVoyageEstimate() ? voyage2 :
                voyage1).orElseThrow(NoSuchElementException::new);

        watch.stop();
        LOGGER.debug("calculateBestVoyage took " + watch.getTime(TimeUnit.SECONDS) + " s");

        return bestVoyage;
    }

    private static Voyage doPermutation(BonusStats bonusStats, List<List<Crew>> bestCandidates,
        List<Stats> permutation, List<String> voyageTraits, int voyageAntimatter){
        StopWatch watch = StopWatch.createStarted();

        Voyage best = Voyage.builder().slots(Collections.emptyList()).voyageEstimate(0).build();
        for(int i = 0; i < permutation.size(); i++){
            Stats stat = permutation.get(i);
            best = fillSlotForStat(stat, stat.getIndex() * 2, best, bonusStats, bestCandidates, permutation,
                voyageTraits, voyageAntimatter);
            best = fillSlotForStat(stat, (stat.getIndex() * 2) + 1, best, bonusStats, bestCandidates, permutation,
                voyageTraits, voyageAntimatter);
        }

        watch.stop();
        LOGGER.debug("PermutationUtil " + permutation + " took: " + watch.getTime(TimeUnit.MILLISECONDS) + " ms");
        return best;
    }

    private static Voyage fillSlotForStat(Stats stat, int index, Voyage bestSoFar, BonusStats bonusStats,
        List<List<Crew>> bestCandidates, List<Stats> permutation, List<String> voyageTraits,
        int voyageAntimatter){
        StopWatch watch = StopWatch.createStarted();

        Set<Crew> selectedCrew =
            bestSoFar.getSlots().stream().map(Slot::getCrew).collect(Collectors.toSet());
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
                    calculateVoyageEstimate(bonusStats, slots, voyageTraits, voyageAntimatter))
                    .build())
                .reduce(
                    (voyage1, voyage2) -> voyage2.getVoyageEstimate() > voyage1.getVoyageEstimate() ? voyage2 : voyage1)
                .orElseThrow(NoSuchElementException::new);

        watch.stop();
        LOGGER.debug("Fill Slot[" + index + "] from permutation " + permutation + " took: " +
            watch.getTime(TimeUnit.MILLISECONDS) + " ms");

        return bestResult;
    }

    private static List<Crew> getBestCandidates(Stats stat, Set<Crew> selected,
        List<List<Crew>> bestCandidates){
        return removeSelected(bestCandidates.get(stat.getIndex()), selected);
    }

    private static List<Crew> removeSelected(List<Crew> candidates, Set<Crew> selected){
        return candidates.stream().filter(candidate -> !selected.contains(candidate)).collect(Collectors.toList());
    }

    private static int calculateVoyageEstimate(BonusStats bonusStats, List<Slot> slots, List<String> voyageTraits,
        int voyageAntimatter){
        Skill primary = getPrimary(bonusStats, slots);
        Skill secondary = getSecondary(bonusStats, slots);
        List<Skill> others = getOthers(bonusStats, slots);
        List<Skill> skills = Arrays.asList(primary, secondary, others.get(0), others.get(1), others.get(2),
            others.get(3));
        int antimatter = getAntimatter(slots, voyageTraits, voyageAntimatter);
        return doDeterministicSimulation(skills, antimatter);
    }

    public static int getAntimatter(List<Slot> slots, List<String> voyageTraits, int voyageAntimatter){
        if(voyageTraits.size() != 12){
            return voyageAntimatter;
        }

        //count matching traits
        int matchingTraits = (int) slots.stream().filter(slot ->
            slot.getCrew().getTraits().contains(voyageTraits.get(slot.getIndex()))
        ).count();

        return voyageAntimatter + matchingTraits * 25;
    }

    protected static int doDeterministicSimulation(List<Skill> skills, int antimatter){
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
        int am = antimatter;

        while(am > 0){
            ++tick;

            if(tick % hazardTick == 0 && tick % hazardAsRewardTick != 0 && tick % ticksBetweenDilemmas != 0){
                Skill skill = pickDeterministicSkill(skills, tick);

                int hazDiff = tick * hazSkillPerTick;
                if(hazDiff < skill.getAvgTotal()){
                    am += hazAmPass;
                }else{
                    am -= hazAmFail;
                }
            }else if(tick % rewardTick != 0 && tick % ticksBetweenDilemmas != 0){
                am -= amPerActivity;
            }
        }

        return tick * secondsPerTick;
    }

    private static Skill pickDeterministicSkill(List<Skill> skills, int tick){
        int skillPickRoll = tick % 100;
        int skillCycle = tick / 100;

        int index;

        if(skillCycle % 2 == 0){
            if(skillPickRoll < 35){
                index = 0;
            }else if(skillPickRoll < 60){
                index = 1;
            }else if(skillPickRoll < 70){
                index = 2;
            }else if(skillPickRoll < 80){
                index = 3;
            }else if(skillPickRoll < 90){
                index = 4;
            }else{
                index = 5;
            }
        }else{
            if(skillPickRoll < 10){
                index = 5;
            }else if(skillPickRoll < 20){
                index = 4;
            }else if(skillPickRoll < 30){
                index = 3;
            }else if(skillPickRoll < 40){
                index = 2;
            }else if(skillPickRoll < 65){
                index = 1;
            }else{
                index = 0;
            }
        }
        return skills.get(index);
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

        List<List<Stats>> permutations = PermutationUtil.of(Arrays.asList(Stats.values()));

        watch.stop();
        LOGGER.debug("Permutations init took " + watch.getTime(TimeUnit.MILLISECONDS) + " ms");

        return permutations;
    }

    private static List<List<Crew>> getBestCandidates(List<? extends Crew> roster){
        StopWatch watch = StopWatch.createStarted();

        List<List<Crew>> bestCandidates =
            Arrays.stream(Stats.values()).map(stat -> getBestCandidates(roster, stat))
                .collect(Collectors.toList());

        watch.stop();
        LOGGER.debug("Candidates init took " + watch.getTime(TimeUnit.MILLISECONDS) + " ms");

        return bestCandidates;
    }

    private static List<Crew> getBestCandidates(List<? extends Crew> roster, Stats stat){
        return roster.stream().filter(crew -> crew.getSkill(stat).getBase() > 0)
            .sorted((o1, o2) -> Integer.compare(o2.getSkill(stat).getAvgTotal(), o1.getSkill(stat).getAvgTotal()))
            .collect(Collectors.toList());
    }
}
