package es.manuel.vera.silvestre.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Permutation{
    public static <T> List<List<T>> of(List<T> items){
        return IntStream.range(0, factorial(items.size())).mapToObj(i -> permutation(i, items))
            .collect(Collectors.toList());
    }

    private static int factorial(int num){
        return IntStream.rangeClosed(2, num).reduce(1, (x, y) -> x * y);
    }

    private static <T> List<T> permutation(int count, LinkedList<T> input, List<T> output){
        if(input.isEmpty()){
            return output;
        }

        int factorial = factorial(input.size() - 1);
        output.add(input.remove(count / factorial));
        return permutation(count % factorial, input, output);
    }

    private static <T> List<T> permutation(int count, List<T> items){
        return permutation(count, new LinkedList<>(items), new ArrayList<>());
    }
}
