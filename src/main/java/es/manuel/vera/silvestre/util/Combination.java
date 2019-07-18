package es.manuel.vera.silvestre.util;

import es.manuel.vera.silvestre.modelo.BonusStats;
import es.manuel.vera.silvestre.modelo.Stats;

import java.util.ArrayList;
import java.util.List;

public class Combination{
    public static List<BonusStats> getCombinations(){
        List<BonusStats> allPossibleCombinations = new ArrayList<>();

        Stats[] stats = Stats.values();
        for(int primary = 0; primary < 6; primary++){
            for(int secondary = 0; secondary < 6; secondary++){
                if(primary == secondary){
                    continue;
                }

                allPossibleCombinations.add(new BonusStats(stats[primary], stats[secondary]));
            }
        }
        return allPossibleCombinations;
    }
}
