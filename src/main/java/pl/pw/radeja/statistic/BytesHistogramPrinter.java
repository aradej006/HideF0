package pl.pw.radeja.statistic;

import pl.pw.radeja.speex.result.BitsCollector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BytesHistogramPrinter {

    public static void printHistograms(List<BitsCollector> bitsCollectors) {
        Map<String, List<BitsCollector>> collect = bitsCollectors.stream()
                .collect(Collectors.groupingBy(BitsCollector::getSampleName, Collectors.toList()));
        Map<String, Map<Integer, Map<Integer, Long>>> sampleNameToThresholdToValueToCount = collect.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().stream().collect(Collectors.toMap(BitsCollector::getThreshold, BitsCollectorParser::getHistogramData))));
        sampleNameToThresholdToValueToCount.forEach((sampleName, thToValToCnt) -> {
            List<Integer> thresholds = new ArrayList<>(thToValToCnt.keySet());
            Collections.sort(thresholds);
            List<Integer> values = new ArrayList<>(thToValToCnt.values().stream().findAny().orElseThrow(() -> new IllegalArgumentException("No data")).keySet());
            Collections.sort(values);

            System.out.println("\n\n\n");
            System.out.println("HistogramData for " + sampleName);
            System.out.println("Values\t" + thresholds.stream().map(Object::toString).collect(Collectors.joining("\t")));
            values.forEach(v -> {
                List<String> vals = new ArrayList<>();
                vals.add(v.toString());
                thresholds.forEach(th -> vals.add(thToValToCnt.get(th).get(v).toString()));
                System.out.println(String.join("\t", vals));
            });
            System.out.println("\n\n\n");
        });


    }
}
