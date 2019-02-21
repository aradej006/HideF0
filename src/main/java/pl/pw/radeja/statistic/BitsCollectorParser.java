package pl.pw.radeja.statistic;


import pl.pw.radeja.speex.result.BitsCollector;
import pl.pw.radeja.speex.result.SpeexBitsName;
import pl.pw.radeja.speex.result.SpeexBits;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class BitsCollectorParser {

    static Map<Integer, Long> getHistogramData(BitsCollector bitsCollector) {
        return buildIntByteList(splitToBytes(buildBits(bitsCollector.getBitsToSave()))).stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
    }

    private static String buildBits(List<SpeexBits> speexBitsList) {
        StringBuilder builder = new StringBuilder();
        speexBitsList.stream()
                .filter(v -> !v.getSpeexBitsName().equals(SpeexBitsName.SIZE))
                .forEach(b -> builder.append(intToBinaryString(b.getBitsData())));
        return builder.toString();
    }

    private static String intToBinaryString(int val) {
        return String.format("%8s", Integer.toBinaryString(val)).replace(' ', '0');
    }

    private static String[] splitToBytes(String bits) {
        return bits.split("(?<=\\G.{8})");
    }

    private static List<Integer> buildIntByteList(String[] byteStrings) {
        return Arrays.stream(byteStrings).map(b -> Integer.parseInt(b, 2)).collect(Collectors.toList());
    }
}
