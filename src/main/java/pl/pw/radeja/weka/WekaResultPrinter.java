package pl.pw.radeja.weka;

import weka.classifiers.Classifier;

import java.util.*;
import java.util.stream.Collectors;

public class WekaResultPrinter {

    public static void print(List<WekaResult> results) {
        Map<Integer, Map<String, WekaResult>> resultMap = getResultMap(results);
        List<Integer> thresholds = new ArrayList<>(resultMap.keySet());
        Collections.sort(thresholds);
        List<String> classifiers = getClassifiers(resultMap);
        System.out.println("WekaVectorResults");

        StringBuilder prcCorrect = getBuilder("PrcCorrect", classifiers);
        StringBuilder rocArea = getBuilder("ROC Area", classifiers);
        StringBuilder precision = getBuilder("Precision", classifiers);
        StringBuilder recall = getBuilder("Recall", classifiers);
        thresholds.forEach(th -> {
            prcCorrect.append(th.toString()).append('\t');
            rocArea.append(th.toString()).append('\t');
            precision.append(th.toString()).append('\t');
            recall.append(th.toString()).append('\t');
            classifiers.forEach(classifier -> {
                WekaResult result = resultMap.get(th).get(classifier);
                prcCorrect.append(result.getPtcCorrect()).append('\t');
                rocArea.append(result.getRocArea()).append('\t');
                precision.append(result.getPrecision()).append('\t');
                recall.append(result.getRecall()).append('\t');
            });
            prcCorrect.append('\n');
            rocArea.append('\n');
            precision.append('\n');
            recall.append('\n');
        });
        System.out.println(prcCorrect.toString());
        System.out.println(rocArea.toString());
        System.out.println(precision.toString());
        System.out.println(recall.toString());
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

    private static String getClassifierName(Classifier c) {
        return c.getClass().getName().substring(c.getClass().getName().lastIndexOf('.') + 1);
    }

    private static StringBuilder getBuilder(String paramName, List<String> classifiers) {
        StringBuilder builder = new StringBuilder(paramName).append("\n");
        builder.append("Threshold\t").append(String.join("\t", classifiers)).append("\n");
        return builder;
    }
}
