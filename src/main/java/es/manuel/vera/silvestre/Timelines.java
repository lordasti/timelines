package es.manuel.vera.silvestre;

import es.manuel.vera.silvestre.modelo.*;
import es.manuel.vera.silvestre.util.AppUtil;
import es.manuel.vera.silvestre.util.GauntletUtil;
import es.manuel.vera.silvestre.util.VoyageUtil;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Timelines{
    private static final String[] CREW_OPTIONS = {"My active crew", "My crew", "Timelines"};

    private List<? extends Crew> crewToConsider;

    private JPanel panel1;

    private JComboBox<String> crewComboBox;

    private JTabbedPane tabbedPane1;

    private JButton calculateGauntlet;
    private JButton gauntletBestCrewButton;

    private JButton calculateVoyage;
    private JButton voyageBestCrewButton;

    private JComboBox<Crew> candidatesComboBox;
    private JButton isUsefulButton;
    private JButton idleCrewButton;

    private JList<String> result;

    public Timelines(){
        crewComboBox.addActionListener(e -> {
            String valueSelected = crewComboBox.getItemAt(crewComboBox.getSelectedIndex());
            switch(valueSelected){
                case "My active crew":
                    crewToConsider = AppUtil.getActiveCrew();
                    break;
                case "My crew":
                default:
                    crewToConsider = AppUtil.getMyCrew();
                    break;
                case "Timelines":
                    crewToConsider = AppUtil.getTimelinesCrew();
                    break;
            }
        });

        calculateGauntlet.addActionListener(e -> {
            Gauntlet skeleton = AppUtil.getGauntlet();
            Gauntlet gauntlet =
                GauntletUtil
                    .calculateGauntlet(
                        skeleton.getPrimary(),
                        crewToConsider,
                        skeleton.getTraits()
                    );
            List<String> printData =
                gauntlet.getSlots().stream().sorted(Slot::compareTo).map(Slot::toString).collect(Collectors.toList());
            printData.add("Featured Skill: " + gauntlet.getPrimary());
            printData.add("Traits: " + gauntlet.getTraits());
            result.setListData(printData.toArray(new String[0]));
        });

        calculateVoyage.addActionListener(e -> {
            Voyage skeleton = AppUtil.getVoyage();
            Ship voyageShip = VoyageUtil.getBestShip(skeleton.getShipTrait());
            List<String> voyageTraits = skeleton.getTraits();
            Voyage voyage =
                VoyageUtil.calculateVoyage(skeleton.getBonusStats(),
                    crewToConsider,
                    voyageTraits,
                    voyageShip.getAntimatter());
            List<String> printData = new ArrayList<>();
            printData.add("Ship trait: " + skeleton.getShipTrait());
            printData.add("Primary Stat: " + skeleton.getBonusStats().getPrimary());
            printData.add("Secondary Stat: " + skeleton.getBonusStats().getSecondary());
            printData.add("Ship:" + voyageShip.getName());
            printData.addAll(
                voyage.getSlots().stream().sorted(Slot::compareTo).map(Slot::toString).collect(Collectors.toList()));
            printData.add("Antimatter:" + VoyageUtil.getAntimatter(voyage.getSlots(), voyageTraits,
                voyageShip.getAntimatter()));
            printData.add("Command:" + voyage.getCommand());
            printData.add("Diplomacy:" + voyage.getDiplomacy());
            printData.add("Security:" + voyage.getSecurity());
            printData.add("Engineering:" + voyage.getEngineering());
            printData.add("Science:" + voyage.getScience());
            printData.add("Medicine:" + voyage.getMedicine());
            printData.add("Estimate:" + LocalTime.ofSecondOfDay(voyage.getVoyageEstimate().longValue()));

            result.setListData(printData.toArray(new String[0]));
        });

        gauntletBestCrewButton.addActionListener(e -> {
            Map<String,Integer> bestCrew = GauntletUtil.calculateBestCrew(crewToConsider);
            result.setListData(
                bestCrew.entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")")
                    .toArray(String[]::new));
        });

        voyageBestCrewButton.addActionListener(e -> {
            Map<String,Integer> bestCrew = VoyageUtil.calculateBestCrew(crewToConsider);
            result.setListData(
                bestCrew.entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")")
                    .toArray(String[]::new));
        });

        idleCrewButton.addActionListener(e -> {
            Map<String,Integer> bestVoyageCrew = VoyageUtil.calculateBestCrew(crewToConsider);
            Map<String,Integer> bestGauntletCrew = GauntletUtil.calculateBestCrew(crewToConsider);

            result.setListData(
                crewToConsider.stream()
                    .filter(crew -> !crew.isFrozen() && crew.getLevel() == 100 && crew.getStars() == crew.getMaxStars())
                    .map(Crew::getName)
                    .filter(crew -> !bestVoyageCrew.containsKey(crew) && !bestGauntletCrew.containsKey(crew))
                    .toArray(String[]::new));
        });

        isUsefulButton.addActionListener(e -> {
            Crew candidate = getSelectedValue(candidatesComboBox);
            List<Crew> myCrew = AppUtil.getMyCrew();
            List<String> printData = new ArrayList<>();

            myCrew.add(candidate);

            Map<String,Integer> bestVoyageCrew = VoyageUtil.calculateBestCrew(myCrew);
            if(bestVoyageCrew.containsKey(candidate.getName())){
                printData
                    .add("Found useful for voyages. Number of voyages: " + bestVoyageCrew.get(candidate.getName()));
            }

            Map<String,Integer> bestGauntletCrew = GauntletUtil.calculateBestCrew(myCrew);
            if(bestGauntletCrew.containsKey(candidate.getName())){
                printData
                    .add(
                        "Found useful for gauntlet. Number of gauntlets: " + bestGauntletCrew.get(candidate.getName()));
            }

            if(printData.size() == 0){
                printData.add("Airlock!");
            }

            result.setListData(printData.toArray(new String[0]));
        });
    }

    private <E> E getSelectedValue(JComboBox<E> combo){
        return combo.getItemAt(combo.getSelectedIndex());
    }

    public static void main(String[] args){
        JFrame frame = new JFrame("Timelines");
        frame.setContentPane(new Timelines().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents(){
        crewToConsider = AppUtil.getMyCrew();

        crewComboBox = new JComboBox<>(CREW_OPTIONS);
        crewComboBox.setSelectedIndex(1);

        candidatesComboBox = new JComboBox<>(crewToConsider.toArray(new Crew[0]));
    }
}
