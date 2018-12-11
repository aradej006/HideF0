package pl.pw.radeja.weka;

import pl.pw.radeja.pitch.collectors.PitchCollector;
import pl.pw.radeja.pitch.collectors.PitchValue;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class WekaPrinter {
    private final static String filePath = "D:/PracaMgr/master-thesis/weka/hideF0-";
    private final static String extension = ".arff";


    public static void print(List<PitchCollector> pitchCollectors) {
        Map<Integer, List<PitchCollector>> thresholdToPitchCollector = pitchCollectors.stream().collect(Collectors.groupingBy(PitchCollector::getThreshold));
        thresholdToPitchCollector.forEach((threshold, pitchCollectors1) -> {
            try {
                PrintWriter pw = new PrintWriter(filePath + threshold + extension, "UTF-8");
                printHeader(pw);
                pw.println("@DATA");
                pitchCollectors1.forEach(pitchCollector -> pitchCollector.getPitchValues().forEach(v -> pw.println(getOneDataRow(v))));
                pw.close();
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
    }

    private static void printHeader(PrintWriter pr) {
        pr.println("@RELATION 'HideF0'");
        pr.println("@ATTRIBUTE finePith0 INTEGER");
        pr.println("@ATTRIBUTE finePith1 INTEGER");
        pr.println("@ATTRIBUTE finePith2 INTEGER");
        pr.println("@ATTRIBUTE finePith3 INTEGER");
        pr.println("@ATTRIBUTE hasHideF0 {true,false}");
    }

    private static String getOneDataRow(PitchValue p) {
        return p.getPitchValues().stream().map(Objects::toString).collect(Collectors.joining(",")) + "," + p.isChanged();
    }

}
