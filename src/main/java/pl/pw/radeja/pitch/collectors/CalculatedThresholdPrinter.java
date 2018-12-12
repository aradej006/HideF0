package pl.pw.radeja.pitch.collectors;

import java.util.*;
import java.util.stream.Collectors;

public class CalculatedThresholdPrinter {

    public static void print(List<PitchCollector> pitchCollectors) {
        Map<String, Map<Integer, Integer>> sampleNameToThresholdToPithcCollectors = getResultSet(pitchCollectors);
        Map<String, Map<Integer, Integer>> getResultSetAfterHideF0 = getResultSetAfterHideF0(pitchCollectors);
        ArrayList<String> sampleNames = new ArrayList<>(sampleNameToThresholdToPithcCollectors.keySet());
        Collections.sort(sampleNames);
        ArrayList<Integer> thresholds = new ArrayList<>(sampleNameToThresholdToPithcCollectors.get(sampleNames.get(0)).keySet());
        Collections.sort(thresholds);
        System.out.println("\n\n\n");
        System.out.println("Calculated Thresholds");
        System.out.println("Threshold\t" + String.join("\t", sampleNames));
        thresholds.forEach(t -> {
            List<String> values = new ArrayList<>();
            values.add(t.toString());
            sampleNames.forEach(s -> values.add(sampleNameToThresholdToPithcCollectors.get(s).get(t).toString()));
            System.out.println(String.join("\t", values));
        });
        System.out.println("\n\n\n");
        System.out.println("\n\n\n");
        System.out.println("Calculated Thresholds After HideF0");
        System.out.println("Threshold\t" + String.join("\t", sampleNames));
        thresholds.forEach(t -> {
            List<String> values = new ArrayList<>();
            values.add(t.toString());
            sampleNames.forEach(s -> values.add(getResultSetAfterHideF0.get(s).get(t).toString()));
            System.out.println(String.join("\t", values));
        });
        System.out.println("\n\n\n");
    }

    private static Map<String, Map<Integer, Integer>> getResultSet(List<PitchCollector> result) {
        List<Integer> thresholds = result.stream().map(PitchCollector::getThreshold).sorted().collect(Collectors.toList());
        Map<String, Map<Integer, Integer>> resultMap = new HashMap<>();
        result.forEach(r -> {
            resultMap.putIfAbsent(r.getSampleName(), new HashMap<>());
            resultMap.get(r.getSampleName()).putIfAbsent(r.getThreshold(), countThresholdFromLinear(r, thresholds));
        });
        return resultMap;
    }

    private static Map<String, Map<Integer, Integer>> getResultSetAfterHideF0(List<PitchCollector> result) {
        List<Integer> thresholds = result.stream().map(PitchCollector::getThreshold).sorted().collect(Collectors.toList());
        Map<String, Map<Integer, Integer>> resultMap = new HashMap<>();
        result.forEach(r -> {
            resultMap.putIfAbsent(r.getSampleName(), new HashMap<>());
            resultMap.get(r.getSampleName()).putIfAbsent(r.getThreshold(), countThresholdAfterHideF0FromLinear(r, thresholds));
        });
        return resultMap;
    }

    private static Integer countThresholdFromLinear(PitchCollector collector, List<Integer> thresholds) {
        Integer min = thresholds.indexOf(collector.getThreshold()) > 0 ? thresholds.get(thresholds.indexOf(collector.getThreshold()) - 1) : 0;
        return (int) collector.getPitchValues().stream().filter(p -> {
            if (collector.getThreshold() != 0) {
                return p.getCalculatedThreshold() <= collector.getThreshold() && p.getCalculatedThreshold() > min;
            } else {
                return p.getCalculatedThreshold() == 0;
            }
        }).count();
    }

    private static Integer countThresholdAfterHideF0FromLinear(PitchCollector collector, List<Integer> thresholds) {
        Integer min = thresholds.indexOf(collector.getThreshold()) > 0 ? thresholds.get(thresholds.indexOf(collector.getThreshold()) - 1) : 0;
        return (int) collector.getPitchValues().stream().filter(p -> {
            if (collector.getThreshold() != 0) {
                return p.getCalculatedThresholdAfterHideF0() <= collector.getThreshold() && p.getCalculatedThresholdAfterHideF0() > min;
            } else {
                return p.getCalculatedThresholdAfterHideF0() == 0;
            }
        }).count();
    }
}
