package es.manuel.vera.silvestre;

import es.manuel.vera.silvestre.modelo.*;
import es.manuel.vera.silvestre.util.AppUtil;
import es.manuel.vera.silvestre.util.GauntletUtil;
import es.manuel.vera.silvestre.util.VoyageUtil;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Timelines{
    private static final String[] CREW_OPTIONS = {"My active crew", "My crew", "Timelines"};

    private List<Crew> crewToConsider;

    private JPanel panel1;

    private JComboBox<String> crewComboBox;

    private JTabbedPane tabbedPane1;

    private JComboBox<Stats> gauntletPrimaryStatComboBox;
    private JComboBox<String> gauntletTrait1ComboBox;
    private JComboBox<String> gauntletTrait2ComboBox;
    private JComboBox<String> gauntletTrait3ComboBox;
    private JButton calculateGauntlet;

    private JSpinner voyageAntimatterSpinner;
    private JComboBox<Stats> voyagePrimaryStatComboBox;
    private JComboBox<Stats> voyageSecondaryStatComboBox;
    private JComboBox<String> voyageCMD1ComboBox;
    private JComboBox<String> voyageDIP1ComboBox;
    private JComboBox<String> voyageSEC1ComboBox;
    private JComboBox<String> voyageENG1ComboBox;
    private JComboBox<String> voyageSCI1ComboBox;
    private JComboBox<String> voyageMED1ComboBox;
    private JComboBox<String> voyageCMD2ComboBox;
    private JComboBox<String> voyageDIP2ComboBox;
    private JComboBox<String> voyageSEC2ComboBox;
    private JComboBox<String> voyageENG2ComboBox;
    private JComboBox<String> voyageSCI2ComboBox;
    private JComboBox<String> voyageMED2ComboBox;

    private JButton calculateVoyage;

    private JList<String> result;
    private JButton gauntletBestCrewButton;
    private JButton voyageBestCrewButton;
    private JComboBox<Crew> candidatesComboBox;
    private JButton isUsefulButton;
    private JButton idleCrewButton;

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
            Gauntlet gauntlet =
                GauntletUtil
                    .calculateGauntlet(
                        getSelectedValue(gauntletPrimaryStatComboBox),
                        crewToConsider,
                        Arrays
                            .asList(getSelectedValue(gauntletTrait1ComboBox),
                                getSelectedValue(gauntletTrait2ComboBox),
                                getSelectedValue(gauntletTrait3ComboBox))
                    );
            result.setListData(
                gauntlet.getSlots().stream().sorted(Slot::compareTo).map(Slot::toString).toArray(String[]::new));
        });

        calculateVoyage.addActionListener(e -> {
            Voyage voyage =
                VoyageUtil.calculateVoyage(new BonusStats(getSelectedValue(voyagePrimaryStatComboBox),
                        getSelectedValue(voyageSecondaryStatComboBox)),
                    crewToConsider,
                    Arrays.asList(getSelectedValue(voyageCMD1ComboBox),
                        getSelectedValue(voyageCMD2ComboBox),
                        getSelectedValue(voyageDIP1ComboBox),
                        getSelectedValue(voyageDIP2ComboBox),
                        getSelectedValue(voyageSEC1ComboBox),
                        getSelectedValue(voyageSEC2ComboBox),
                        getSelectedValue(voyageENG1ComboBox),
                        getSelectedValue(voyageENG2ComboBox),
                        getSelectedValue(voyageSCI1ComboBox),
                        getSelectedValue(voyageSCI2ComboBox),
                        getSelectedValue(voyageMED1ComboBox),
                        getSelectedValue(voyageMED2ComboBox)
                    ), (Integer) voyageAntimatterSpinner.getValue());
            List<String> printData =
                voyage.getSlots().stream().sorted(Slot::compareTo).map(Slot::toString).collect(Collectors.toList());
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

            result.setListData(crewToConsider.stream().map(Crew::getName)
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
        String[] traits = AppUtil.getTraits().toArray(new String[0]);

        crewComboBox = new JComboBox<>(CREW_OPTIONS);
        crewComboBox.setSelectedIndex(1);

        gauntletPrimaryStatComboBox = new JComboBox<>(Stats.values());
        gauntletTrait1ComboBox = new JComboBox<>(traits);
        gauntletTrait2ComboBox = new JComboBox<>(traits);
        gauntletTrait3ComboBox = new JComboBox<>(traits);

        voyageAntimatterSpinner = new JSpinner();
        voyageAntimatterSpinner.setValue(2500);
        voyagePrimaryStatComboBox = new JComboBox<>(Stats.values());
        voyageSecondaryStatComboBox = new JComboBox<>(Stats.values());
        voyageCMD1ComboBox = new JComboBox<>(traits);
        voyageDIP1ComboBox = new JComboBox<>(traits);
        voyageSEC1ComboBox = new JComboBox<>(traits);
        voyageENG1ComboBox = new JComboBox<>(traits);
        voyageSCI1ComboBox = new JComboBox<>(traits);
        voyageMED1ComboBox = new JComboBox<>(traits);
        voyageCMD2ComboBox = new JComboBox<>(traits);
        voyageDIP2ComboBox = new JComboBox<>(traits);
        voyageSEC2ComboBox = new JComboBox<>(traits);
        voyageENG2ComboBox = new JComboBox<>(traits);
        voyageSCI2ComboBox = new JComboBox<>(traits);
        voyageMED2ComboBox = new JComboBox<>(traits);

        candidatesComboBox = new JComboBox<>(crewToConsider.toArray(new Crew[0]));
    }
}
