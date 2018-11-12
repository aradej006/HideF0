import org.jetbrains.annotations.NotNull;
import pl.pw.radeja.PitchCollector;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

public class FinePithExtractor {

    public static void main(@NotNull final String[] args) throws IOException {
        final String path = "D:/PracaMgr/master-thesis/TIMIT_M/1";
        @NotNull JSpeexEnc encoder = getSpeexEncoder(path);
        System.out.println("Encoding....");
        encoder.encode();
        System.out.println("Encoded");
        Collection<Integer> pitches = PitchCollector.getAllSinglePitches();
        System.out.println(pitches.stream().map(Object::toString).collect(Collectors.joining(";")));
//        FileInputStream pitchIn = new FileInputStream(path + "-pitch.spx");
//        FileInputStream hideIn = new FileInputStream(path + "-hide.spx");
//        @NotNull JSpeexDec decoder = getSpeexDecoder(path);
//        System.out.println("Decoding....");
//        decoder.decode();
//        System.out.println("Decoded");
    }

    private static JSpeexEnc getSpeexEncoder(final String filename) {
        @NotNull JSpeexEnc enc = new JSpeexEnc();
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
}
