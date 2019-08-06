package es.manuel.vera.silvestre;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import es.manuel.vera.silvestre.modelo.*;
import es.manuel.vera.silvestre.util.GauntletUtil;
import es.manuel.vera.silvestre.util.VoyageUtil;
import es.manuel.vera.silvestre.util.google.SheetsServiceUtil;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class App{
    //GENERAL
    public static final int BEST_CREW_LIMIT = 100;

    //VOYAGE
    public static final BonusStats VOYAGE_BONUS_STATS = new BonusStats(Stats.MEDICINE, Stats.SCIENCE);
    public static final int VOYAGE_ANTIMATTER = 2500;
    public static final int VOYAGE_MODE = 1; //0 Random, 1. Deterministic
    public static final int VOYAGE_NUM_SIMS = 100;//Random only
    public static final List<String> VOYAGE_TRAITS = Arrays.asList(
        "federation",
        "villain",
        "romulan",
        "bajoran",
        "brutal",
        "explorer",
        "gambler",
        "tactician",
        "innovator",
        "human",
        "physician",
        "starfleet");
    //lowercase

    //GAUNTLET
    public static final Stats GAUNTLET_STAT = Stats.SCIENCE;
    public static final List<String> GAUNTLET_TRAITS = Arrays.asList("resourceful", "innovator", "vulcan");
    //lowercase
    // case

    //UTILITY
    private static final Logger LOGGER = Logger.getLogger(App.class);
    private static final String SPREADSHEET_ID = "1xTaefRpNV_gPHMympcVU-c2uAe8hVIYOjbt3MXBusjc";

    public static void main(String[] args) throws IOException, GeneralSecurityException{
        StopWatch watch = StopWatch.createStarted();

        List<List<Object>> rawCrew = getRoster();

        //List<Crew> allTimelinesCrew = awCrew.size()).mapToObj(id -> new Crew(id, rawCrew.get(id))).collect
        // (Collectors.toList());
        List<Crew> allMyCrew =
            IntStream.range(0, rawCrew.size()).mapToObj(id -> new Crew(id, rawCrew.get(id))).filter(Crew::hasStars)
                .collect(Collectors.toList());
        //List<Crew> allActiveCrew =
        //    IntStream.range(0, rawCrew.size()).mapToObj(id -> new Crew(id, rawCrew.get(id))).filter
        //        (Crew::isActive).collect(Collectors.toList());

        calculateAVoyage(allMyCrew);
        //calculateBestCrew(allMyCrew);

        //calculateAGauntlet(allMyCrew);
        //calculateBestGauntletCrew(allMyCrew);

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

    private static void calculateAGauntlet(List<Crew> gauntletCrew){
        Gauntlet gauntlet = GauntletUtil.calculateGauntlet(GAUNTLET_STAT, GAUNTLET_TRAITS, gauntletCrew);
        LOGGER.info(gauntlet);
    }

    private static void calculateBestCrew(List<Crew> voyageCrew){
        Map<String,Integer> bestCrew = VoyageUtil.calculateBestCrew(voyageCrew);
        LOGGER.info(bestCrew);
    }

    private static void calculateBestGauntletCrew(List<Crew> gauntletCrew){
        Map<String,Integer> bestCrew = GauntletUtil.calculateBestCrew(gauntletCrew);
        LOGGER.info(bestCrew);
    }
}
