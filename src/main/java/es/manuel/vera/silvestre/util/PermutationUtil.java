package es.manuel.vera.silvestre.util;

import com.google.common.collect.Collections2;

import java.util.ArrayList;
import java.util.List;

public class PermutationUtil{
    public static <T> List<List<T>> of(List<T> items){
        return new ArrayList(Collections2.permutations(items));
    }
}
