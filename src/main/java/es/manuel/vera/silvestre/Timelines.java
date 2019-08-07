package es.manuel.vera.silvestre;

import es.manuel.vera.silvestre.modelo.*;
import es.manuel.vera.silvestre.util.AppUtil;
import es.manuel.vera.silvestre.util.GauntletUtil;
import es.manuel.vera.silvestre.util.VoyageUtil;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Timelines{
    private static final Logger LOGGER = Logger.getLogger(App.class);
    private static final String[] CREW_OPTIONS = {"My active crew", "My crew", "Timelines"};

    private List<Crew> crewToConsider;
    private List<String> traits;

    private JPanel panel1;

    private JComboBox crewComboBox;

    private JTabbedPane tabbedPane1;

    private JComboBox gauntletPrimaryStatComboBox;
    private JComboBox gauntletTrait1ComboBox;
    private JComboBox gauntletTrait2ComboBox;
    private JComboBox gauntletTrait3ComboBox;
    private JButton calculateGauntlet;

    private JSpinner voyageAntimatterSpinner;
    private JComboBox voyagePrimaryStatComboBox;
    private JComboBox voyageSecondaryStatComboBox;
    private JComboBox voyageCMD1ComboBox;
    private JComboBox voyageDIP1ComboBox;
    private JComboBox voyageSEC1ComboBox;
    private JComboBox voyageENG1ComboBox;
    private JComboBox voyageSCI1ComboBox;
    private JComboBox voyageMED1ComboBox;
    private JComboBox voyageCMD2ComboBox;
    private JComboBox voyageDIP2ComboBox;
    private JComboBox voyageSEC2ComboBox;
    private JComboBox voyageENG2ComboBox;
    private JComboBox voyageSCI2ComboBox;
    private JComboBox voyageMED2ComboBox;

    private JButton calculateVoyage;

    private JList result;
    private JButton gauntletBestCrewButton;
    private JButton voyageBestCrewButton;

    public Timelines(){
        calculateGauntlet.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                Gauntlet gauntlet =
                    GauntletUtil
                        .calculateGauntlet((Stats) gauntletPrimaryStatComboBox.getSelectedItem(),
                            crewToConsider,
                            Arrays.asList((String) gauntletTrait1ComboBox.getSelectedItem(),
                                (String) gauntletTrait2ComboBox.getSelectedItem(),
                                (String) gauntletTrait3ComboBox.getSelectedItem())
                        );
                gauntlet.getSlots().sort(Slot::compareTo);
                result.setListData(gauntlet.getSlots().toArray());
            }
        });

        calculateVoyage.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                Voyage voyage =
                    VoyageUtil.calculateVoyage(new BonusStats((Stats) voyagePrimaryStatComboBox.getSelectedItem(),
                            (Stats) voyageSecondaryStatComboBox.getSelectedItem()),
                        crewToConsider,
                        Arrays.asList((String) voyageCMD1ComboBox.getSelectedItem(),
                            (String) voyageCMD2ComboBox.getSelectedItem(),
                            (String) voyageDIP1ComboBox.getSelectedItem(),
                            (String) voyageDIP2ComboBox.getSelectedItem(),
                            (String) voyageSEC1ComboBox.getSelectedItem(),
                            (String) voyageSEC2ComboBox.getSelectedItem(),
                            (String) voyageENG1ComboBox.getSelectedItem(),
                            (String) voyageENG2ComboBox.getSelectedItem(),
                            (String) voyageSCI1ComboBox.getSelectedItem(),
                            (String) voyageSCI2ComboBox.getSelectedItem(),
                            (String) voyageMED1ComboBox.getSelectedItem(),
                            (String) voyageMED2ComboBox.getSelectedItem()
                        ), (Integer) voyageAntimatterSpinner.getValue());
                voyage.getSlots().sort(Slot::compareTo);
                List<String> printSlots = voyage.getSlots().stream().map(Slot::toString).collect(Collectors.toList());
                List<String> printData = new ArrayList<>(printSlots);
                printData.add("Command:" + voyage.getCommand());
                printData.add("Diplomacy:" + voyage.getDiplomacy());
                printData.add("Security:" + voyage.getSecurity());
                printData.add("Engineering:" + voyage.getEngineering());
                printData.add("Science:" + voyage.getScience());
                printData.add("Medicine:" + voyage.getMedicine());
                printData.add("Estimate:" + LocalTime.ofSecondOfDay(voyage.getVoyageEstimate().longValue()));

                result.setListData(printData.toArray());
            }
        });

        gauntletBestCrewButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                Map<String,Integer> bestCrew = GauntletUtil.calculateBestCrew(crewToConsider);
                List<String> printData =
                    bestCrew.entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")")
                        .collect(Collectors.toList());
                result.setListData(printData.toArray());
            }
        });
        voyageBestCrewButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                Map<String,Integer> bestCrew = VoyageUtil.calculateBestCrew(crewToConsider);
                List<String> printData =
                    bestCrew.entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")")
                        .collect(Collectors.toList());
                result.setListData(printData.toArray());
            }
        });
    }

    public static void main(String[] args){
        JFrame frame = new JFrame("Timelines");
        frame.setContentPane(new Timelines().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() throws IOException, GeneralSecurityException{
        crewToConsider = AppUtil.getMyCrew();
        traits = AppUtil.getTraits();

        crewComboBox = new JComboBox(CREW_OPTIONS);
        crewComboBox.setSelectedIndex(1);

        gauntletPrimaryStatComboBox = new JComboBox(Stats.values());
        gauntletTrait1ComboBox = new JComboBox(traits.toArray());
        gauntletTrait2ComboBox = new JComboBox(traits.toArray());
        gauntletTrait3ComboBox = new JComboBox(traits.toArray());

        voyageAntimatterSpinner = new JSpinner();
        voyageAntimatterSpinner.setValue(2500);
        voyagePrimaryStatComboBox = new JComboBox(Stats.values());
        voyageSecondaryStatComboBox = new JComboBox(Stats.values());
        voyageCMD1ComboBox = new JComboBox(traits.toArray());
        voyageDIP1ComboBox = new JComboBox(traits.toArray());
        voyageSEC1ComboBox = new JComboBox(traits.toArray());
        voyageENG1ComboBox = new JComboBox(traits.toArray());
        voyageSCI1ComboBox = new JComboBox(traits.toArray());
        voyageMED1ComboBox = new JComboBox(traits.toArray());
        voyageCMD2ComboBox = new JComboBox(traits.toArray());
        voyageDIP2ComboBox = new JComboBox(traits.toArray());
        voyageSEC2ComboBox = new JComboBox(traits.toArray());
        voyageENG2ComboBox = new JComboBox(traits.toArray());
        voyageSCI2ComboBox = new JComboBox(traits.toArray());
        voyageMED2ComboBox = new JComboBox(traits.toArray());
    }
}
