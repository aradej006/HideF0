package pl.pw.radeja;

import org.javatuples.Triplet;
import org.jetbrains.annotations.NotNull;
import org.xiph.speex.AudioFileWriter;
import org.xiph.speex.Bits;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public final class HideF0Encoder {
    private static final int logLevel = 1;
    private static final String PATH = "D:/PracaMgr/master-thesis/TIMIT_M/1";
    private static final Random RAND = new Random();
    private static Integer numberOfHiddenPositions = 0;

    public static void hide(BitsCollector bitsCollector) throws IOException {
        numberOfHiddenPositions = 0;
        AudioFileWriter writer = HideF0SpeexConfig.getAudioFileWriter(new File(PATH + "-hide.spx"));
        Map<Integer, List<Triplet<NamesOfBits, Integer, Integer>>> chunks = getChunks(bitsCollector);
        for (int i = 1; i <= chunks.size(); i++) {
            saveChunk(writer, chunks.get(i));
        }
        if (logLevel >= 1) {
            System.out.println("Number of hidden positions: " + numberOfHiddenPositions);
        }
        writer.close();
        numberOfHiddenPositions = 0;
    }

    private static Map<Integer, List<Triplet<NamesOfBits, Integer, Integer>>> getChunks(BitsCollector bitsCollector) {
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

    private static void saveChunk(AudioFileWriter writer, List<Triplet<NamesOfBits, Integer, Integer>> bitsToSave) throws IOException {
        @NotNull byte[] temp = new byte[2560];
        List<Triplet<NamesOfBits, Integer, Integer>> changed = changePitchValues(bitsToSave);
        Bits bits = new Bits();
        bits.init();
        changed.forEach(b -> bits.pack(b.getValue1(), b.getValue2()));
        int size = bits.getBufferSize();
        System.arraycopy(bits.getBuffer(), 0, temp, 0, size);
        if (size > 0) {
            writer.writePacket(temp, 0, size);
        }
    }

    private static List<Triplet<NamesOfBits, Integer, Integer>> changePitchValues(List<Triplet<NamesOfBits, Integer, Integer>> bits) {
        Map<Integer, Triplet<NamesOfBits, Integer, Integer>> pitch = new HashMap<>();
        for (int i = 0; i < bits.size(); i++) {
            if (bits.get(i).getValue0().equals(NamesOfBits.PITCH)) {
                pitch.put(i, bits.get(i));
            }
        }
        List<Integer> pitchValues = pitch.entrySet().stream().map(e -> e.getValue().getValue1()).collect(Collectors.toList());
        //todo: change pitch with thr
        int threshold = 10, maxVariance = 5;
        if (Collections.max(pitchValues) - Collections.min(pitchValues) < threshold) {
            printPitchValue("A", bits);
            numberOfHiddenPositions += 3;
            pitch.forEach((key, value) -> bits.set(key, value.setAt1(value.getValue1() + (RAND.nextInt((1 + 2 * maxVariance)) - maxVariance))));
            printPitchValue("B", bits);
        }
        return bits;
    }

    private static void printPitchValue(String prefix, List<Triplet<NamesOfBits, Integer, Integer>> bits) {
        if (logLevel >= 1) {
            System.out.println(prefix + ": " +
                    bits.stream().filter(b -> b.getValue0().equals(NamesOfBits.PITCH))
                            .map(Triplet::getValue1)
                            .map(Objects::toString)
                            .collect(Collectors.joining(";"))

            );
        }

    }
}
