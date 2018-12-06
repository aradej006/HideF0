package pl.pw.radeja.pitch.collectors;

import java.util.List;

public class PitchValue {
    private int numberOfFrame;
    private List<Integer> pitchValues;

    public PitchValue(int numberOfFrame, List<Integer> pitchValues) {
        this.numberOfFrame = numberOfFrame;
        this.pitchValues = pitchValues;
    }

    public int getNumberOfFrame() {
        return numberOfFrame;
    }

    public List<Integer> getPitchValues() {
        return pitchValues;
    }
}
