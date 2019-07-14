package es.manuel.vera.silvestre;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import es.manuel.vera.silvestre.modelo.BonusStats;
import es.manuel.vera.silvestre.modelo.Crew;
import es.manuel.vera.silvestre.modelo.Stats;
import es.manuel.vera.silvestre.modelo.Voyage;
import es.manuel.vera.silvestre.util.VoyageUtil;
import es.manuel.vera.silvestre.util.google.SheetsServiceUtil;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class App{
    private static final String SPREADSHEET_ID = "1CEwYl9Xo-u6rz7xaXyivKjCqWmOOhqhlS_PsksoYP4A";

    public static void main(String[] args) throws IOException, GeneralSecurityException{
        StopWatch watch = StopWatch.createStarted();
        Sheets sheetsService = SheetsServiceUtil.getSheetsService();

        ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, "'Stats'").execute();

        List<List<Object>> rawCrew = response.getValues();

        //remove headers
        rawCrew.remove(0);
        rawCrew.remove(0);
        rawCrew.remove(0);
        rawCrew.remove(0);

        //List<Crew> allTimelinesCrew = rawCrew.stream().map(Crew::new).collect(Collectors.toList());
        //List<Crew> allMyCrew = rawCrew.stream().map(Crew::new).filter(Crew::hasStars).collect(Collectors.toList());
        List<Crew> allActiveCrew = rawCrew.stream().map(Crew::new).filter(Crew::isActive).collect(Collectors.toList());

        calculateAVoyage(allActiveCrew);
        //calculateBestCrew(allMyCrew);
        
        watch.stop();
        System.out.println("Total time: " + watch.getTime(TimeUnit.SECONDS));
    }

    private static void calculateAVoyage(List<Crew> roster){
        Voyage voyage = VoyageUtil.calculateVoyage(new BonusStats(Stats.SECURITY, Stats.COMMAND), 2550, roster);
        System.out.println(voyage);
        System.out.println("Command:" + voyage.getCommand());
        System.out.println("Diplomacy:" + voyage.getDiplomacy());
        System.out.println("Security:" + voyage.getSecurity());
        System.out.println("Engineering:" + voyage.getEngineering());
        System.out.println("Science:" + voyage.getScience());
        System.out.println("Medicine:" + voyage.getMedicine());
        System.out.println("Estimate:" + LocalTime.ofSecondOfDay(voyage.calculateDuration().longValue()));
    }

    private static void calculateBestCrew(List<Crew> voyageCrew){
        Map<String,Integer> bestCrew = VoyageUtil.calculateBestCrew(voyageCrew);
        System.out.println(bestCrew);
    }
}
