package pl.pw.radeja.speex.pitch.changers;

import java.util.List;

public class NonPitchChanger extends PitchChanger {

    public NonPitchChanger(float threshold) {
        super(threshold);
    }

    @Override
    public List<Integer> change(List<Integer> pitches, boolean shouldLog) {
        return pitches;
    }

    @Override
    public boolean shouldChange(List<Integer> pitches) {
        return false;
    }

    @Override
    public Integer getNumberOfHiddenPositions(List<Integer> pitches) {
        return 0;
    }
}
