package pl.pw.radeja.speex.encoders;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.jetbrains.annotations.NotNull;
import org.xiph.speex.AudioFileWriter;
import org.xiph.speex.Bits;
import pl.pw.radeja.speex.result.BitsCollector;
import pl.pw.radeja.speex.encoders.config.HideF0SpeexConfig;
import pl.pw.radeja.speex.result.SpeexBitsName;
import pl.pw.radeja.speex.result.SpeexBits;
import pl.pw.radeja.speex.pitch.changers.PitchChanger;
import pl.pw.radeja.speex.pitch.collectors.PitchCollector;

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
        Map<Integer, List<SpeexBits>> chunks = getChunks(bitsCollector);
        for (int i = 2; i <= chunks.size(); i++) {
            List<SpeexBits> chunk = chunks.get(i - 1);
            List<SpeexBits> nextChunk = chunks.get(i);
            saveChunk(writer, chunk, nextChunk);
        }
        if (logLevel >= 1) {
            System.out.println("Number of hidden positions: " + numberOfHiddenPositions);
        }
        writer.close();
    }

    protected Map<Integer, List<SpeexBits>> getChunks(BitsCollector bitsCollector) {
        Map<Integer, List<SpeexBits>> chunks = new HashMap<>();
        List<SpeexBits> chunk = new ArrayList<>();
        for (int i = 0; i < bitsCollector.getBitsToSave().size(); i++) {
            if (!bitsCollector.getBitsToSave().get(i).getSpeexBitsName().equals(SpeexBitsName.SIZE)) {
                chunk.add(bitsCollector.getBitsToSave().get(i));
            } else {
                chunks.put(bitsCollector.getBitsToSave().get(i).getNumberOfBits(), chunk);
                chunk = new ArrayList<>();
            }
        }
        return chunks;
    }

    protected void saveChunk(AudioFileWriter writer, List<SpeexBits> chunk, List<SpeexBits> nextChunk) throws IOException {
        @NotNull byte[] temp = new byte[2560];
        List<SpeexBits> changed = changePitchValues(chunk, nextChunk);
        Bits bits = new Bits();
        bits.init();
        changed.forEach(b -> bits.pack(b.getBitsData(), b.getNumberOfBits()));
        int size = bits.getBufferSize();
        System.arraycopy(bits.getBuffer(), 0, temp, 0, size);
        if (size > 0) {
            writer.writePacket(temp, 0, size);
        }
    }

    protected List<Pair<Integer, SpeexBits>> getPitches(List<SpeexBits> chunk) {
        List<Pair<Integer, SpeexBits>> pitch = new ArrayList<>();
        for (int i = 0; i < chunk.size(); i++) {
            if (chunk.get(i).getSpeexBitsName().equals(SpeexBitsName.PITCH)) {
                pitch.add(new Pair<>(i, chunk.get(i)));
            }
        }
        return pitch;
    }

    protected abstract List<Pair<Integer, SpeexBits>> getPitches(List<SpeexBits> chunk, List<SpeexBits> nextChunk);

    protected List<SpeexBits> changePitchValues(List<SpeexBits> chunk, List<SpeexBits> nextChunk) {
        List<Pair<Integer, SpeexBits>> pitch = getPitches(chunk, nextChunk);
        // change: PitchChanger
        List<Integer> pitchValues = pitch.stream().map(e -> e.getValue1().getBitsData()).collect(Collectors.toList());
        List<Integer> newPitches = pitchChanger.change(pitchValues);
        boolean changed = false;
        int calculatedThreshold = pitchChanger.calculateThreshold(pitchValues);
        int calculatedThresholdAfterHideF0 = pitchChanger.calculateThreshold(newPitches);
        if (pitchChanger.shouldChange(pitchValues)) {
            changed = true;
            numberOfHiddenPositions += hiddenPositionPerFrame;
            for (int i = 0; i < 4; i++) {
                Pair<Integer, SpeexBits> pair = pitch.get(i);
                chunk.set(pair.getValue0(), pair.getValue1().setBitsData(newPitches.get(i)));
            }
        }
        pitchCollector.addPitch(chunk.stream().filter(b -> b.getSpeexBitsName().equals(SpeexBitsName.PITCH)).map(SpeexBits::getBitsData).collect(Collectors.toList()), changed, calculatedThreshold, calculatedThresholdAfterHideF0);
        return chunk;
    }

    protected void printPitchValue(String prefix, List<Triplet<SpeexBitsName, Integer, Integer>> bits) {
        if (logLevel >= 1) {
            System.out.println(prefix + ": " +
                    bits.stream().filter(b -> b.getValue0().equals(SpeexBitsName.PITCH))
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
