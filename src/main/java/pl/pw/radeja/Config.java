package pl.pw.radeja;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class Config {
    static final int NUMBER_OF_THREADS = 48;
    static final boolean CALCULATE_ALLOW_PLACES = true;
    static final boolean DECODE_FILES = false;
    static final boolean CALCUALTE_PESQ = false;
    static final boolean PRINT_WEKA_FILES = false;
    static final boolean PRINT_WEKA_VECTOR_FILES = false;
    static final boolean PRINT_CALCULATED_THRESHOLDS = false;
    static final boolean PRINT_HISTOGRAM = false;
    static final int LOG_LEVEL = 1;
    static final HideF0Type HIDE_F0_TYPE = HideF0Type.FirstLast;

    static final List<Integer> THRESHOLDS = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20, 25, 30, 35, 40, 45, 50, 60, 70, 80, 90, 100, 110, 127);
    //    static final List<Integer> THRESHOLDS = Arrays.asList(0, 1);

    static List<String> getSamples() {
        final String baseMalePath = "D:/PracaMgr/master-thesis/TIMIT_M/";
        final String baseFemalePath = "D:/PracaMgr/master-thesis/TIMIT_F/";
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

    enum HideF0Type {
        FirstLast,
        FirstLastRand,
        FirstFirst
    }
}
