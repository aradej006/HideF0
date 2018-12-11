package pl.pw.radeja.weka;

import pl.pw.radeja.pitch.collectors.PitchCollector;
import pl.pw.radeja.pitch.collectors.PitchValue;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class WekaPrinter {
    private final static String filePath = "D:/PracaMgr/master-thesis/hideF0.arff";

    public static void print(List<PitchCollector> pitchCollectors) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter pr = new PrintWriter(filePath, "UTF-8");
        printHeader(pr);
        pr.println("@DATA");
        pitchCollectors.forEach(pitchCollector -> pitchCollector.getPitchValues().forEach(pitchValue -> pr.println(getOneDataRow(pitchValue))));
        pr.close();
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
