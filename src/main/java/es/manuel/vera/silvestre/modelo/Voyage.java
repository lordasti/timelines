package es.manuel.vera.silvestre.modelo;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class Voyage {
    private final Slot firstOfficer;
    private final Slot helmOfficer;
    private final Slot diplomat;
    private final Slot communicationsOfficer;
    private final Slot chiefSecurityOfficer;
    private final Slot tacticalOfficer;
    private final Slot chiefEngineer;
    private final Slot engineer;
    private final Slot chiefScienceOfficer;
    private final Slot deputyScienceOfficer;
    private final Slot chiefMedicalOfficer;
    private final Slot shipCounselor;
    private final BonusStats bonusStats;

    public Voyage(BonusStats bonusStats) {
        this.bonusStats = bonusStats;

        firstOfficer = new Slot("First Officer", Stats.COMMAND);
        helmOfficer = new Slot("Helm Officer", Stats.COMMAND);

        diplomat = new Slot("Diplomat", Stats.DIPLOMACY);
        communicationsOfficer = new Slot("Communications Officer", Stats.DIPLOMACY);

        chiefSecurityOfficer = new Slot("Chief Security", Stats.SECURITY);
        tacticalOfficer = new Slot("Tactical Officer", Stats.SECURITY);

        chiefEngineer = new Slot("Chief Engineer", Stats.ENGINEERING);
        engineer = new Slot("Engineer", Stats.ENGINEERING);

        chiefScienceOfficer = new Slot("Chief Science Officer", Stats.SCIENCE);
        deputyScienceOfficer = new Slot("Deputy Science Officer", Stats.SCIENCE);

        chiefMedicalOfficer = new Slot("Chief Medical Officer", Stats.MEDICINE);
        shipCounselor = new Slot("Ship Counselor", Stats.MEDICINE);
    }

    public List<Slot> getVoyageSlots() {
        return Arrays.asList(firstOfficer, helmOfficer,
                diplomat, communicationsOfficer,
                chiefSecurityOfficer, tacticalOfficer,
                chiefEngineer, engineer,
                chiefScienceOfficer, deputyScienceOfficer,
                chiefMedicalOfficer, shipCounselor);
    }

    public int getTotal() {
        return getVoyageSlots().stream().map(Slot::getCrew).mapToInt(Crew::getTotal).sum();
    }

    public int getCommand() {
        return getVoyageSlots().stream().map(Slot::getCrew).mapToInt(Crew::getCommand).sum();
    }

    public int getDiplomacy() {
        return getVoyageSlots().stream().map(Slot::getCrew).mapToInt(Crew::getDiplomacy).sum();
    }

    public int getSecurity() {
        return getVoyageSlots().stream().map(Slot::getCrew).mapToInt(Crew::getSecurity).sum();
    }

    public int getEngineering() {
        return getVoyageSlots().stream().map(Slot::getCrew).mapToInt(Crew::getEngineering).sum();
    }

    public int getScience() {
        return getVoyageSlots().stream().map(Slot::getCrew).mapToInt(Crew::getScience).sum();
    }

    public int getMedicine() {
        return getVoyageSlots().stream().map(Slot::getCrew).mapToInt(Crew::getMedicine).sum();
    }
}
