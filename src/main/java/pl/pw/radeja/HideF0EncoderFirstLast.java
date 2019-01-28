package pl.pw.radeja;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import pl.pw.radeja.pitch.changers.LinearApproximateChanger;
import pl.pw.radeja.pitch.changers.PitchChanger;

import java.util.*;

public class HideF0EncoderFirstLast extends HideF0Encoder {

    public HideF0EncoderFirstLast(int logLevel, int threshold, String path) {
        super(new LinearApproximateChanger(logLevel, threshold), path);
        hiddenPositionPerFrame = 2;
    }

    HideF0EncoderFirstLast(PitchChanger pitchChanger, String path) {
        super(pitchChanger, path);
        hiddenPositionPerFrame = 2;
    }

    protected List<Pair<Integer, SpeexBits>> getPitches(List<SpeexBits> chunk, List<SpeexBits> nextChunk) {
        return getPitches(chunk);
    }
}
