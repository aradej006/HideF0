package pl.pw.radeja;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import pl.pw.radeja.pitch.changers.LinearApproximateChanger;
import pl.pw.radeja.pitch.changers.PitchChanger;

import java.util.*;

public class HideF0EncoderFirstFirst extends HideF0Encoder {

    public HideF0EncoderFirstFirst(int logLevel, int threshold, String path) {
        super(new LinearApproximateChanger(logLevel, threshold), path);
        hiddenPositionPerFrame = 3;
    }

    HideF0EncoderFirstFirst(PitchChanger pitchChanger, String path) {
        super(pitchChanger, path);
        hiddenPositionPerFrame = 3;
    }

    protected List<Pair<Integer, Triplet<NamesOfBits, Integer, Integer>>> getPitches(List<Triplet<NamesOfBits, Integer, Integer>> chunk, List<Triplet<NamesOfBits, Integer, Integer>> nextChunk) {
        List<Pair<Integer, Triplet<NamesOfBits, Integer, Integer>>> pitch = getPitches(chunk);
        pitch.addAll(getPitches(nextChunk));
        return pitch.subList(0, 5);
    }
}

