package pl.pw.radeja.pitch.collectors;

import java.util.List;

public class PitchValue {
    private int numberOfFrame;
    private List<Integer> pitchValues;
    private boolean changed;

    public PitchValue(int numberOfFrame, List<Integer> pitchValues, boolean changed) {
        this.numberOfFrame = numberOfFrame;
        this.pitchValues = pitchValues;
        this.changed = changed;
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
}
