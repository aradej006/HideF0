import org.jetbrains.annotations.NotNull;
import pl.pw.radeja.HideF0Encoder;
import pl.pw.radeja.PitchCollector;
import pl.pw.radeja.pitch.FirstLastLinearApproximate;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FinePithExtractor {

    public static void main(@NotNull final String[] args) throws IOException {
        List<String> paths = getSamples();
        List<Integer> thresholds = getThresholds();
        List<String> results = new ArrayList<>();
        paths.forEach(path -> thresholds.forEach(threshold -> {
            @NotNull JSpeexEnc encoder = getSpeexEncoder(path, threshold);
            try {
                encoder.encode();
                HideF0Encoder hideF0Encoder = encoder.getHideF0Encoder();
                results.add(path + '\t' + threshold + '\t' + hideF0Encoder.getNumberOfHiddenPositions());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        System.out.println("Path\tThreshold\tAllowPlaces");
        results.forEach(System.out::println);
//        final String path = "D:/PracaMgr/master-thesis/TIMIT_M/1";
//        final Integer threshold = 5;
//        @NotNull JSpeexEnc encoder = getSpeexEncoder(path, threshold);
//        System.out.println("Encoding....");
//        encoder.encode();
//        System.out.println("Encoded");
//        Collection<Integer> pitches = PitchCollector.getAllSinglePitches();
//        System.out.println(pitches.stream().map(Object::toString).collect(Collectors.joining(";")));
//        FileInputStream pitchIn = new FileInputStream(path + "-pitch.spx");
//        FileInputStream hideIn = new FileInputStream(path + "-hide.spx");
//        @NotNull JSpeexDec decoder = getSpeexDecoder(path);
//        System.out.println("Decoding....");
//        decoder.decode();
//        System.out.println("Decoded");
    }

    private static JSpeexEnc getSpeexEncoder(final String filename, final Integer threshold) {
        HideF0Encoder hideF0Encoder = new HideF0Encoder(new FirstLastLinearApproximate(threshold), filename);
        @NotNull JSpeexEnc enc = new JSpeexEnc(hideF0Encoder);
        enc.srcFile = filename + ".wav";
        enc.destFile = filename + "-pitch.spx";
        enc.srcFormat = JSpeexEnc.FILE_FORMAT_WAVE;
        enc.destFormat = JSpeexEnc.FILE_FORMAT_OGG;
        enc.printlevel = JSpeexEnc.DEBUG;
        enc.mode = 0;
        enc.sampleRate = 8000;
        enc.channels = 1;
//        enc.quality = 8;      // default 8
        return enc;
    }

    private static JSpeexDec getSpeexDecoder(final String filename) {
        @NotNull JSpeexDec dec = new JSpeexDec();
        dec.srcFile = filename + ".spx";
        dec.destFile = filename + "-encdec.wav";
        dec.srcFormat = JSpeexDec.FILE_FORMAT_OGG;
        dec.destFormat = JSpeexDec.FILE_FORMAT_WAVE;
        dec.printlevel = JSpeexDec.DEBUG;
        dec.enhanced = true;
        return dec;
    }

    private static List<String> getSamples() {
        final String baseMalePath = "D:/PracaMgr/master-thesis/TIMIT_M/";
        final String baseFemalePath = "D:/PracaMgr/master-thesis/TIMIT_F/";
        final Integer maleLimit = 25;
        final Integer femaleLimit = 25;
        List<String> samples = new ArrayList<>();
        for (int i = 1; i < maleLimit; i++) {
            samples.add(baseMalePath + i);
        }
        for (int i = 1; i < femaleLimit; i++) {
            samples.add(baseFemalePath + i);
        }
        return samples;
    }

    private static List<Integer> getThresholds() {
        return Arrays.asList(0, 1, 2, 3, 4, 5, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 128);
    }
}
