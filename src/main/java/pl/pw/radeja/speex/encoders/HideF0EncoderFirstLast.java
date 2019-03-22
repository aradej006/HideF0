package pl.pw.radeja.speex.encoders;

import org.javatuples.Pair;
import pl.pw.radeja.Config;
import pl.pw.radeja.speex.result.SpeexBits;
import pl.pw.radeja.speex.pitch.changers.LinearApproximateChanger;
import pl.pw.radeja.speex.pitch.changers.PitchChanger;

import java.util.*;

public class HideF0EncoderFirstLast extends HideF0Encoder {

    public HideF0EncoderFirstLast(int threshold, String path) {
        super(new LinearApproximateChanger(threshold), path, Config.HideF0Type.FIRST_LAST);
    }

    HideF0EncoderFirstLast(PitchChanger pitchChanger, String path) {
        super(pitchChanger, path, Config.HideF0Type.FIRST_LAST);
    }

    protected List<Pair<Integer, SpeexBits>> getPitches(List<SpeexBits> chunk, List<SpeexBits> nextChunk) {
        return getPitches(chunk);
    }
}
