package pl.pw.radeja.speex.encoders;

import org.javatuples.Pair;
import pl.pw.radeja.Config;
import pl.pw.radeja.speex.pitch.changers.NonPitchChanger;
import pl.pw.radeja.speex.pitch.changers.PitchChanger;
import pl.pw.radeja.speex.result.SpeexBits;

import java.util.ArrayList;
import java.util.List;

public class NonHideF0Encoder extends HideF0Encoder {

    public NonHideF0Encoder(float threshold, String path) {
        super(new NonPitchChanger(threshold), path, Config.HideF0Type.NON);
    }

    NonHideF0Encoder(PitchChanger pitchChanger, String path) {
        super(pitchChanger, path, Config.HideF0Type.NON);
    }

    protected List<Pair<Integer, SpeexBits>> getPitches(List<SpeexBits> chunk, List<SpeexBits> nextChunk) {
        List<Pair<Integer, SpeexBits>> pitch = getPitches(chunk);
        pitch.addAll(getPitches(nextChunk));
        return new ArrayList<>(pitch.subList(0, 5));
    }

}
