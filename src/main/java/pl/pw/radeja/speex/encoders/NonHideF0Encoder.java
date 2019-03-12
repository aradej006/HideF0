package pl.pw.radeja.speex.encoders;

import org.javatuples.Pair;
import pl.pw.radeja.speex.pitch.changers.NonPitchChanger;
import pl.pw.radeja.speex.pitch.changers.PitchChanger;
import pl.pw.radeja.speex.result.SpeexBits;

import java.util.ArrayList;
import java.util.List;

public class NonHideF0Encoder extends HideF0Encoder {

    public NonHideF0Encoder(int threshold, String path) {
        super(new NonPitchChanger(threshold), path);
        hiddenPositionPerFrame = 3;
    }

    NonHideF0Encoder(PitchChanger pitchChanger, String path) {
        super(pitchChanger, path);
        hiddenPositionPerFrame = 3;
    }

    protected List<Pair<Integer, SpeexBits>> getPitches(List<SpeexBits> chunk, List<SpeexBits> nextChunk) {
        List<Pair<Integer, SpeexBits>> pitch = getPitches(chunk);
        pitch.addAll(getPitches(nextChunk));
        return new ArrayList<>(pitch.subList(0, 5));
    }

}
