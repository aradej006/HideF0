package pl.pw.radeja.speex.pitch.changers;

import lombok.Getter;

import java.util.List;

@Getter
public abstract class PitchChanger {
    Integer threshold;

    PitchChanger(Integer threshold) {
        this.threshold = threshold;
    }

    public abstract List<Integer> change(List<Integer> pitches);

    public abstract boolean shouldChange(List<Integer> pitches);

    public abstract Integer calculateThreshold(List<Integer> pitches);

    public abstract boolean isLinear(List<Integer> pitches);
}
