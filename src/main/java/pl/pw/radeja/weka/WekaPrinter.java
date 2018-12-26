package pl.pw.radeja.weka;

import org.apache.commons.math3.complex.Complex;
import org.jtransforms.fft.DoubleFFT_1D;
import pl.pw.radeja.pitch.collectors.PitchCollector;
import pl.pw.radeja.pitch.collectors.PitchValue;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public final class WekaPrinter {
    private final static String filePath = "D:/PracaMgr/master-thesis/weka/hideF0-";
    private final static String extension = ".arff";
    private final static String hasHideF0 = "HideF0";
    private final static String hasNotHideF0 = "NoHideF0";
    private final static DoubleFFT_1D FFT = new DoubleFFT_1D(4);


    public static void print(List<PitchCollector> pitchCollectors, int numberOfFrames) {
        Map<Integer, List<PitchCollector>> thresholdToPitchCollector = pitchCollectors.stream().collect(Collectors.groupingBy(PitchCollector::getThreshold));
        thresholdToPitchCollector.forEach((threshold, pitchCollectorList) -> {
            try {
                PrintWriter pw = new PrintWriter(filePath + threshold + "-" + numberOfFrames + extension, "UTF-8");
                PrintWriter pwFft = new PrintWriter(filePath + threshold + "-fft-" + numberOfFrames + extension, "UTF-8");
                printHeader(pw, numberOfFrames);
                printFftHeader(pwFft, numberOfFrames);
                pitchCollectorList.forEach(pitchCollector ->
                        partition(pitchCollector.getPitchValues(), numberOfFrames).stream()
                                .filter(p -> p.size() == numberOfFrames)
                                .forEach(p -> {
                                    boolean hasHideF0 = p.stream().anyMatch(PitchValue::isChanged);
                                    List<Complex> fftPitchValues = new ArrayList<>();
                                    for (PitchValue pitchValue1 : p) {
                                        fftPitchValues.addAll(doFFT(pitchValue1.getPitchValues()));
                                    }
                                    //print normal
                                    pw.println(p.stream()
                                            .flatMap(pitchValue -> pitchValue.getPitchValues().stream())
                                            .map(Objects::toString)
                                            .collect(Collectors.joining(",")) + "," + (hasHideF0 ? WekaPrinter.hasHideF0 : WekaPrinter.hasNotHideF0));
                                    //print Fourier Transform values
                                    pwFft.println(fftPitchValues.stream()
                                            .map(Complex::abs)
                                            .map(Objects::toString)
                                            .collect(Collectors.joining(",")) + "," + (hasHideF0 ? WekaPrinter.hasHideF0 : WekaPrinter.hasNotHideF0));
                                })
                );
                pw.close();
                pwFft.close();
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
    }

    private static void printHeader(PrintWriter pr, int numberOfFrames) {
        pr.println("@RELATION 'HideF0'");
        for (int i = 0; i < (numberOfFrames * 4); i++) {
            pr.println("@ATTRIBUTE F" + (i / 4) + "_P" + (i % 4) + " INTEGER");
        }
        pr.println("@ATTRIBUTE class {" + hasHideF0 + "," + hasNotHideF0 + "}");
        pr.println("@DATA");
    }

    private static void printFftHeader(PrintWriter pr, int numberOfFrames) {
        pr.println("@RELATION 'HideF0'");
        for (int i = 0; i < (numberOfFrames * 4); i++) {
            pr.println("@ATTRIBUTE F" + (i / 4) + "_P_ABS_" + (i % 4) + " NUMERIC");
            pr.println("@ATTRIBUTE F" + (i / 4) + "_P_ARG_" + (i % 4) + " NUMERIC");
        }
        pr.println("@ATTRIBUTE class {" + hasHideF0 + "," + hasNotHideF0 + "}");
        pr.println("@DATA");
    }

    private static List<Complex> doFFT(List<Integer> p) {
        double[] fftData = new double[8];
        for (int i = 0; i < p.size(); i++) {
            fftData[i] = p.get(i);
        }
        FFT.realForwardFull(fftData);
        List<Complex> result = new ArrayList<>();
        for (int i = 0; i < fftData.length; i += 2) {
            result.add(new Complex(fftData[i], fftData[i + 1]));
        }
        return result;
    }

    private static <T> Collection<List<T>> partition(List<T> list, int size) {
        final AtomicInteger counter = new AtomicInteger(0);

        return list.stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / size))
                .values();
    }

}
