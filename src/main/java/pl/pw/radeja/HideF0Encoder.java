package pl.pw.radeja;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.jetbrains.annotations.NotNull;
import org.xiph.speex.AudioFileWriter;
import org.xiph.speex.Bits;
import pl.pw.radeja.pitch.changers.PitchChanger;
import pl.pw.radeja.pitch.collectors.PitchCollector;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class HideF0Encoder {
    protected int logLevel;
    protected String path;
    protected Integer numberOfHiddenPositions = 0;
    protected Integer hiddenPositionPerFrame = 0;
    protected PitchChanger pitchChanger;
    protected PitchCollector pitchCollector;

    public HideF0Encoder(PitchChanger pitchChanger, String path) {
        this.pitchChanger = pitchChanger;
        this.logLevel = pitchChanger.getLogLevel();
        this.path = path;
        this.pitchCollector = new PitchCollector(this.path, pitchChanger.getThreshold());
    }

    public void hide(BitsCollector bitsCollector) throws IOException {
        AudioFileWriter writer = HideF0SpeexConfig.getAudioFileWriter(new File(path + "-hide-" + pitchChanger.getThreshold() + ".spx"));
        Map<Integer, List<Triplet<NamesOfBits, Integer, Integer>>> chunks = getChunks(bitsCollector);
        for (int i = 2; i <= chunks.size(); i++) {
            List<Triplet<NamesOfBits, Integer, Integer>> chunk = chunks.get(i - 1);
            List<Triplet<NamesOfBits, Integer, Integer>> nextChunk = chunks.get(i);
            saveChunk(writer, chunk, nextChunk);
        }
        if (logLevel >= 1) {
            System.out.println("Number of hidden positions: " + numberOfHiddenPositions);
        }
        writer.close();
    }

    protected Map<Integer, List<Triplet<NamesOfBits, Integer, Integer>>> getChunks(BitsCollector bitsCollector) {
        Map<Integer, List<Triplet<NamesOfBits, Integer, Integer>>> chunks = new HashMap<>();
        List<Triplet<NamesOfBits, Integer, Integer>> chunk = new ArrayList<>();
        for (int i = 0; i < bitsCollector.getBitsToSave().size(); i++) {
            if (!bitsCollector.getBitsToSave().get(i).getValue0().equals(NamesOfBits.SIZE)) {
                chunk.add(bitsCollector.getBitsToSave().get(i));
            } else {
                chunks.put(bitsCollector.getBitsToSave().get(i).getValue2(), chunk);
                chunk = new ArrayList<>();
            }
        }
        return chunks;
    }

    protected void saveChunk(AudioFileWriter writer, List<Triplet<NamesOfBits, Integer, Integer>> chunk, List<Triplet<NamesOfBits, Integer, Integer>> nextChunk) throws IOException {
        @NotNull byte[] temp = new byte[2560];
        List<Triplet<NamesOfBits, Integer, Integer>> changed = changePitchValues(chunk, nextChunk);
        Bits bits = new Bits();
        bits.init();
        changed.forEach(b -> bits.pack(b.getValue1(), b.getValue2()));
        int size = bits.getBufferSize();
        System.arraycopy(bits.getBuffer(), 0, temp, 0, size);
        if (size > 0) {
            writer.writePacket(temp, 0, size);
        }
    }

    protected List<Pair<Integer, Triplet<NamesOfBits, Integer, Integer>>> getPitches(List<Triplet<NamesOfBits, Integer, Integer>> chunk) {
        List<Pair<Integer, Triplet<NamesOfBits, Integer, Integer>>> pitch = new ArrayList<>();
        for (int i = 0; i < chunk.size(); i++) {
            if (chunk.get(i).getValue0().equals(NamesOfBits.PITCH)) {
                pitch.add(new Pair<>(i, chunk.get(i)));
            }
        }
        return pitch;
    }

    protected abstract List<Pair<Integer, Triplet<NamesOfBits, Integer, Integer>>> getPitches(List<Triplet<NamesOfBits, Integer, Integer>> chunk, List<Triplet<NamesOfBits, Integer, Integer>> nextChunk);

    protected List<Triplet<NamesOfBits, Integer, Integer>> changePitchValues(List<Triplet<NamesOfBits, Integer, Integer>> chunk, List<Triplet<NamesOfBits, Integer, Integer>> nextChunk) {
        List<Pair<Integer, Triplet<NamesOfBits, Integer, Integer>>> pitch = getPitches(chunk, nextChunk);
        // change: PitchChanger
        List<Integer> pitchValues = pitch.stream().map(e -> e.getValue1().getValue1()).collect(Collectors.toList());
        List<Integer> newPitches = pitchChanger.change(pitchValues);
        boolean changed = false;
        int calculatedThreshold = pitchChanger.calculateThreshold(pitchValues);
        int calculatedThresholdAfterHideF0 = pitchChanger.calculateThreshold(newPitches);
        if (pitchChanger.shouldChange(pitchValues)) {
            changed = !pitchChanger.isLinear(pitchValues);
            numberOfHiddenPositions += hiddenPositionPerFrame;
            for (int i = 0; i < 4; i++) {
                Pair<Integer, Triplet<NamesOfBits, Integer, Integer>> pair = pitch.get(i);
                chunk.set(pair.getValue0(), pair.getValue1().setAt1(newPitches.get(i)));
            }
        }
        pitchCollector.addPitch(chunk.stream().filter(b -> b.getValue0().equals(NamesOfBits.PITCH)).map(Triplet::getValue1).collect(Collectors.toList()), changed, calculatedThreshold, calculatedThresholdAfterHideF0);
        return chunk;
    }

    protected void printPitchValue(String prefix, List<Triplet<NamesOfBits, Integer, Integer>> bits) {
        if (logLevel >= 1) {
            System.out.println(prefix + ": " +
                    bits.stream().filter(b -> b.getValue0().equals(NamesOfBits.PITCH))
                            .map(Triplet::getValue1)
                            .map(Objects::toString)
                            .collect(Collectors.joining(";"))

            );
        }
    }

    public Integer getNumberOfHiddenPositions() {
        return numberOfHiddenPositions;
    }

    public PitchCollector getPitchCollector() {
        return pitchCollector;
    }

    public String getPath() {
        return path;
    }
}
