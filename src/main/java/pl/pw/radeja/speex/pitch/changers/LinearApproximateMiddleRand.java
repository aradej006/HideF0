package pl.pw.radeja.speex.pitch.changers;

import java.util.List;
import java.util.Random;

public class LinearApproximateMiddleRand extends LinearApproximateMiddleChanger {
    protected Random r = new Random();

    public LinearApproximateMiddleRand(float threshold, Long seed) {
        super(threshold);
        if (seed != null) {
            r = new Random(seed);
        }
    }

    @Override
    public boolean shouldChange(List<Integer> pitches) {
        boolean canBeChanged = shouldChange(pitches, threshold);
        if (canBeChanged && !isLinear(pitches)) {
            return r.nextBoolean();
        } else {
            return canBeChanged;
        }
    }

    boolean isLinear(List<Integer> pitches) {
        return shouldChangeSecond(pitches, 0) || shouldChangeFourth(pitches, 0);
    }
}
