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
import org.apache.log4j.Logger;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class App{
    public static final BonusStats BONUS_STATS = new BonusStats(Stats.SECURITY, Stats.MEDICINE);
    public static final int ANTIMATTER = 2650;
    public static final int NUM_SIMS = 100;
    public static final int BEST_CREW_LIMIT = 100;
    public static final int MODE = 1; //0 Random, 1. Deterministic
    private static final Logger LOGGER = Logger.getLogger(App.class);
    private static final String SPREADSHEET_ID = "1CEwYl9Xo-u6rz7xaXyivKjCqWmOOhqhlS_PsksoYP4A";

    public static void main(String[] args) throws IOException, GeneralSecurityException{
        StopWatch watch = StopWatch.createStarted();

        List<List<Object>> rawCrew = getRoster();

        //List<Crew> allTimelinesCrew = awCrew.size()).mapToObj(id -> new Crew(id, rawCrew.get(id))).collect
        // (Collectors.toList());
        List<Crew> allMyCrew =
            IntStream.range(0, rawCrew.size()).mapToObj(id -> new Crew(id, rawCrew.get(id))).filter(Crew::hasStars)
                .collect(Collectors.toList());
        //List<Crew> allActiveCrew = awCrew.size()).mapToObj(id -> new Crew(id, rawCrew.get(id))).filter
        // (Crew::isActive).collect(Collectors.toList ());

        calculateAVoyage(allMyCrew);
        //calculateBestCrew(allMyCrew);

        watch.stop();
        LOGGER.info("Total time: " + watch.getTime(TimeUnit.SECONDS) + " s");
    }

    private static List<List<Object>> getRoster() throws IOException, GeneralSecurityException{
        Sheets sheetsService = SheetsServiceUtil.getSheetsService();

        ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, "'Stats'").execute();

        List<List<Object>> rawCrew = response.getValues();

        //remove headers
        rawCrew.remove(0);
        rawCrew.remove(0);
        rawCrew.remove(0);
        rawCrew.remove(0);

        return rawCrew;
    }

    private static void calculateAVoyage(List<Crew> roster){
        Voyage voyage = VoyageUtil.calculateVoyage(roster);
        LOGGER.info(voyage);
        LOGGER.info("Command:" + voyage.getCommand());
        LOGGER.info("Diplomacy:" + voyage.getDiplomacy());
        LOGGER.info("Security:" + voyage.getSecurity());
        LOGGER.info("Engineering:" + voyage.getEngineering());
        LOGGER.info("Science:" + voyage.getScience());
        LOGGER.info("Medicine:" + voyage.getMedicine());
        LOGGER.info("Estimate:" + LocalTime.ofSecondOfDay(voyage.getVoyageEstimate().longValue()));
    }

    private static void calculateBestCrew(List<Crew> voyageCrew){
        Map<String,Integer> bestCrew = VoyageUtil.calculateBestCrew(voyageCrew);
        LOGGER.info(bestCrew);
    }
}
