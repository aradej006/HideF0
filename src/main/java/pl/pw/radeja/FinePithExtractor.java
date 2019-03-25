package pl.pw.radeja;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.NotNull;
import pl.pw.radeja.pesq.PesqResultPrinter;
import pl.pw.radeja.pesq.PesqRunner;
import pl.pw.radeja.pesq.common.PesqFiles;
import pl.pw.radeja.pesq.common.PesqResult;
import pl.pw.radeja.speex.encoders.*;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static pl.pw.radeja.Config.WEKA_FRAMES_PER_RECORD;

@Slf4j
public class FinePithExtractor {

    public static void main(@NotNull final String[] args) throws InterruptedException, IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // calculate allow places
        List<SampleResult> result = new ArrayList<>();
        List<SampleResult> wekaResults = new ArrayList<>();
        if (Config.CALCULATE_ALLOW_PLACES) {
            result = calculate(false);
        }
        if (Config.PRINT_WEKA_FILES || Config.PRINT_WEKA_VECTOR_FILES) {
            wekaResults = calculate(true);
        }

        // decoding
        if (Config.DECODE_FILES) {
            List<String> filesToDecode = new ArrayList<>();
            Config.getSamples().forEach(s -> Config.THRESHOLDS.forEach(t -> filesToDecode.add(s + "-hide-" + t + "_" + Config.HIDE_F0_TYPE.getName())));
            runDecoding(filesToDecode);
        }

        //pesq
        List<PesqResult> pesqResults = new ArrayList<>();
        if (Config.CALCUALTE_PESQ) {
            List<PesqFiles> filesToPesq = new ArrayList<>();
            Config.getSamples().forEach(s -> Config.THRESHOLDS.forEach(t -> filesToPesq.add(
                    new PesqFiles(
                            s + ".wav",
                            s + "-hide-" + t + "_" + Config.HIDE_F0_TYPE.getName() + "-dec.wav"
                    ))));
            pesqResults = PesqRunner.run(filesToPesq);
        }

        //print pitchValues
        if (Config.CALCULATE_ALLOW_PLACES) {
            result.stream().map(SampleResult::getPitchCollector).collect(Collectors.toList()).forEach(pitchCollector -> {
                try (PrintWriter printWriter = new PrintWriter(pitchCollector.getPath() + "-pitch-" + pitchCollector.getThreshold() + ".txt", "UTF-8")) {
                    PitchCollectorPrint.print(pitchCollector.getFramePitchValues(), printWriter, false).close();
                } catch (FileNotFoundException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            });
        }
        if (Config.PRINT_WEKA_FILES) {
            WekaPrinter.print(wekaResults.stream().map(SampleResult::getPitchCollector).collect(Collectors.toList()), WEKA_FRAMES_PER_RECORD);
        }
        if (Config.PRINT_WEKA_VECTOR_FILES) {
            WekaVectorPrinter.print(wekaResults.stream().map(SampleResult::getPitchCollector).collect(Collectors.toList()), WEKA_FRAMES_PER_RECORD);
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
        log.info("Total time:" + stopWatch.toString());
    }

    public static JSpeexEnc getSpeexEncoder(final HideF0Encoder hideF0Encoder) {
        @NotNull JSpeexEnc enc = new JSpeexEnc(hideF0Encoder);
        enc.srcFile = hideF0Encoder.getPath() + ".wav";
        enc.destFile = hideF0Encoder.getPath() + "-pitch.spx";
        enc.srcFormat = JSpeexEnc.FILE_FORMAT_WAVE;
        enc.destFormat = JSpeexEnc.FILE_FORMAT_OGG;
        enc.printlevel = JSpeexEnc.ERROR;
        enc.mode = 0;
        enc.sampleRate = 8000;
        enc.channels = 1;
//        enc.quality = 8;      // default 8
        return enc;
    }

    public static JSpeexDec getSpeexDecoder(final String filename) {
        @NotNull JSpeexDec dec = new JSpeexDec();
        dec.srcFile = filename + ".spx";
        dec.destFile = filename + "-dec.wav";
        dec.srcFormat = JSpeexDec.FILE_FORMAT_OGG;
        dec.destFormat = JSpeexDec.FILE_FORMAT_WAVE;
        dec.printlevel = JSpeexDec.ERROR;
        dec.enhanced = true;
        return dec;
    }

    private static List<SampleResult> calculate(boolean calculateForWekaFiles) throws InterruptedException {
        List<SampleResult> result = Collections.synchronizedList(new ArrayList<>());
        for (float threshold : Config.THRESHOLDS) {
            ExecutorService es = Config.getExecutorService();
            Config.getSamples().forEach(path -> es.execute(() -> {
                JSpeexEnc encoder = getEncoder(threshold, path, calculateForWekaFiles);
                try {
                    log.debug("Encoding:\tSample: " + encoder.getHideF0Encoder().getPitchCollector().getSampleName() + "\t, Threshold: " + threshold);
                    BitsCollector bitsCollector = encoder.encode();
                    HideF0Encoder hideF0Encoder = encoder.getHideF0Encoder();
                    bitsCollector.setPath(hideF0Encoder.getPitchCollector().getPath());
                    bitsCollector.setThreshold(hideF0Encoder.getPitchCollector().getThreshold());
                    log.info("Encoded:\tSample: " + hideF0Encoder.getPitchCollector().getSampleName() + "\t, Threshold: " + threshold);
                    result.add(new SampleResult(
                            new AllowPlaces(path, threshold, hideF0Encoder.getNumberOfHiddenPositions()),
                            hideF0Encoder.getPitchCollector(),
                            bitsCollector
                    ));
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }));
            es.shutdown();
            boolean finished = es.awaitTermination(24, TimeUnit.HOURS);
            if (!finished) {
                throw new Error("Some Error");
            }
        }
        return result;
    }

    private static JSpeexEnc getEncoder(float threshold, String path, boolean calculateForWekaFiles) {
        JSpeexEnc encoder;
        if (calculateForWekaFiles && WekaVectorPrinter.getNoHideF0Set().contains(Config.getSampleNameFromPath(path))) {
            encoder = getSpeexEncoder(new NonHideF0Encoder(threshold, path));
        } else if (Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_LAST)) {
            encoder = getSpeexEncoder(new HideF0EncoderFirstLast(threshold, path));
        } else if (Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_FIRST)) {
            encoder = getSpeexEncoder(new HideF0EncoderFirstFirst(threshold, path));
        } else if (Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_FIRST_RAND)) {
            encoder = getSpeexEncoder(new HideF0EncoderFirstFirstRand(threshold, path));
        } else if (Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_LAST_RAND)) {
            encoder = getSpeexEncoder(new HideF0EncoderFirstLastRand(threshold, path));
        } else if (Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_MIDDLE)) {
            encoder = getSpeexEncoder(new HideF0EncoderFirstMiddle(threshold, path));
        } else if (Config.HIDE_F0_TYPE.equals(Config.HideF0Type.FIRST_MIDDLE_RAND)) {
            encoder = getSpeexEncoder(new HideF0EncoderFirstMiddleRand(threshold, path));
        } else {
            throw new Error("Add HideF0Encoder to your new HideF0 variant: " + Config.HIDE_F0_TYPE.getName());
        }
        return encoder;
    }

    private static void runDecoding(List<String> paths) throws IOException {
        log.debug("Decoding....");
        for (String p : paths) {
            @NotNull JSpeexDec decoder = getSpeexDecoder(p);
            decoder.decode();
            log.info("Decoded:\t" + p);
        }
        log.debug("Decoded");
    }

}
