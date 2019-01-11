package pl.pw.radeja.weka;

import weka.classifiers.Classifier;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class WekaResultPrinter {

    public static void print(List<WekaResult> results) {
        Map<Integer, Map<String, WekaResult>> resultMap = getResultMap(results);
        List<Integer> thresholds = new ArrayList<>(resultMap.keySet());
        Collections.sort(thresholds);
        List<String> classifiers = getClassifiers(resultMap);
        System.out.println("\t\t\tWekaResults\t");
        System.out.println("Threshold\t" + getClassifierNames(resultMap));
        System.out.println("Threshold\t" + classifiers.stream().map(c -> "PrcCorrect\tROC Area").collect(Collectors.joining("\t")));
        thresholds.forEach(th -> {
            AtomicReference<String> line = new AtomicReference<>(th.toString() + '\t');
            classifiers.forEach(classifier -> {
                WekaResult result = resultMap.get(th).get(classifier);
                line.updateAndGet(v -> v + result.getPtcCorrect() + '\t' + result.getRocArea() + '\t');
            });
            System.out.println(line);
        });
    }

    private static Map<Integer, Map<String, WekaResult>> getResultMap(List<WekaResult> results) {
        Map<Integer, List<WekaResult>> thresholdToWekaResults = results.stream().collect(Collectors.groupingBy(WekaResult::getThreshold));
        Map<Integer, Map<String, WekaResult>> result = new HashMap<>();
        thresholdToWekaResults.forEach((th, list) -> list.forEach((wekaResult -> {
            result.putIfAbsent(th, new HashMap<>());
            result.get(th).put(getClassifierName(wekaResult.getClassifier()), wekaResult);
        })));
        return result;
    }

    private static List<String> getClassifiers(Map<Integer, Map<String, WekaResult>> resultMap) {
        ArrayList<String> classifiers = new ArrayList<>(resultMap.get(resultMap.keySet().iterator().next()).keySet());
        Collections.sort(classifiers);
        return classifiers;
    }

    private static String getClassifierNames(Map<Integer, Map<String, WekaResult>> resultMap) {
        return String.join("\t\t", resultMap
                .get(resultMap.keySet().iterator().next())
                .keySet());
    }

    private static String getClassifierName(Classifier c) {
        return c.getClass().getName().substring(c.getClass().getName().lastIndexOf('.') + 1);
    }
}
