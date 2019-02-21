package pl.pw.radeja;

import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.NotNull;
import pl.pw.radeja.pesq.PesqResultPrinter;
import pl.pw.radeja.pesq.PesqRunner;
import pl.pw.radeja.pesq.common.PesqFiles;
import pl.pw.radeja.pesq.common.PesqResult;
import pl.pw.radeja.speex.encoders.HideF0Encoder;
import pl.pw.radeja.speex.encoders.HideF0EncoderFirstFirst;
import pl.pw.radeja.speex.encoders.HideF0EncoderFirstLast;
import pl.pw.radeja.speex.encoders.HideF0EncoderFirstLastRand;
import pl.pw.radeja.speex.pitch.collectors.CalculatedThresholdPrinter;
import pl.pw.radeja.speex.pitch.collectors.PitchCollectorPrint;
import pl.pw.radeja.speex.result.AllowPlaces;
import pl.pw.radeja.speex.result.AllowPlacesPrint;
import pl.pw.radeja.speex.result.BitsCollector;
import pl.pw.radeja.speex.result.SampleResult;
import pl.pw.radeja.statistic.BytesHistogramPrinter;
import pl.pw.radeja.weka.printers.WekaPrinter;
import pl.pw.radeja.weka.printers.WekaVectorPrinter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class FinePithExtractor {

    public static void main(@NotNull final String[] args) throws InterruptedException, IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // calculate allow places
        List<SampleResult> result = new ArrayList<>();
        if (Config.CALCULATE_ALLOW_PLACES) {
            result = calculateAllowPlaces();
            AllowPlacesPrint.print(result.stream().map(SampleResult::getAllowPlaces).collect(Collectors.toList()));
        }

        // decoding
        if (Config.DECODE_FILES) {
            List<String> filesToDecode = new ArrayList<>();
            Config.getSamples().forEach(s -> Config.THRESHOLDS.forEach(t -> filesToDecode.add(s + "-hide-" + t)));
            runDecoding(filesToDecode);
        }


        //pesq
        List<PesqResult> pesqResults = new ArrayList<>();
        if (Config.CALCUALTE_PESQ) {
            List<PesqFiles> filesToPesq = new ArrayList<>();
            Config.getSamples().forEach(s -> Config.THRESHOLDS.forEach(t -> filesToPesq.add(new PesqFiles(s + ".wav", s + "-hide-" + t + "-dec.wav"))));
            pesqResults = PesqRunner.run(filesToPesq);
            PesqResultPrinter.print(pesqResults);
        }

        //print pitchValues
        if (Config.CALCULATE_ALLOW_PLACES) {
            result.stream().map(SampleResult::getPitchCollector).collect(Collectors.toList()).forEach(pitchCollector -> {
                try {
                    PrintWriter printWriter = new PrintWriter(pitchCollector.getPath() + "-pitch-" + pitchCollector.getThreshold() + ".txt", "UTF-8");
                    PitchCollectorPrint.print(pitchCollector.getFramePitchValues(), printWriter, false).close();
                } catch (FileNotFoundException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            });
        }
        if (Config.PRINT_WEKA_FILES) {
            WekaPrinter.print(result.stream().map(SampleResult::getPitchCollector).collect(Collectors.toList()), 1);
        }
        if (Config.PRINT_WEKA_VECTOR_FILES) {
            WekaVectorPrinter.print(result.stream().map(SampleResult::getPitchCollector).collect(Collectors.toList()), 10);
        }

        //print results
        if (Config.CALCULATE_ALLOW_PLACES) {
            AllowPlacesPrint.print(result.stream().map(SampleResult::getAllowPlaces).collect(Collectors.toList()));
        }
        if (Config.CALCUALTE_PESQ) {
            PesqResultPrinter.print(pesqResults);
        }
        if (Config.PRINT_CALCULATED_THRESHOLDS) {
            CalculatedThresholdPrinter.print(result.stream().map(SampleResult::getPitchCollector).collect(Collectors.toList()));
        }
        if (Config.PRINT_HISTOGRAM) {
            BytesHistogramPrinter.printHistograms(result.stream().map(SampleResult::getBitsCollector).collect(Collectors.toList()));
        }

        stopWatch.stop();
        System.out.println("\n\nTotal time:" + (stopWatch.getTime() / 1000) + "[s]");
    }

    private static JSpeexEnc getSpeexEncoder(final HideF0Encoder hideF0Encoder) {
        @NotNull JSpeexEnc enc = new JSpeexEnc(hideF0Encoder);
        enc.srcFile = hideF0Encoder.getPath() + ".wav";
        enc.destFile = hideF0Encoder.getPath() + "-pitch.spx";
        enc.srcFormat = JSpeexEnc.FILE_FORMAT_WAVE;
        enc.destFormat = JSpeexEnc.FILE_FORMAT_OGG;
        enc.printlevel = JSpeexEnc.INFO;
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
        dec.printlevel = JSpeexDec.INFO;
        dec.enhanced = true;
        return dec;
    }

    private static List<SampleResult> calculateAllowPlaces() throws InterruptedException {
        List<SampleResult> result = Collections.synchronizedList(new ArrayList<>());
        ExecutorService es = Executors.newFixedThreadPool(Config.NUMBER_OF_THREADS);
        Config.THRESHOLDS.forEach(threshold -> Config.getSamples().forEach(path -> es.execute(() -> {
            JSpeexEnc encoder;
            if (Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FirstLast)) {
                encoder = getSpeexEncoder(new HideF0EncoderFirstLast(Config.LOG_LEVEL, threshold, path));
            } else if (Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FirstFirst)) {
                encoder = getSpeexEncoder(new HideF0EncoderFirstFirst(Config.LOG_LEVEL, threshold, path));
            } else if (Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FirstLastRand)) {
                encoder = getSpeexEncoder(new HideF0EncoderFirstLastRand(Config.LOG_LEVEL, threshold, path));
            } else {
                throw new Error("Add HideF0Encoder to your new HideF0 variant: " + Config.HIDE_F0_TYPE.toString());
            }
            try {
                BitsCollector bitsCollector = encoder.encode();
                HideF0Encoder hideF0Encoder = encoder.getHideF0Encoder();
                synchronized (result) {
                    bitsCollector.setPath(hideF0Encoder.getPitchCollector().getPath());
                    bitsCollector.setThreshold(hideF0Encoder.getPitchCollector().getThreshold());
                    System.out.println("Encoded:\t" + path + "\t" + threshold + "\t" + hideF0Encoder.getNumberOfHiddenPositions());
                    result.add(new SampleResult(
                            new AllowPlaces(path, threshold, hideF0Encoder.getNumberOfHiddenPositions()),
                            hideF0Encoder.getPitchCollector(),
                            bitsCollector
                    ));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        })));
        es.shutdown();
        boolean finished = es.awaitTermination(24, TimeUnit.HOURS);
        if (!finished) {
            throw new Error("Some Error");
        }
        return result;
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
