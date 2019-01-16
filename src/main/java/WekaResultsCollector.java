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

public class WekaResultsCollector {

    static String trainExt = "-1-train.arff";
    static String testExt = "-1-test.arff";

    public static void main(@NotNull final String[] args) {
        List<WekaResult> results = Collections.synchronizedList(new ArrayList<>());
        List<Integer> thresholds = FinePithExtractor.getThresholds();

        String path = "D:/PracaMgr/master-thesis/wekaFLRand/hideF0-";

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        thresholds.forEach(th -> {
            try {
                runMachineLearning(path + th, results);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        WekaResultPrinter.print(results);

        stopWatch.stop();
        System.out.println("\n\nTotal time:" + (stopWatch.getTime() / 1000) + "[s]");
    }

    static List<Classifier> getClassifiers() {
        List<Classifier> classifiers = new ArrayList<>();
        classifiers.add(new RandomForest());
        classifiers.add(new NaiveBayes());
        classifiers.add(new MultilayerPerceptron());
        classifiers.add(new Logistic());
        classifiers.add(new SMO());
        classifiers.add(new AdaBoostM1());
        return classifiers;
    }

    private static void runMachineLearning(String path, List<WekaResult> results) throws IOException {
        System.out.println("Running data for:\t" + path);
        ArffLoader.ArffReader trainLoader = new ArffLoader.ArffReader(new BufferedReader(new FileReader(path + trainExt)));
        ArffLoader.ArffReader testLoader = new ArffLoader.ArffReader(new BufferedReader(new FileReader(path + testExt)));

        Instances train = trainLoader.getData();
        train.setClassIndex(train.numAttributes() - 1);
        Instances test = testLoader.getData();
        test.setClassIndex(test.numAttributes() - 1);
        System.out.println("Loaded data for:\t" + path);

        List<Classifier> cls = getClassifiers();
        cls.forEach(c -> {
            System.out.println("Building for:\t" + path + "\tClassifier:\t" + c.getClass().getName());
            try {
                c.buildClassifier(train);
                System.out.println("Built for:\t" + path + "\tClassifier:\t" + c.getClass().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        cls.forEach(c -> {
            System.out.println("Evaluating for:\t" + path + "\tClassifier:\t" + c.getClass().getName());
            try {
                Evaluation eval = new Evaluation(train);
                eval.evaluateModel(c, test);
                synchronized (results) {
                    results.add(new WekaResult(eval, c, path));
                }
                System.out.println("Evaluated for:\t" + path + "\tClassifier:\t" + c.getClass().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
