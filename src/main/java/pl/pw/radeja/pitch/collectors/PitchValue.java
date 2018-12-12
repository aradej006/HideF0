package pl.pw.radeja.pitch.collectors;

import java.util.List;

public class PitchValue {
    private int numberOfFrame;
    private List<Integer> pitchValues;
    private boolean changed;
    private int calculatedThreshold;
    private int calculatedThresholdAfterHideF0;

    public PitchValue(int numberOfFrame, List<Integer> pitchValues, boolean changed, int calculatedThreshold, int calculatedThresholdAfterHideF0) {
        this.numberOfFrame = numberOfFrame;
        this.pitchValues = pitchValues;
        this.changed = changed;
        this.calculatedThreshold = calculatedThreshold;
        this.calculatedThresholdAfterHideF0 = calculatedThresholdAfterHideF0;
    }

    public int getNumberOfFrame() {
        return numberOfFrame;
    }

    public List<Integer> getPitchValues() {
        return pitchValues;
    }

    public boolean isChanged() {
        return changed;
    }

    public int getCalculatedThreshold() {
        return calculatedThreshold;
    }

    public int getCalculatedThresholdAfterHideF0() {
        return calculatedThresholdAfterHideF0;
    }
}
