package pl.pw.radeja.speex.result;

import java.util.*;

public class AllowPlacesPrint {
    public static void print(List<AllowPlaces> result) {
        Map<String, Map<Float, AllowPlaces>> resultMap = getResultMap(result);
        ArrayList<String> sampleNames = new ArrayList<>(resultMap.keySet());
        Collections.sort(sampleNames);
        ArrayList<Float> thresholds = new ArrayList<>(resultMap.get(sampleNames.get(0)).keySet());
        Collections.sort(thresholds);
        System.out.println("\n\n\n");
        System.out.println("Allow Places");
        System.out.println("Threshold\t" + String.join("\t", sampleNames));
        thresholds.forEach(t -> {
            List<String> values = new ArrayList<>();
            values.add(t.toString());
            sampleNames.forEach(s -> values.add(resultMap.get(s).get(t).getNumberOfAllowPlaces().toString()));
            System.out.println(String.join("\t", values));
        });
        System.out.println("\n\n\n");
    }

    private static Map<String, Map<Float, AllowPlaces>> getResultMap(List<AllowPlaces> result) {
        Map<String, Map<Float, AllowPlaces>> resultMap = new HashMap<>();
        result.forEach(r -> {
            resultMap.putIfAbsent(r.getSampleName(), new HashMap<>());
            resultMap.get(r.getSampleName()).putIfAbsent(r.getThreshold(), r);
        });
        return resultMap;
    }
}
