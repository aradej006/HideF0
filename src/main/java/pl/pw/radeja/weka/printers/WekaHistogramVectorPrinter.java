package pl.pw.radeja.weka.printers;

import lombok.extern.slf4j.Slf4j;
import pl.pw.radeja.Config;
import pl.pw.radeja.speex.result.BitsCollector;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static pl.pw.radeja.Config.BASE_PATH;
import static pl.pw.radeja.statistic.BitsCollectorParser.bitsCollectorToIntByteList;

@Slf4j
public class WekaHistogramVectorPrinter {
    private static final String FILE_PATH = BASE_PATH.resolve("wekaHistogram" + Config.HIDE_F0_TYPE.getVectorName()) + "/hideF0-";

    private static final Predicate<BitsCollector> hasHideF0SaverTraining = c -> WekaVectorPrinter.TRAINING_SET_HIDE_FO.contains(c.getSampleName());
    private static final Predicate<BitsCollector> hasHideF0SaverTest = c -> WekaVectorPrinter.TEST_SET_HIDE_FO.contains(c.getSampleName());
    private static final Predicate<BitsCollector> isTraining = c -> WekaVectorPrinter.getTrainingSet().contains(c.getSampleName());
    private static final Predicate<BitsCollector> isTest = c -> WekaVectorPrinter.getTestSet().contains(c.getSampleName());

    private WekaHistogramVectorPrinter() {
    }

    public static void generateHistogramData(List<BitsCollector> list, int bytesWindow) {
        Map<Integer, List<BitsCollector>> thresholdToBitsCollectors = list.stream().collect(Collectors.groupingBy(BitsCollector::getThreshold));
        generateFiles(thresholdToBitsCollectors, bytesWindow, "train", isTraining, hasHideF0SaverTraining);
        generateFiles(thresholdToBitsCollectors, bytesWindow, "test", isTest, hasHideF0SaverTest);
    }

    private static void generateFiles(Map<Integer, List<BitsCollector>> thresholdToBitsCollectors,
                                      int bytesWindow,
                                      String name,
                                      Predicate<BitsCollector> filter,
                                      Predicate<BitsCollector> hasHideF0Saver) {

        thresholdToBitsCollectors.forEach((threshold, bitsCollectorList) -> {
            try {
                Path path = Paths.get(FILE_PATH + threshold + "-" + bytesWindow + '-' + name + WekaEnum.EXTENSION);
                Files.createDirectories(path.getParent());
                PrintWriter pw = new PrintWriter(path.toFile(), "UTF-8");
                printHeader(pw, bytesWindow);
                bitsCollectorList
                        .stream()
                        .filter(filter)
                        .forEach(bitsCollector ->
                                partition(getHistogramData(bitsCollector), bytesWindow)
                                        .forEach(p -> {
                                            boolean hasHideF0 = hasHideF0Saver.test(bitsCollector);
                                            String bytes = String.join(",", p);
                                            bytes += "," + (hasHideF0 ? WekaEnum.HIDE_F0.getHasHideF0() : WekaEnum.NO_HIDE_F0.getHasHideF0());
                                            pw.println(bytes);
                                        })
                        );
                pw.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        });
    }


    private static String[] getHistogramData(BitsCollector bitsCollector) {
        return bitsCollectorToIntByteList(bitsCollector).stream().map(String::valueOf).toArray(String[]::new);
    }

    private static void printHeader(PrintWriter pr, int numberOfFrames) {
        pr.println("@RELATION 'HistogramHideF0'");
        for (int i = 0; i < numberOfFrames; i++) {
            pr.println("@ATTRIBUTE F" + i + " INTEGER");
        }
        pr.println("@ATTRIBUTE class {" + WekaEnum.HIDE_F0.getHasHideF0() + "," + WekaEnum.NO_HIDE_F0.getHasHideF0() + "}");
        pr.println("@DATA");
    }


    private static <T> Collection<T[]> partition(T[] bytes, int windowSize) {
        Collection<T[]> collection = new ArrayList<>();
        for (int i = 0; i + windowSize < bytes.length; i += windowSize) {
            collection.add(Arrays.copyOfRange(bytes, i, i + windowSize));
        }
        return collection;
    }
}
