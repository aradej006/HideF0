package pl.pw.radeja.weka.printers;

import org.apache.commons.math3.complex.Complex;
import org.jtransforms.fft.DoubleFFT_1D;
import pl.pw.radeja.speex.pitch.collectors.PitchCollector;
import pl.pw.radeja.speex.pitch.collectors.PitchValue;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static pl.pw.radeja.Config.BASE_PATH;

public final class WekaPrinter {
    private final static String filePath = BASE_PATH.resolve("weka").toAbsolutePath().toString() + "/hideF0-";
    private final static String extension = ".arff";
    private final static String hasHideF0 = "HideF0";
    private final static String hasNotHideF0 = "NoHideF0";
    private final static DoubleFFT_1D FFT = new DoubleFFT_1D(4);
    private final static Long seed = 1L;
    private final static Random rand = new Random(seed);


    public static void print(List<PitchCollector> pitchCollectors, int numberOfFrames) {
        Map<Integer, List<PitchCollector>> thresholdToPitchCollector = pitchCollectors.stream().collect(Collectors.groupingBy(PitchCollector::getThreshold));
        printTrainTest(thresholdToPitchCollector, numberOfFrames, "train", isTraining, hasHideF0SaverTraining);
        printTrainTest(thresholdToPitchCollector, numberOfFrames, "test", isTest, hasHideF0SaverTest);
    }

    private static void printTrainTest(Map<Integer, List<PitchCollector>> thresholdToPitchCollector,
                                       int numberOfFrames,
                                       String name,
                                       Predicate<PitchCollector> filter,
                                       Predicate<List<PitchValue>> hasHideF0Saver) {
        thresholdToPitchCollector.forEach((threshold, pitchCollectorList) -> {
            try {
                PrintWriter pw = new PrintWriter(filePath + threshold + "-" + numberOfFrames + '-' + name + extension, "UTF-8");
                PrintWriter pwFft = new PrintWriter(filePath + threshold + "-fft-" + numberOfFrames + '-' + name + extension, "UTF-8");
                printHeader(pw, numberOfFrames);
                printFftHeader(pwFft, numberOfFrames);
                pitchCollectorList
                        .stream()
                        .filter(filter)
                        .forEach(pitchCollector ->
                                partition(pitchCollector.getFramePitchValues(), numberOfFrames)
                                        .stream()
                                        .filter(p -> p.size() == numberOfFrames)
                                        .forEach(p -> {
                                            boolean hasHideF0 = hasHideF0Saver.test(p);
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

    private static Predicate<PitchCollector> isTraining = (p) -> WekaPrinter.TRAINING_SET.contains(p.getSampleName());
    private static Predicate<List<PitchValue>> hasHideF0SaverTraining = (p) -> {
        boolean isChanged = p.stream().anyMatch(PitchValue::isChanged);
        long thZeroCount = p.stream().filter(v -> v.getCalculatedThreshold() == 0).count();
        if (thZeroCount == (long) p.size() && rand.nextInt(100) < 10) {
            return true;
        } else {
            return isChanged;
        }
    };
    private static Predicate<PitchCollector> isTest = (p) -> WekaPrinter.TEST_SET.contains(p.getSampleName());
    private static Predicate<List<PitchValue>> hasHideF0SaverTest = (p) -> p.stream().anyMatch(PitchValue::isChanged);


    private static List<String> TRAINING_SET = Arrays.asList(
            "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12", "F13", "F14", "F15", "F16",
            "M1", "M2", "M3", "M4", "M5", "M6", "M7", "M8", "M9", "M10", "M11", "M12", "M13", "M14", "M15", "M16");

    private static List<String> TEST_SET = Arrays.asList(
            "F17", "F18", "F19", "F20", "F21", "F22", "F23", "F24",
            "M17", "M18", "M19", "M20", "M21", "M22", "M23", "M24");
}
