package pl.pw.radeja.weka.printers;

import pl.pw.radeja.Config;
import pl.pw.radeja.speex.pitch.changers.PitchChanger;
import pl.pw.radeja.speex.pitch.collectors.PitchCollector;
import pl.pw.radeja.speex.pitch.collectors.PitchValue;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pl.pw.radeja.Config.BASE_PATH;

public class WekaVectorPrinter {
    private final static String filePath = BASE_PATH.resolve("weka" + Config.HIDE_F0_TYPE.getVectorName()) + "/hideF0-";
    private final static String extension = ".arff";
    private final static String hasHideF0 = "HideF0";
    private final static String hasNotHideF0 = "NoHideF0";
    private final static Long seed = 1L;
    private final static Random rand = new Random(seed);

    public static void print(List<PitchCollector> pitchCollectors, int numberOfFrames) {
        Map<Float, List<PitchCollector>> thresholdToPitchCollector = pitchCollectors.stream().collect(Collectors.groupingBy(PitchCollector::getThreshold));
        printTrainTest(thresholdToPitchCollector, numberOfFrames, "train", isTraining, hasHideF0SaverTraining);
        printTrainTest(thresholdToPitchCollector, numberOfFrames, "test", isTest, hasHideF0SaverTest);
    }

    private static void printTrainTest(Map<Float, List<PitchCollector>> thresholdToPitchCollector,
                                       int numberOfFrames,
                                       String name,
                                       Predicate<PitchCollector> filter,
                                       BiPredicate<List<PitchValue>, PitchCollector> hasHideF0Saver) {
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
                                                .peek(windowFrames -> {
                                                    if (Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_FIRST) ||
                                                            Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_FIRST_RAND) ||
                                                            Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_MIDDLE) ||
                                                            Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_MIDDLE_RAND)) {
                                                        for (int i = 0; i < numberOfFrames; i++) {
                                                            if (windowFrames.get(i).getPitchValues().size() < 5) {
                                                                windowFrames.get(i).getPitchValues().add(windowFrames.get(i + 1).getPitchValues().get(0));
                                                            }
                                                        }
                                                        windowFrames.remove(windowFrames.get(windowFrames.size() - 1));
                                                    }
                                                })
                                                .forEach(p -> {
                                                    boolean hasHideF0 = hasHideF0Saver.test(p, pitchCollector);
                                                    //print normal;
                                                    String frameDeltas = p.stream()
                                                            .map(framePitchValues -> {
                                                                List<Float> delta = new ArrayList<>();
                                                                if (Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_LAST) || Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_LAST_RAND)) {
                                                                    int first = framePitchValues.getPitchValues().get(0);
                                                                    int last = framePitchValues.getPitchValues().get(framePitchValues.getPitchValues().size() - 1);
                                                                    delta.add(PitchChanger.LinearApprox(first, last, framePitchValues.getPitchValues().size(), 1) - framePitchValues.getPitchValues().get(1));
                                                                    delta.add(PitchChanger.LinearApprox(first, last, framePitchValues.getPitchValues().size(), 2) - framePitchValues.getPitchValues().get(2));
                                                                } else if (Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_FIRST) || Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_FIRST_RAND)) {
                                                                    int first = framePitchValues.getPitchValues().get(0);
                                                                    int last = framePitchValues.getPitchValues().get(framePitchValues.getPitchValues().size() - 1);
                                                                    delta.add(PitchChanger.LinearApprox(first, last, framePitchValues.getPitchValues().size(), 1) - framePitchValues.getPitchValues().get(1));
                                                                    delta.add(PitchChanger.LinearApprox(first, last, framePitchValues.getPitchValues().size(), 2) - framePitchValues.getPitchValues().get(2));
                                                                    delta.add(PitchChanger.LinearApprox(first, last, framePitchValues.getPitchValues().size(), 3) - framePitchValues.getPitchValues().get(3));
                                                                } else if (Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_MIDDLE) || Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_MIDDLE_RAND)) {
                                                                    int first = framePitchValues.getPitchValues().get(0);
                                                                    int middle = framePitchValues.getPitchValues().get(2);
                                                                    int last = framePitchValues.getPitchValues().get(4);
                                                                    delta.add(PitchChanger.LinearApprox(first, middle, 3, 1) - framePitchValues.getPitchValues().get(1));
                                                                    delta.add(PitchChanger.LinearApprox(middle, last, 3, 1) - framePitchValues.getPitchValues().get(3));
                                                                }
                                                                return delta.stream().map(Math::abs).map(Objects::toString).collect(Collectors.toList());
                                                            })
                                                            .flatMap(List::stream)
                                                            .collect(Collectors.joining(",")) + "," + (hasHideF0 ? WekaVectorPrinter.hasHideF0 : WekaVectorPrinter.hasNotHideF0);
                                                    pw.println(frameDeltas);

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
            if (Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_FIRST) || Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_FIRST_RAND)) {
                if (i % 4 != 0) {
                    pr.println("@ATTRIBUTE F" + i + " INTEGER");
                }
            } else if (Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_LAST) || Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_LAST_RAND)) {
                if (i % 4 == 1 || i % 4 == 2) {
                    pr.println("@ATTRIBUTE F" + i + " INTEGER");
                }
            } else if (Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_MIDDLE) || Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_MIDDLE_RAND)) {
                if (i % 4 == 1 || i % 4 == 3) {
                    pr.println("@ATTRIBUTE F" + i + " INTEGER");
                }
            }
        }
        pr.println("@ATTRIBUTE class {" + hasHideF0 + "," + hasNotHideF0 + "}");
        pr.println("@DATA");
    }

    private static <T> Collection<List<T>> partition(List<T> list, int size) {
        Collection<List<T>> collection = new ArrayList<>();
        for (int i = 0; (i + size) < list.size(); i++) {
            if (i + size < list.size()) {
                List<T> subList = new ArrayList<>(list.subList(i, i + size));
                if ((Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_FIRST) ||
                        Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_FIRST_RAND) ||
                        Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_MIDDLE) ||
                        Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_MIDDLE_RAND)) && i + size < list.size()) {
                    subList.add(list.get(i + size));
                    collection.add(subList);
                } else if (!(Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_FIRST) ||
                        Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_FIRST_RAND) ||
                        Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_MIDDLE) ||
                        Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_MIDDLE_RAND))) {
                    collection.add(subList);
                }
            }
        }
        return collection;
    }

    private static final Predicate<PitchCollector> isTraining = (p) -> getTrainingSet().contains(p.getSampleName());
    private static final BiPredicate<List<PitchValue>, PitchCollector> hasHideF0SaverTraining = (p, collector) -> {
//        boolean isChanged = p.stream().anyMatch(PitchValue::isChanged);
//        long thZeroCount = p.stream().filter(v -> v.getCalculatedThreshold() == 0).count();
//        if (thZeroCount > 0 && rand.nextInt(100) < Config.WEKA_TRAINING_TH_0_RAND * 100) {
//        if (thZeroCount > 0) {
//            return false;
//        } else {
//            return isChanged;
//        }
        return WekaVectorPrinter.TRAINING_SET_HIDE_FO.contains(collector.getSampleName());
    };
    private static final Predicate<PitchCollector> isTest = (p) -> getTestSet().contains(p.getSampleName());
    private static final BiPredicate<List<PitchValue>, PitchCollector> hasHideF0SaverTest = (p, collector) -> {
//        p.stream().anyMatch(PitchValue::isChanged)
        return WekaVectorPrinter.TEST_SET_HIDE_FO.contains(collector.getSampleName());
    };


    public static final List<String> TRAINING_SET_NO_HIDE_FO = Arrays.asList(
            "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12",
            "M1", "M2", "M3", "M4", "M5", "M6", "M7", "M8", "M9", "M10", "M11", "M12");

    public static final List<String> TRAINING_SET_HIDE_FO = Arrays.asList(
            "F13", "F14", "F15", "F16",
            "M13", "M14", "M15", "M16");

    public static final List<String> TEST_SET_NON_HIDE_F0 = Arrays.asList(
            "F17", "F18", "F19", "F20", "F21", "F22",
            "M17", "M18", "M19", "M20", "M21", "M22");
    public static final List<String> TEST_SET_HIDE_FO = Arrays.asList(
            "F23", "F24",
            "M23", "M24");

    public static List<String> getTrainingSet() {
        return Stream.concat(TRAINING_SET_NO_HIDE_FO.stream(), TRAINING_SET_HIDE_FO.stream()).collect(Collectors.toList());
    }

    public static List<String> getTestSet() {
        return Stream.concat(TEST_SET_NON_HIDE_F0.stream(), TEST_SET_HIDE_FO.stream()).collect(Collectors.toList());
    }

    public static List<String> getHideF0Set() {
        return Stream.concat(TRAINING_SET_HIDE_FO.stream(), TEST_SET_HIDE_FO.stream()).collect(Collectors.toList());
    }

    public static List<String> getNoHideF0Set() {
        return Stream.concat(TRAINING_SET_NO_HIDE_FO.stream(), TEST_SET_NON_HIDE_F0.stream()).collect(Collectors.toList());
    }
}
