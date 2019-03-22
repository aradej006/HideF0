package pl.pw.radeja.speex.pitch.collectors;


import pl.pw.radeja.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PitchCollector {
    private String path;
    private Integer threshold;
    private List<PitchValue> framePitchValues;
    private int numberOfFrame = -1;

    public PitchCollector(String path, Integer threshold) {
        this.path = path;
        this.threshold = threshold;
        framePitchValues = Collections.synchronizedList(new ArrayList<>());
    }

    public boolean addPitch(List<Integer> pitchValues, boolean changed, Integer calculatedThreshold, Integer calculatedThresholdAfterHideF0) {
        numberOfFrame++;
        return this.framePitchValues.add(new PitchValue(numberOfFrame, pitchValues, changed, calculatedThreshold, calculatedThresholdAfterHideF0));
    }

    public List<PitchValue> getFramePitchValues() {
        return framePitchValues;
    }

    public String getPath() {
        return path;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public String getSampleName() {
        return Config.getSampleNameFromPath(path);
    }
}
