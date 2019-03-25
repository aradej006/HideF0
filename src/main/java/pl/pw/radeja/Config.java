package pl.pw.radeja;

import lombok.Getter;
import pl.pw.radeja.human.HumanSample;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.trees.RandomForest;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Config {
    public static final Path BASE_PATH = Paths.get("");
    public static final Path HUMAN_SAMPLE = Paths.get("").resolve("HumanTestSamples");
    public static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();
    public static final boolean CALCULATE_ALLOW_PLACES = false;
    public static final boolean DECODE_FILES = false;
    public static final boolean CALCUALTE_PESQ = false;
    public static final boolean PRINT_WEKA_FILES = false;
    public static final boolean PRINT_WEKA_VECTOR_FILES = true;
    public static final int WEKA_FRAMES_PER_RECORD = 250;
    public static final boolean PRINT_CALCULATED_THRESHOLDS = false;
    public static final boolean PRINT_HISTOGRAM = false;
    public static final boolean PRINT_WEKA_HISTOGRAM = true;
    public static final int WEKA_BYTES_WINDOW = 250;
    public static final HideF0Type HIDE_F0_TYPE = HideF0Type.FIRST_FIRST_RAND;

//        public static final List<Integer> THRESHOLDS = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20, 25, 30, 35, 40, 45, 50, 60, 70, 80, 90, 100, 110, 127);
//    public static final List<Integer> THRESHOLDS = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8);
//    public static final List<Integer> THRESHOLDS = Arrays.asList(9, 10, 15, 20, 25, 30, 35, 40, 45);
//    public static final List<Integer> THRESHOLDS = Arrays.asList(50, 60, 70, 80, 90, 100, 110, 127);
    public static final List<Integer> THRESHOLDS = Arrays.asList(0, 50);

    public static List<String> getSamples() {
        final String baseMalePath = BASE_PATH.resolve("TIMIT_M").toAbsolutePath().toString() + '/';
        final String baseFemalePath = BASE_PATH.resolve("TIMIT_F").toAbsolutePath().toString() + '/';
//        final int maleLimit = 2;
        final int maleLimit = 25;
//        final int femaleLimit = 1;
        final int femaleLimit = 25;
        List<String> samples = new ArrayList<>();
        for (int i = 1; i < maleLimit; i++) {
            samples.add(baseMalePath + i);
        }
        for (int i = 1; i < femaleLimit; i++) {
            samples.add(baseFemalePath + i);
        }
//        samples.add(baseFemalePath + "24");
        return samples;
    }

    public static List<HumanSample> getHumanTestSamples() {
        final String femalePath = HUMAN_SAMPLE.resolve("F").toAbsolutePath().toString() + "/";
        final String malePath = HUMAN_SAMPLE.resolve("M").toAbsolutePath().toString() + "/";

        List<HumanSample> samples = new ArrayList<>();
        //Female
        samples.add(new HumanSample(femalePath + "DR1_FAKS0_SI1573", 0f, HideF0Type.FIRST_LAST));
        samples.add(new HumanSample(femalePath + "DR1_FDAC1_SA1", 4f, HideF0Type.FIRST_LAST));
        samples.add(new HumanSample(femalePath + "DR1_FDAC1_SI844", 10f, HideF0Type.FIRST_LAST));
        samples.add(new HumanSample(femalePath + "DR1_FEJM0_SI634", 15f, HideF0Type.FIRST_LAST));
        samples.add(new HumanSample(femalePath + "DR1_FEJM0_SX364", 25f, HideF0Type.FIRST_LAST));
        samples.add(new HumanSample(femalePath + "DR1_FELC0_SI1386", 35f, HideF0Type.FIRST_LAST));

        samples.add(new HumanSample(femalePath + "DR2_FCMR0_SI1105", 0f, HideF0Type.FIRST_LAST_RAND));
        samples.add(new HumanSample(femalePath + "DR2_FCMR0_SX205", 30f, HideF0Type.FIRST_LAST_RAND));
        samples.add(new HumanSample(femalePath + "DR2_FDRD1_SI1566", 35f, HideF0Type.FIRST_LAST_RAND));
        samples.add(new HumanSample(femalePath + "DR2_FDRD1_SI2149", 40f, HideF0Type.FIRST_LAST_RAND));
        samples.add(new HumanSample(femalePath + "DR2_FJAS0_SI770", 50f, HideF0Type.FIRST_LAST_RAND));
        samples.add(new HumanSample(femalePath + "DR2_FJRE0_SI1587", 127f, HideF0Type.FIRST_LAST_RAND));

        samples.add(new HumanSample(femalePath + "DR2_FJWB0_SI635", 0f, HideF0Type.FIRST_FIRST));
        samples.add(new HumanSample(femalePath + "DR2_FJWB0_SI992", 1f, HideF0Type.FIRST_FIRST));
        samples.add(new HumanSample(femalePath + "DR2_FRAM1_SI730", 2f, HideF0Type.FIRST_FIRST));
        samples.add(new HumanSample(femalePath + "DR1_FDAC1_SI844", 5f, HideF0Type.FIRST_FIRST));
        samples.add(new HumanSample(femalePath + "DR1_FELC0_SI1386", 15f, HideF0Type.FIRST_FIRST));
        samples.add(new HumanSample(femalePath + "DR2_FCMR0_SX205", 30f, HideF0Type.FIRST_FIRST));

        samples.add(new HumanSample(femalePath + "DR2_FCMR0_SX205", 0f, HideF0Type.FIRST_FIRST_RAND));
        samples.add(new HumanSample(femalePath + "DR2_FJWB0_SX365", 25f, HideF0Type.FIRST_FIRST_RAND));
        samples.add(new HumanSample(femalePath + "DR2_FJAS0_SI770", 30f, HideF0Type.FIRST_FIRST_RAND));
        samples.add(new HumanSample(femalePath + "DR2_FJRE0_SI1587", 35f, HideF0Type.FIRST_FIRST_RAND));
        samples.add(new HumanSample(femalePath + "DR1_FEJM0_SI634", 40f, HideF0Type.FIRST_FIRST_RAND));
        samples.add(new HumanSample(femalePath + "DR1_FAKS0_SI1573", 60f, HideF0Type.FIRST_FIRST_RAND));

        samples.add(new HumanSample(femalePath + "DR2_FJAS0_SI770", 0f, HideF0Type.FIRST_MIDDLE));
        samples.add(new HumanSample(femalePath + "DR2_FJWB0_SI992", 1f, HideF0Type.FIRST_MIDDLE));
        samples.add(new HumanSample(femalePath + "DR2_FJWB0_SX365", 2f, HideF0Type.FIRST_MIDDLE));
        samples.add(new HumanSample(femalePath + "DR2_FPAS0_SI2204", 3f, HideF0Type.FIRST_MIDDLE));
        samples.add(new HumanSample(femalePath + "DR2_FRAM1_SX190", 9f, HideF0Type.FIRST_MIDDLE));
        samples.add(new HumanSample(femalePath + "DR2_FRAM1_SX370", 20f, HideF0Type.FIRST_MIDDLE));

        samples.add(new HumanSample(femalePath + "DR1_FEJM0_SI634", 0f, HideF0Type.FIRST_MIDDLE_RAND));
        samples.add(new HumanSample(femalePath + "DR2_FCMR0_SI1105", 2f, HideF0Type.FIRST_MIDDLE_RAND));
        samples.add(new HumanSample(femalePath + "DR2_FJAS0_SI770", 8f, HideF0Type.FIRST_MIDDLE_RAND));
        samples.add(new HumanSample(femalePath + "DR2_FJRE0_SI1587", 15f, HideF0Type.FIRST_MIDDLE_RAND));
        samples.add(new HumanSample(femalePath + "DR2_FRAM1_SI730", 25f, HideF0Type.FIRST_MIDDLE_RAND));
        samples.add(new HumanSample(femalePath + "DR2_FSLB1_SI891", 50f, HideF0Type.FIRST_MIDDLE_RAND));
//        Male
        samples.add(new HumanSample(malePath + "DR1_MDAB0_SI1039", 0f, HideF0Type.FIRST_LAST));
        samples.add(new HumanSample(malePath + "DR1_MJSW0_SA1", 4f, HideF0Type.FIRST_LAST));
        samples.add(new HumanSample(malePath + "DR1_MJSW0_SI1010", 10f, HideF0Type.FIRST_LAST));
        samples.add(new HumanSample(malePath + "DR1_MJSW0_SX380", 15f, HideF0Type.FIRST_LAST));
        samples.add(new HumanSample(malePath + "DR1_MREB0_SI1375", 25f, HideF0Type.FIRST_LAST));
        samples.add(new HumanSample(malePath + "DR1_MREB0_SI2005", 35f, HideF0Type.FIRST_LAST));

        samples.add(new HumanSample(malePath + "DR1_MREB0_SX205", 0f, HideF0Type.FIRST_LAST_RAND));
        samples.add(new HumanSample(malePath + "DR1_MRJO0_SI734", 30f, HideF0Type.FIRST_LAST_RAND));
        samples.add(new HumanSample(malePath + "DR1_MRJO0_SI1364", 35f, HideF0Type.FIRST_LAST_RAND));
        samples.add(new HumanSample(malePath + "DR1_MSTK0_SI1024", 40f, HideF0Type.FIRST_LAST_RAND));
        samples.add(new HumanSample(malePath + "DR1_MSTK0_SI2222", 50f, HideF0Type.FIRST_LAST_RAND));
        samples.add(new HumanSample(malePath + "DR2_MABW0_SI1230", 127f, HideF0Type.FIRST_LAST_RAND));

        samples.add(new HumanSample(malePath + "DR2_MABW0_SX134", 0f, HideF0Type.FIRST_FIRST));
        samples.add(new HumanSample(malePath + "DR2_MBJK0_SI2128", 1f, HideF0Type.FIRST_FIRST));
        samples.add(new HumanSample(malePath + "DR2_MBJK0_SX365", 2f, HideF0Type.FIRST_FIRST));
        samples.add(new HumanSample(malePath + "DR1_MRJO0_SI734", 5f, HideF0Type.FIRST_FIRST));
        samples.add(new HumanSample(malePath + "DR1_MJSW0_SX380", 15f, HideF0Type.FIRST_FIRST));
        samples.add(new HumanSample(malePath + "DR1_MDAB0_SI1039", 30f, HideF0Type.FIRST_FIRST));

        samples.add(new HumanSample(malePath + "DR1_MREB0_SX205", 0f, HideF0Type.FIRST_FIRST_RAND));
        samples.add(new HumanSample(malePath + "DR2_MRGG0_SI1829", 25f, HideF0Type.FIRST_FIRST_RAND));
        samples.add(new HumanSample(malePath + "DR1_MRJO0_SI734", 30f, HideF0Type.FIRST_FIRST_RAND));
        samples.add(new HumanSample(malePath + "DR2_MRCZ0_SX101", 35f, HideF0Type.FIRST_FIRST_RAND));
        samples.add(new HumanSample(malePath + "DR1_MRJO0_SI1364", 40f, HideF0Type.FIRST_FIRST_RAND));
        samples.add(new HumanSample(malePath + "DR2_MRCZ0_SI1541", 60f, HideF0Type.FIRST_FIRST_RAND));

        samples.add(new HumanSample(malePath + "DR2_MBJK0_SI2128", 0f, HideF0Type.FIRST_MIDDLE));
        samples.add(new HumanSample(malePath + "DR2_MDLD0_SI913", 1f, HideF0Type.FIRST_MIDDLE));
        samples.add(new HumanSample(malePath + "DR2_MRCZ0_SI1541", 2f, HideF0Type.FIRST_MIDDLE));
        samples.add(new HumanSample(malePath + "DR2_MRCZ0_SX101", 3f, HideF0Type.FIRST_MIDDLE));
        samples.add(new HumanSample(malePath + "DR2_MWEW0_SI1991", 9f, HideF0Type.FIRST_MIDDLE));
        samples.add(new HumanSample(malePath + "DR2_MWVW0_SI846", 20f, HideF0Type.FIRST_MIDDLE));

        samples.add(new HumanSample(malePath + "DR1_MREB0_SI1375", 0f, HideF0Type.FIRST_MIDDLE_RAND));
        samples.add(new HumanSample(malePath + "DR2_MMDM2_SI1452", 2f, HideF0Type.FIRST_MIDDLE_RAND));
        samples.add(new HumanSample(malePath + "DR2_MRCZ0_SX101", 8f, HideF0Type.FIRST_MIDDLE_RAND));
        samples.add(new HumanSample(malePath + "DR2_MRGG0_SI1829", 15f, HideF0Type.FIRST_MIDDLE_RAND));
        samples.add(new HumanSample(malePath + "DR2_MTAS1_SX388", 25f, HideF0Type.FIRST_MIDDLE_RAND));
        samples.add(new HumanSample(malePath + "DR2_MWEW0_SI1991", 50f, HideF0Type.FIRST_MIDDLE_RAND));
        return samples;
    }

    public static List<Classifier> getWekaClassifiers() {
        List<Classifier> classifiers = new ArrayList<>();
//        classifiers.add(new AdaBoostM1());
//        classifiers.add(new Logistic());
//        classifiers.add(new NaiveBayes());
        classifiers.add(new RandomForest());
        return classifiers;
    }

    public static final double WEKA_TRAINING_TH_0_RAND = 0.9;

    @Getter
    public enum HideF0Type {
        NON("N", "VectorN"),
        FIRST_LAST("FL", "VectorFL"),
        FIRST_LAST_RAND("FLRand", "VectorFLRand"),
        FIRST_FIRST("FF", "VectorFF"),
        FIRST_FIRST_RAND("FFRand", "VectorFFRand"),
        FIRST_MIDDLE("FM", "VectorFM"),
        FIRST_MIDDLE_RAND("FMRand", "VectorFMRand");

        private final String name;
        private final String vectorName;

        HideF0Type(String name, String vectorName) {
            this.name = name;
            this.vectorName = vectorName;
        }
    }

    public static String getSampleNameFromPath(String path) {
        if (path.contains("TIMIT_")) {
            String temp = path.split("TIMIT_")[1];
            return temp.charAt(0) + temp.split("-")[0].substring(2);
        } else if (path.contains("HumanTestSamples")) {
            String temp = path.contains("DR1") ? path.split("DR1_")[1] : path.split(("DR2_"))[1];
            return temp;
        } else {
            throw new Error("Bad path: " + path);
        }

    }

    public static ExecutorService getExecutorService() {
//        return Executors.newFixedThreadPool(NUMBER_OF_THREADS - 1);
        return Executors.newFixedThreadPool(2);
//        return Executors.newFixedThreadPool(4);
//        return Executors.newSingleThreadExecutor();
    }
}
