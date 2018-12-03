package pl.pw.radeja.pitch;

import java.util.List;

public abstract class PitchChanger {

    protected Integer threshold;

    PitchChanger(Integer threshold) {
        this.threshold = threshold;
    }

    public abstract List<Integer> change(List<Integer> pitches);

    public abstract boolean shouldChange(List<Integer> pitches);

    public Integer getThreshold() {
        return threshold;
    }
}
