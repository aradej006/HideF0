package pl.pw.radeja.speex.pitch.changers;

import java.util.List;
import java.util.Random;

public class LinearApproximateRand extends LinearApproximateChanger {
    protected Random r = new Random();

    public LinearApproximateRand(int logLevel, Integer threshold, Long seed) {
        super(logLevel, threshold);
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
}
