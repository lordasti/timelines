package es.manuel.vera.silvestre;

import es.manuel.vera.silvestre.modelo.*;
import es.manuel.vera.silvestre.util.AppUtil;
import es.manuel.vera.silvestre.util.GauntletUtil;
import es.manuel.vera.silvestre.util.VoyageUtil;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class App{
    private static final Logger LOGGER = Logger.getLogger(App.class);

    public static void main(String[] args) throws IOException, GeneralSecurityException{
        StopWatch watch = StopWatch.createStarted();

        //List<Crew> allTimelinesCrew = AppUtil.getTimelinesCrew();
        List<Crew> allMyCrew = AppUtil.getMyCrew();
        //List<Crew> allActiveCrew =AppUtil.getActiveCrew();

        calculateAVoyage(allMyCrew);
        //calculateBestCrew(allMyCrew);

        //calculateAGauntlet(allMyCrew);
        //calculateBestGauntletCrew(allMyCrew);

        watch.stop();
        LOGGER.info("Total time: " + watch.getTime(TimeUnit.SECONDS) + " s");
    }

    private static void calculateAVoyage(List<Crew> roster){
        Voyage voyage = VoyageUtil.calculateVoyage(new BonusStats(Stats.MEDICINE, Stats.SCIENCE), roster, Arrays.asList(
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
            "starfleet"), 2500);
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
        Gauntlet gauntlet = GauntletUtil
            .calculateGauntlet(Stats.SCIENCE, gauntletCrew, Arrays.asList("resourceful", "innovator", "vulcan"));
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
