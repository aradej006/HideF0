package pl.pw.radeja.weka;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;

public class WekaResult {
    private final Evaluation eval;
    private final Classifier classifier;
    private final String path;

    public WekaResult(Evaluation eval, Classifier classifier, String path) {
        this.eval = eval;
        this.classifier = classifier;
        this.path = path;
    }

    public Evaluation getEval() {
        return eval;
    }

    public double getRocArea() {
        return eval.weightedAreaUnderROC();
    }

    public double getPtcCorrect() {
        return eval.pctCorrect();
    }

    public double getPrecision() {
        return eval.weightedPrecision();
    }

    public double getRecall() {
        return eval.weightedRecall();
    }

    public Classifier getClassifier() {
        return classifier;
    }

    public String getPath() {
        return path;
    }

    public Integer getThreshold() {
        return Integer.parseInt(path.substring(path.indexOf("hideF0-") + 7).replace("-1.arff", ""));
    }

    @Override
    public String toString() {
        return "PtcCorrect:\t" + getPtcCorrect() + "\tROC Area:\t" + getRocArea() + "\tClassifier:\t" + getClassifier();
    }
}
