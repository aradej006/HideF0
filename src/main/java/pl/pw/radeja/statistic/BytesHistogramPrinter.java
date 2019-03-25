package pl.pw.radeja.statistic;

import pl.pw.radeja.speex.result.BitsCollector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BytesHistogramPrinter {

    public static void printHistograms(List<BitsCollector> bitsCollectors) {
        Map<Integer, List<BitsCollector>> collect = bitsCollectors.stream()
                .collect(Collectors.groupingBy(BitsCollector::getThreshold, Collectors.toList()));
        Map<Integer, Map<String, Map<Integer, Long>>> thresholdToSampleNameToValueToCount = collect.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        v -> v.getValue().stream().collect(Collectors.toMap(BitsCollector::getSampleName, BitsCollectorParser::getHistogramData))));

        thresholdToSampleNameToValueToCount.forEach((threshold, thToValToCnt) -> {
            List<String> sampleNames = new ArrayList<>(thToValToCnt.keySet());
            Collections.sort(sampleNames);
            List<Integer> values = new ArrayList<>(thToValToCnt.values().stream().findAny().orElseThrow(() -> new IllegalArgumentException("No data")).keySet());
            Collections.sort(values);

            System.out.println("\n\n\n");
            System.out.println("HistogramData for Th=" + threshold);
            System.out.println("Values\t" + sampleNames.stream().map(Object::toString).collect(Collectors.joining("\t")));
            values.forEach(v -> {
                List<String> vals = new ArrayList<>();
                vals.add(v.toString());
                sampleNames.forEach(th -> vals.add(thToValToCnt.get(th).get(v).toString()));
                System.out.println(String.join("\t", vals));
            });
            System.out.println("\n\n\n");
        });
    }

}
