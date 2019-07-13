package es.manuel.vera.silvestre;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import es.manuel.vera.silvestre.modelo.BonusStats;
import es.manuel.vera.silvestre.modelo.Crew;
import es.manuel.vera.silvestre.modelo.Stats;
import es.manuel.vera.silvestre.modelo.Voyage;
import es.manuel.vera.silvestre.util.VoyageUtil;
import es.manuel.vera.silvestre.util.google.SheetsServiceUtil;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class App {
    private static final String SPREADSHEET_ID = "1JaKMfwTENBJNb5XgnQ4T8A2zWk3azP5lcOkn-2i5F34";

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        Sheets sheetsService = SheetsServiceUtil.getSheetsService();

        ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, "'[Voyage] Stats'").execute();

        List<List<Object>> rawCrew = response.getValues();

        //remove headers
        rawCrew.remove(0);
        rawCrew.remove(0);

        List<Crew> roster = rawCrew.stream().map(Crew::new).filter(Crew::isValid).collect(Collectors.toList());

        calculateAVoyage(roster);
        //calculateBestCrew(roster);
    }

    private static void calculateAVoyage(List<Crew> roster) {
        Voyage voyage = VoyageUtil.calculateVoyage(new BonusStats(Stats.MEDICINE, Stats.COMMAND), roster);
        System.out.println(voyage);
        System.out.println("Command:" + voyage.getCommand());
        System.out.println("Diplomacy:" + voyage.getDiplomacy());
        System.out.println("Security:" + voyage.getSecurity());
        System.out.println("Engineering:" + voyage.getEngineering());
        System.out.println("Science:" + voyage.getScience());
        System.out.println("Medicine:" + voyage.getMedicine());
        System.out.println("Total:" + voyage.getTotal());
    }

    private static void calculateBestCrew(List<Crew> voyageCrew) {
        Map<String, Integer> bestCrew = VoyageUtil.calculateBestCrew(voyageCrew);
        System.out.println(bestCrew);
    }
}
