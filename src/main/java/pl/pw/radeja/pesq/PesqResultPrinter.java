package pl.pw.radeja.pesq;

import pl.pw.radeja.pesq.common.PesqResult;

import java.util.*;

public class PesqResultPrinter {
    public static void print(List<PesqResult> result) {
        Map<String, Map<Integer, PesqResult>> resultMap = getResultMap(result);
        ArrayList<String> sampleNames = new ArrayList<>(resultMap.keySet());
        Collections.sort(sampleNames);
        ArrayList<Integer> thresholds = new ArrayList<>(resultMap.get(sampleNames.get(0)).keySet());
        Collections.sort(thresholds);
        System.out.println("\n\n\n");
        System.out.println("PESQ Raw MOS");
        System.out.println("Threshold\t" + String.join("\t", sampleNames));
        thresholds.forEach(t -> {
            List<String> values = new ArrayList<>();
            values.add(t.toString());
            sampleNames.forEach(s -> values.add(resultMap.get(s).get(t).getRawMos().toString()));
            System.out.println(String.join("\t", values));
        });
        System.out.println("\n\n\n");
        System.out.println("PESQ MOS LQO");
        System.out.println("Threshold\t" + String.join("\t", sampleNames));
        thresholds.forEach(t -> {
            List<String> values = new ArrayList<>();
            values.add(t.toString());
            sampleNames.forEach(s -> values.add(resultMap.get(s).get(t).getMosLqo().toString()));
            System.out.println(String.join("\t", values));
        });
        System.out.println("\n\n\n");
    }

    private static Map<String, Map<Integer, PesqResult>> getResultMap(List<PesqResult> result) {
        Map<String, Map<Integer, PesqResult>> resultMap = new HashMap<>();
        result.forEach(r -> {
            resultMap.putIfAbsent(r.getSampleName(), new HashMap<>());
            resultMap.get(r.getSampleName()).putIfAbsent(r.getSampleThreshold(), r);
        });
        return resultMap;
    }
}
