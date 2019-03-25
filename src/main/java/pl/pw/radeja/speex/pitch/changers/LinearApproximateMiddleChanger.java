package pl.pw.radeja.speex.pitch.changers;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LinearApproximateMiddleChanger extends LinearApproximateChanger {

    public LinearApproximateMiddleChanger(Integer threshold) {
        super(threshold);
    }

    @Override
    public List<Integer> change(List<Integer> pitches, boolean shouldLog) {
        if (pitches == null) {
            throw new IllegalArgumentException("Pitches cannot be null.");
        } else if (pitches.size() < 3) {
            throw new IllegalArgumentException("Pitches size must be at least 3");
        }
        Integer first = pitches.get(0);
        Integer middle = pitches.get(2);
        Integer last = pitches.get(4);

        List<Integer> result = new ArrayList<>();
        result.add(pitches.get(0));
        if (shouldChangeSecond(pitches, threshold)) {
            result.add(LinearApprox(first, middle, 3, 1));
        } else {
            result.add(pitches.get(1));
        }
        result.add(pitches.get(2));
        if (shouldChangeFourth(pitches, threshold)) {
            result.add(LinearApprox(middle, last, 3, 1));
        } else {
            result.add(pitches.get(3));
        }
        result.add(pitches.get(4));

        if (shouldLog) {
            log.debug("Pitches changed form {} to {}", pitchesToString(pitches), pitchesToString(result));
        }
        return result;
    }

    boolean shouldChange(List<Integer> pitches, Integer threshold) {
        boolean firstTh = shouldChangeSecond(pitches, threshold);
        boolean secondTh = shouldChangeFourth(pitches, threshold);
        return firstTh || secondTh;
    }

    protected boolean shouldChangeSecond(List<Integer> pitches, Integer threshold) {
        Integer first = pitches.get(0);
        Integer middle = pitches.get(2);
        return !(Math.abs(LinearApprox(first, middle, 3, 1) - pitches.get(1)) > threshold);
    }

    protected boolean shouldChangeFourth(List<Integer> pitches, Integer threshold) {
        Integer middle = pitches.get(2);
        Integer last = pitches.get(4);
        return !(Math.abs(LinearApprox(middle, last, 3, 1) - pitches.get(3)) > threshold);
    }

    @Override
    public Integer getNumberOfHiddenPositions(List<Integer> pitches) {
        if (shouldChangeSecond(pitches, threshold) && shouldChangeFourth(pitches, threshold)) {
            return 2;
        } else if (shouldChangeSecond(pitches, threshold) || shouldChangeFourth(pitches, threshold)) {
            return 1;
        } else {
            return 0;
        }
    }
}
