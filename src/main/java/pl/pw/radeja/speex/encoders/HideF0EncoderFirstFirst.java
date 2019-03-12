package pl.pw.radeja.speex.encoders;

import org.javatuples.Pair;
import pl.pw.radeja.speex.result.SpeexBits;
import pl.pw.radeja.speex.pitch.changers.LinearApproximateChanger;
import pl.pw.radeja.speex.pitch.changers.PitchChanger;

import java.util.*;

public class HideF0EncoderFirstFirst extends HideF0Encoder {

    public HideF0EncoderFirstFirst(int threshold, String path) {
        super(new LinearApproximateChanger(threshold), path);
        hiddenPositionPerFrame = 3;
    }

    HideF0EncoderFirstFirst(PitchChanger pitchChanger, String path) {
        super(pitchChanger, path);
        hiddenPositionPerFrame = 3;
    }

    protected List<Pair<Integer, SpeexBits>> getPitches(List<SpeexBits> chunk, List<SpeexBits> nextChunk) {
        List<Pair<Integer, SpeexBits>> pitch = getPitches(chunk);
        pitch.addAll(getPitches(nextChunk));
        return new ArrayList<>(pitch.subList(0, 5));
    }
}

