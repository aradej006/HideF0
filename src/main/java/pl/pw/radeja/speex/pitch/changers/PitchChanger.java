package pl.pw.radeja.speex.pitch.changers;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public abstract class PitchChanger {
    Integer threshold;

    PitchChanger(Integer threshold) {
        this.threshold = threshold;
    }

    public abstract List<Integer> change(List<Integer> pitches, boolean shouldLog);

    public abstract boolean shouldChange(List<Integer> pitches);

    public Integer calculateThreshold(List<Integer> pitches) {
        List<Integer> approxValues = new ArrayList<>();
        for (int i = 0; i < pitches.size(); i++) {
            Integer val = pitches.get(i);
            Integer approx = LinearApprox(pitches.get(0), pitches.get(pitches.size() - 1), pitches.size(), i);
            approxValues.add(Math.abs(approx - val));
        }
        return approxValues.stream().max(Integer::compareTo).orElse(0);
    }

    public static Integer LinearApprox(Integer first, Integer last, Integer length, Integer x) {
        return (int) Math.rint(((last * 1. - first) / (length - 1)) * x + first);
    }

    static String pitchesToString(List<Integer> pitches) {
        return pitches.stream().map(Objects::toString).collect(Collectors.joining(";"));
    }

    public abstract Integer getNumberOfHiddenPositions(List<Integer> pitches);
}
