import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.NotNull;
import pl.pw.radeja.weka.WekaResult;
import pl.pw.radeja.weka.WekaResultPrinter;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WekaResultsCollector {

    static String trainExt = "-1-train.arff";
    static String testExt = "-1-test.arff";

    public static void main(@NotNull final String[] args) throws InterruptedException {
        List<WekaResult> results = Collections.synchronizedList(new ArrayList<>());
        List<Integer> thresholds = FinePithExtractor.getThresholds();

        String path = "D:/PracaMgr/master-thesis/wekaFLRand/hideF0-";

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ExecutorService es = Executors.newCachedThreadPool();
        thresholds.forEach(th -> es.execute(() -> {
            try {
                runMachineLearning(path + th, results);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

        es.shutdown();
        boolean finished = es.awaitTermination(24, TimeUnit.HOURS);
        if (!finished) {
            throw new Error("Some Error");
        }


        WekaResultPrinter.print(results);

        stopWatch.stop();
        System.out.println("\n\nTotal time:" + (stopWatch.getTime() / 1000) + "[s]");
    }

    static List<Classifier> getClassifiers() {
        List<Classifier> classifiers = new ArrayList<>();
        classifiers.add(new AdaBoostM1());
        classifiers.add(new Logistic());
        classifiers.add(new MultilayerPerceptron());
        classifiers.add(new NaiveBayes());
        classifiers.add(new RandomForest());
//        classifiers.add(new SMO());
        return classifiers;
    }

    private static void runMachineLearning(String path, List<WekaResult> results) throws InterruptedException {
        List<Classifier> cls = getClassifiers();
        ExecutorService es = Executors.newCachedThreadPool();

        cls.forEach(classifier -> es.execute(() -> {
            try {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                System.out.println("Running data for:\t" + path);
                ArffLoader.ArffReader trainLoader = new ArffLoader.ArffReader(new BufferedReader(new FileReader(path + trainExt)));
                ArffLoader.ArffReader testLoader = new ArffLoader.ArffReader(new BufferedReader(new FileReader(path + testExt)));

                Instances train = trainLoader.getData();
                train.setClassIndex(train.numAttributes() - 1);
                Instances test = testLoader.getData();
                test.setClassIndex(test.numAttributes() - 1);
                System.out.println("Loaded data for:\t" + path);

                System.out.println("Building for:\t" + path + "\tClassifier:\t" + classifier.getClass().getName());
                classifier.buildClassifier(train);
                System.out.println("Built for:\t" + path + "\tClassifier:\t" + classifier.getClass().getName());

                System.out.println("Evaluating for:\t" + path + "\tClassifier:\t" + classifier.getClass().getName());

                Evaluation eval = new Evaluation(train);
                eval.evaluateModel(classifier, test);
                WekaResult result = new WekaResult(eval, classifier, path);
                synchronized (results) {
                    results.add(result);
                }
                System.out.println("Evaluated for:\t" + path + "\tClassifier:\t" + classifier.getClass().getName());
                System.out.println("Result:\t" + result.getPtcCorrect() + "\t" + result.getRocArea());
                stopWatch.stop();
                System.out.println("Total time:" + (stopWatch.getTime() / 1000) + "[s]\n\n");
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
