package pl.pw.radeja;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.NotNull;
import pl.pw.radeja.weka.WekaResult;
import pl.pw.radeja.weka.WekaResultPrinter;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static pl.pw.radeja.Config.*;

@Slf4j
public class WekaResultsCollector {

    static String trainExt = "-" + WEKA_FRAMES_PER_RECORD + "-train.arff";
    static String testExt = "-" + WEKA_FRAMES_PER_RECORD + "-test.arff";

    public static void main(@NotNull final String[] args) throws InterruptedException {

        Map<String, List<WekaResult>> resultsMap = new HashMap<>();
        if (PRINT_WEKA_FILES) {
            String pathFL = BASE_PATH.resolve("weka" + HIDE_F0_TYPE.getName()).toAbsolutePath().toString() + "/hideF0-";
            resultsMap.put(pathFL, Collections.synchronizedList(new ArrayList<>()));
        }
        if (PRINT_WEKA_VECTOR_FILES) {
            String pathFLRand = BASE_PATH.resolve("weka" + HIDE_F0_TYPE.getVectorName()).toAbsolutePath().toString() + "/hideF0-";
            resultsMap.put(pathFLRand, Collections.synchronizedList(new ArrayList<>()));
        }
        if (resultsMap.isEmpty()) {
            throw new Error("Add new path to Weka files to your new HideF0 variant: " + Config.HIDE_F0_TYPE.getName());
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ExecutorService es = Config.getExecutorService();
        Config.THRESHOLDS.forEach(th -> es.execute(() -> resultsMap.keySet().forEach(path -> {
            try {
                runMachineLearning(path + th, resultsMap.get(path));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        })));

        es.shutdown();
        boolean finished = es.awaitTermination(24, TimeUnit.HOURS);
        if (!finished) {
            throw new Error("Some Error");
        }

        resultsMap.forEach((path, results) -> {
            log.info("\n\nResults for " + path + ":");
            WekaResultPrinter.print(results);
        });

        stopWatch.stop();
        log.info("\n\nTotal time:" + (stopWatch.getTime() / 1000) + "[s]");

    }

    private static void runMachineLearning(String path, List<WekaResult> results) throws InterruptedException {
        List<Classifier> cls = Config.getWekaClassifiers();
        ExecutorService es = Config.getExecutorService();

        cls.forEach(classifier -> es.execute(() -> {
            try {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                log.info("Running data for:\t" + path);
                ArffLoader.ArffReader trainLoader = new ArffLoader.ArffReader(new BufferedReader(new FileReader(path + trainExt)));
                ArffLoader.ArffReader testLoader = new ArffLoader.ArffReader(new BufferedReader(new FileReader(path + testExt)));

                Instances train = trainLoader.getData();
                train.setClassIndex(train.numAttributes() - 1);
                Instances test = testLoader.getData();
                test.setClassIndex(test.numAttributes() - 1);
                log.debug("Loaded data for:\t" + path);

                log.debug("Building for:\t" + path + "\tClassifier:\t" + classifier.getClass().getName());
                classifier.buildClassifier(train);
                log.debug("Built for:\t" + path + "\tClassifier:\t" + classifier.getClass().getName());

                log.debug("Evaluating for:\t" + path + "\tClassifier:\t" + classifier.getClass().getName());

                Evaluation eval = new Evaluation(train);
                eval.evaluateModel(classifier, test);
                WekaResult result = new WekaResult(eval, classifier, path);
                results.add(result);
                log.info("Evaluated for:\t" + path + "\tClassifier:\t" + classifier.getClass().getName());
                log.debug("Result:\t" + result.getPtcCorrect() + "\t" + result.getRocArea());
                stopWatch.stop();
                log.info("Total time:" + (stopWatch.getTime() / 1000) + "[s]\n\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        es.shutdown();
        boolean finished = es.awaitTermination(24, TimeUnit.HOURS);
        if (!finished) {
            throw new Error("Some Error");
        }
    }

}
