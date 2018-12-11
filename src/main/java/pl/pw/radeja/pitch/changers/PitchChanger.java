package pl.pw.radeja.pitch.changers;

import java.util.List;

public abstract class PitchChanger {

    protected int logLevel;
    protected Integer threshold;

    PitchChanger(int logLevel, Integer threshold) {
        this.logLevel = logLevel;
        this.threshold = threshold;
    }

    public abstract List<Integer> change(List<Integer> pitches);

    public abstract boolean shouldChange(List<Integer> pitches);

    public abstract boolean isLinear(List<Integer> pitches);

    public Integer getThreshold() {
        return threshold;
    }

    public int getLogLevel() {
        return logLevel;
    }
}
