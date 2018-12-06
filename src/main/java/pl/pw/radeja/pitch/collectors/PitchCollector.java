package pl.pw.radeja.pitch.collectors;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PitchCollector {
    private String path;
    private Integer threshold;
    private List<PitchValue> pitchValues;
    private int numberOfFrame = -1;

    public PitchCollector(String path, Integer threshold) {
        this.path = path;
        this.threshold = threshold;
        pitchValues = Collections.synchronizedList(new ArrayList<>());
    }

    public boolean addPitch(List<Integer> pitchValues) {
        numberOfFrame++;
        return this.pitchValues.add(new PitchValue(numberOfFrame, pitchValues));
    }

    public List<PitchValue> getPitchValues() {
        return pitchValues;
    }

    public String getPath() {
        return path;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public String getSampleName() {
        String temp = path.split("TIMIT_")[1];
        return temp.charAt(0) + temp.split("-")[0].substring(2);
    }
}
