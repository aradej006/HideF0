package pl.pw.radeja.pitch;

import java.util.List;

public class NonPitchChanger extends PitchChanger {

    public NonPitchChanger() {
        super(0);
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
