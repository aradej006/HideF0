import org.apache.commons.lang3.time.StopWatch;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;
import pl.pw.radeja.AllowPlaces;
import pl.pw.radeja.AllowPlacesPrint;
import pl.pw.radeja.HideF0Encoder;
import pl.pw.radeja.pesq.PesqResultPrinter;
import pl.pw.radeja.pesq.PesqRunner;
import pl.pw.radeja.pesq.common.PesqFiles;
import pl.pw.radeja.pesq.common.PesqResult;
import pl.pw.radeja.pitch.changers.FirstLastLinearApproximate;
import pl.pw.radeja.pitch.collectors.PitchCollector;
import pl.pw.radeja.pitch.collectors.PitchCollectorPrint;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FinePithExtractor {

    public static void main(@NotNull final String[] args) throws InterruptedException, IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // calculate allow places
        Pair<List<AllowPlaces>, List<PitchCollector>> result = calculateAllowPlaces(getSamples(), getThresholds());
        AllowPlacesPrint.print(result.getValue0());

        // decoding
        List<String> filesToDecode = new ArrayList<>();
        getSamples().forEach(s -> getThresholds().forEach(t -> filesToDecode.add(s + "-hide-" + t)));
        runDecoding(filesToDecode);


        //pesq
        List<PesqFiles> filesToPesq = new ArrayList<>();
        getSamples().forEach(s -> getThresholds().forEach(t -> filesToPesq.add(new PesqFiles(s + "-hide-0-dec.wav", s + "-hide-" + t + "-dec.wav"))));
        List<PesqResult> pesqResults = PesqRunner.run(filesToPesq);
        PesqResultPrinter.print(pesqResults);

        //print pitchValues
        result.getValue1().forEach(pitchCollector -> {
            try {
                PrintWriter printWriter = new PrintWriter(pitchCollector.getPath() + "-pitch.txt", "UTF-8");
                PitchCollectorPrint.print(pitchCollector.getPitchValues(), printWriter, false).close();
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        });

        //print results
        AllowPlacesPrint.print(result.getValue0());
        PesqResultPrinter.print(pesqResults);
        stopWatch.stop();
        System.out.println("\n\nTotal time:" + (stopWatch.getTime() / 1000) + "[s]");
    }

    private static JSpeexEnc getSpeexEncoder(final String filename, final Integer threshold, final int logLevel) {
        HideF0Encoder hideF0Encoder = new HideF0Encoder(new FirstLastLinearApproximate(logLevel, threshold), filename);
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
        dec.destFile = filename + "-dec.wav";
        dec.srcFormat = JSpeexDec.FILE_FORMAT_OGG;
        dec.destFormat = JSpeexDec.FILE_FORMAT_WAVE;
        dec.printlevel = JSpeexDec.DEBUG;
        dec.enhanced = true;
        return dec;
    }

    private static List<String> getSamples() {
        final String baseMalePath = "D:/PracaMgr/master-thesis/TIMIT_M/";
        final String baseFemalePath = "D:/PracaMgr/master-thesis/TIMIT_F/";
//        final int maleLimit = 2;
        final int maleLimit = 25;
//        final int femaleLimit = 1;
        final int femaleLimit = 25;
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
        return Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20, 25, 30, 35, 40, 45, 50, 60, 64);
//        return Arrays.asList(0);
    }

    private static Pair<List<AllowPlaces>, List<PitchCollector>> calculateAllowPlaces(List<String> paths, List<Integer> thresholds) throws InterruptedException {
        List<AllowPlaces> allowPlaces = Collections.synchronizedList(new ArrayList<>());
        List<PitchCollector> pitchCollectors = Collections.synchronizedList(new ArrayList<>());

        for (Integer threshold : thresholds) {
            ExecutorService es = Executors.newCachedThreadPool();
            paths.forEach(path ->
                    es.execute(() -> {
                        @NotNull JSpeexEnc encoder = getSpeexEncoder(path, threshold, 0);
                        try {
                            encoder.encode();
                            HideF0Encoder hideF0Encoder = encoder.getHideF0Encoder();
                            synchronized (allowPlaces) {
                                System.out.println("Encoded:\t" + path + "\t" + threshold + "\t" + hideF0Encoder.getNumberOfHiddenPositions());
                                allowPlaces.add(new AllowPlaces(path, threshold, hideF0Encoder.getNumberOfHiddenPositions()));
                            }
                            synchronized (pitchCollectors) {
                                pitchCollectors.add(hideF0Encoder.getPitchCollector());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    })
            );
            es.shutdown();
            boolean finished = es.awaitTermination(24, TimeUnit.HOURS);
            if (!finished) {
                throw new Error("Some Error");
            }
        }
        return new Pair<>(allowPlaces, pitchCollectors);
    }

    private static void runDecoding(List<String> paths) throws IOException {
        System.out.println("Decoding....");
        for (String p : paths) {
            @NotNull JSpeexDec decoder = getSpeexDecoder(p);
            decoder.decode();
        }
        System.out.println("Decoded");
    }

}
