package pl.pw.radeja.speex.encoders;

import org.javatuples.Pair;
import pl.pw.radeja.Config;
import pl.pw.radeja.speex.pitch.changers.LinearApproximateChanger;
import pl.pw.radeja.speex.pitch.changers.LinearApproximateMiddleChanger;
import pl.pw.radeja.speex.pitch.changers.PitchChanger;
import pl.pw.radeja.speex.result.SpeexBits;

import java.util.ArrayList;
import java.util.List;

public class HideF0EncoderFirstMiddle extends HideF0Encoder {

    public HideF0EncoderFirstMiddle(int threshold, String path) {
        super(new LinearApproximateMiddleChanger(threshold), path, Config.HideF0Type.FIRST_MIDDLE);
    }

    HideF0EncoderFirstMiddle(PitchChanger pitchChanger, String path) {
        super(pitchChanger, path, Config.HideF0Type.FIRST_MIDDLE);
    }

    protected List<Pair<Integer, SpeexBits>> getPitches(List<SpeexBits> chunk, List<SpeexBits> nextChunk) {
        List<Pair<Integer, SpeexBits>> pitch = getPitches(chunk);
        pitch.addAll(getPitches(nextChunk));
        return new ArrayList<>(pitch.subList(0, 5));
    }
}
