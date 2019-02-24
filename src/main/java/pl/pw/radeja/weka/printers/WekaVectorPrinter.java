package pl.pw.radeja.weka.printers;

import pl.pw.radeja.Config;
import pl.pw.radeja.speex.pitch.changers.LinearApproximateChanger;
import pl.pw.radeja.speex.pitch.collectors.PitchCollector;
import pl.pw.radeja.speex.pitch.collectors.PitchValue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static pl.pw.radeja.Config.BASE_PATH;

public class WekaVectorPrinter {
    private final static String filePath = BASE_PATH.resolve("weka" + Config.HIDE_F0_TYPE.getVectorName()) + "/hideF0-";
    private final static String extension = ".arff";
    private final static String hasHideF0 = "HideF0";
    private final static String hasNotHideF0 = "NoHideF0";
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
                Path path = Paths.get(filePath + threshold + "-" + numberOfFrames + '-' + name + extension);
                Files.createDirectories(path.getParent());
                PrintWriter pw = new PrintWriter(path.toFile(), "UTF-8");
                printHeader(pw, numberOfFrames);
                pitchCollectorList
                        .stream()
                        .filter(filter)
                        .forEach(pitchCollector ->
                                        partition(pitchCollector.getFramePitchValues(), numberOfFrames)
                                                .stream()
                                                .filter(p -> p.size() == numberOfFrames)
                                                .forEach(p -> {
                                                    boolean hasHideF0 = hasHideF0Saver.test(p);
                                                    //print normal;
                                                    pw.println(p.stream().map(framePitchValues -> {
                                                        List<Integer> delta = new ArrayList<>();
                                                        int first = framePitchValues.getPitchValues().get(0);
                                                        int last = framePitchValues.getPitchValues().get(3);
                                                        delta.add(LinearApproximateChanger.LinearApprox(first, last, framePitchValues.getPitchValues().size(), 1) - framePitchValues.getPitchValues().get(1));
                                                        delta.add(LinearApproximateChanger.LinearApprox(first, last, framePitchValues.getPitchValues().size(), 2) - framePitchValues.getPitchValues().get(2));
                                                        return delta.stream().map(Math::abs).map(Objects::toString).collect(Collectors.toList());
                                                    }).flatMap(List::stream)
                                                            .collect(Collectors.joining(",")) + "," + (hasHideF0 ? WekaVectorPrinter.hasHideF0 : WekaVectorPrinter.hasNotHideF0));

//                                            pw.println(p.stream()
//                                                    .map(PitchValue::getCalculatedThreshold)
//                                                    .map(Objects::toString)
//                                                    .collect(Collectors.joining(",")) + "," + (hasHideF0 ? WekaVectorPrinter.hasHideF0 : WekaVectorPrinter.hasNotHideF0));
                                                })
                        );
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void printHeader(PrintWriter pr, int numberOfFrames) {
        pr.println("@RELATION 'HideF0'");
        for (int i = 0; i < numberOfFrames * 4; i++) {
            if (i % 4 == 1 || i % 4 == 2) {
                pr.println("@ATTRIBUTE F" + i + " INTEGER");
            }
        }
        pr.println("@ATTRIBUTE class {" + hasHideF0 + "," + hasNotHideF0 + "}");
        pr.println("@DATA");
    }

    private static <T> Collection<List<T>> partition(List<T> list, int size) {
        Collection<List<T>> collection = new ArrayList<>();
        for (int i = 0; (i + size) < list.size(); i++) {
            collection.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return collection;
    }

    private static Predicate<PitchCollector> isTraining = (p) -> WekaVectorPrinter.TRAINING_SET.contains(p.getSampleName());
    private static Predicate<List<PitchValue>> hasHideF0SaverTraining = (p) -> {
        boolean isChanged = p.stream().anyMatch(PitchValue::isChanged);
        long thZeroCount = p.stream().filter(v -> v.getCalculatedThreshold() == 0).count();
//        if (thZeroCount == (long) p.size() && rand.nextInt(100) < 10) {
        if (thZeroCount == (long) p.size()) {
            return false;
        } else {
            return isChanged;
        }
    };
    private static Predicate<PitchCollector> isTest = (p) -> WekaVectorPrinter.TEST_SET.contains(p.getSampleName());
    private static Predicate<List<PitchValue>> hasHideF0SaverTest = (p) -> p.stream().anyMatch(PitchValue::isChanged);


    private static List<String> TRAINING_SET = Arrays.asList(
            "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12", "F13", "F14", "F15", "F16",
            "M1", "M2", "M3", "M4", "M5", "M6", "M7", "M8", "M9", "M10", "M11", "M12", "M13", "M14", "M15", "M16");

    private static List<String> TEST_SET = Arrays.asList(
            "F17", "F18", "F19", "F20", "F21", "F22", "F23", "F24",
            "M17", "M18", "M19", "M20", "M21", "M22", "M23", "M24");
}
