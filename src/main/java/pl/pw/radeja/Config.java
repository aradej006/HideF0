package pl.pw.radeja;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Config {
    public static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();
    public static final boolean CALCULATE_ALLOW_PLACES = true;
    public static final boolean DECODE_FILES = false;
    public static final boolean CALCUALTE_PESQ = false;
    public static final boolean PRINT_WEKA_FILES = false;
    public static final boolean PRINT_WEKA_VECTOR_FILES = false;
    public static final boolean PRINT_CALCULATED_THRESHOLDS = false;
    public static final boolean PRINT_HISTOGRAM = true;
    public static final HideF0Type HIDE_F0_TYPE = HideF0Type.FIRST_LAST;

    public static final List<Integer> THRESHOLDS = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20, 25, 30, 35, 40, 45, 50, 60, 70, 80, 90, 100, 110, 127);
//public static final List<Integer> THRESHOLDS = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
//    static final List<Integer> THRESHOLDS = Arrays.asList(0);

    public static List<String> getSamples() {
        final String baseMalePath = "E:/mgr//master-thesis/TIMIT_M/";
        final String baseFemalePath = "E:/mgr//master-thesis/TIMIT_F/";
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
        return samples;
    }

    public enum HideF0Type {
        FIRST_LAST("FL"),
        FIRST_LAST_RAND("FLRand"),
        FIRST_FIRST("FF");

        private final String name;

        HideF0Type(String s) {
            name = s;
        }

        public String toString() {
            return this.name;
        }

    }
}
