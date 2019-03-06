package pl.pw.radeja.speex.pitch.changers;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LinearApproximateChanger extends PitchChanger {

    public LinearApproximateChanger(Integer threshold) {
        super(threshold);
    }

    @Override
    public List<Integer> change(List<Integer> pitches) {
        if (pitches == null) {
            throw new IllegalArgumentException("Pitches cannot be null.");
        } else if (pitches.size() < 3) {
            throw new IllegalArgumentException("Pitches size must be at least 3");
        }
        List<Integer> result = new ArrayList<>();
        Integer first = pitches.get(0);
        Integer last = pitches.get(pitches.size() - 1);
        for (int i = 0; i < 4; i++) {
            result.add(LinearApprox(first, last, pitches.size(), i));
        }
        log.debug("Pitches changed form {} to {}", pitchesToString(pitches), pitchesToString(result));
        return result;
    }

    public boolean shouldChange(List<Integer> pitches) {
        return shouldChange(pitches, threshold);
    }

    boolean shouldChange(List<Integer> pitches, Integer threshold) {
        Integer first = pitches.get(0);
        Integer last = pitches.get(pitches.size() - 1);
        for (int i = 0; i < pitches.size(); i++) {
            Integer val = pitches.get(i);
            Integer approx = LinearApprox(first, last, pitches.size(), i);
            if (Math.abs(approx - val) > threshold) {
                return false;
            }
        }

        return true;
    }

    boolean isLinear(List<Integer> pitches) {
        return shouldChange(pitches, 0);
    }
}
