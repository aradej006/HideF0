package pl.pw.radeja.speex.pitch.changers;

import java.util.List;

public class NonPitchChanger extends PitchChanger {

    public NonPitchChanger(Integer threshold) {
        super(threshold);
    }

    @Override
    public List<Integer> change(List<Integer> pitches) {
        return pitches;
    }

    @Override
    public boolean shouldChange(List<Integer> pitches) {
        return false;
    }
}
