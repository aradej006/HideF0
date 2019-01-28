package pl.pw.radeja.statistic;

import pl.pw.radeja.NamesOfBits;
import pl.pw.radeja.SpeexBits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BytesHistogram {
    public static void printHistogram(List<SpeexBits> speexBitsList) {
        List<Integer> bytes = buildIntByteList(splitToByteList(buildBitsString(speexBitsList)));
        Map<Integer, Long> histogramData = bytes.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        List<Integer> values = new ArrayList<>(histogramData.keySet());
        Collections.sort(values);
        System.out.println("ByteValue\tCount");
        values.forEach(v -> System.out.println(v + "\t" + histogramData.get(v)));
    }

    private static String buildBitsString(List<SpeexBits> speexBitsList) {
        StringBuilder builder = new StringBuilder();
        speexBitsList.stream()
                .filter(v -> !v.getNamesOfBits().equals(NamesOfBits.SIZE))
                .forEach(speexBits -> builder.append(intToBinaryString(speexBits.getBitsData(), speexBits.getNumberOfBits())));
        return builder.toString();
    }

    private static String intToBinaryString(int val, int length) {
        StringBuilder s = new StringBuilder(Integer.toBinaryString(val));
        while (s.length() < length) {
            s.insert(0, '0');
        }
        return s.toString();
    }

    private static List<String> splitToByteList(String bits) {
        List<String> byteList = new ArrayList<>();
        int index = 0;
        while (index < bits.length()) {
            byteList.add(bits.substring(index, Math.min(index + 8, bits.length())));
            index += 8;
        }
        return byteList;
    }

    private static List<Integer> buildIntByteList(List<String> byteStringList) {
        return byteStringList.stream().map(bitString -> Integer.parseInt(bitString, 2)).collect(Collectors.toList());
    }
}
