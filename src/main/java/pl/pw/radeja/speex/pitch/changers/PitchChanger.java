package pl.pw.radeja.speex.pitch.changers;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public abstract class PitchChanger {
    float threshold;

    PitchChanger(float threshold) {
        this.threshold = threshold;
    }

    public abstract List<Integer> change(List<Integer> pitches, boolean shouldLog);

    public abstract boolean shouldChange(List<Integer> pitches);

    public float calculateThreshold(List<Integer> pitches) {
        List<Float> approxValues = new ArrayList<>();
        for (int i = 0; i < pitches.size(); i++) {
            float val = (float) (pitches.get(i) * 1.);
            float approx = LinearApprox(pitches.get(0), pitches.get(pitches.size() - 1), pitches.size(), i);
            approxValues.add(Math.abs(approx - val));
        }
        return approxValues.stream().max(Float::compareTo).orElse(0f);
    }

    public static float LinearApprox(Integer first, Integer last, Integer length, Integer x) {
        return (float) Math.rint(((last * 1. - first) / (length - 1)) * x + first);
//
//        return (float) ((last * 1. - first) / (length - 1) * x + first);
    }

    static String pitchesToString(List<Integer> pitches) {
        return pitches.stream().map(Objects::toString).collect(Collectors.joining(";"));
    }

    public abstract Integer getNumberOfHiddenPositions(List<Integer> pitches);
}
