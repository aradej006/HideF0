package pl.pw.radeja.human;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.pw.radeja.Config;
import pl.pw.radeja.JSpeexDec;
import pl.pw.radeja.JSpeexEnc;
import pl.pw.radeja.speex.encoders.*;
import pl.pw.radeja.speex.result.AllowPlaces;
import pl.pw.radeja.speex.result.BitsCollector;
import pl.pw.radeja.speex.result.SampleResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static pl.pw.radeja.FinePithExtractor.getSpeexEncoder;

@Slf4j
public class HumanSampleGenerator {

    public static void main(@NotNull final String[] args) throws InterruptedException, IOException {
        List<SampleResult> sampleResults = calculateHumanSamples(Config.getHumanTestSamples());
        runDecoding(getEncodedFiles());
    }

    private static List<SampleResult> calculateHumanSamples(List<HumanSample> humanSamples) throws InterruptedException {
        List<SampleResult> result = Collections.synchronizedList(new ArrayList<>());
        ExecutorService es = Config.getExecutorService();
        humanSamples.forEach(sample -> es.execute(() -> {
            JSpeexEnc encoder = getEncoder(sample.getThreshold(), sample.getPath(), sample.getHideF0Type());
            try {
                log.debug("Encoding:\tSample: " + encoder.getHideF0Encoder().getPitchCollector().getSampleName() + "\t, Threshold: " + sample.getThreshold());
                BitsCollector bitsCollector = encoder.encode();
                HideF0Encoder hideF0Encoder = encoder.getHideF0Encoder();
                bitsCollector.setPath(hideF0Encoder.getPitchCollector().getPath());
                bitsCollector.setThreshold(hideF0Encoder.getPitchCollector().getThreshold());
                log.info("Encoded:\tSample: " + hideF0Encoder.getPitchCollector().getSampleName() + "\t, Threshold: " + sample.getThreshold());
                result.add(new SampleResult(
                        new AllowPlaces(sample.getPath(), sample.getThreshold().intValue(), hideF0Encoder.getNumberOfHiddenPositions()),
                        hideF0Encoder.getPitchCollector(),
                        bitsCollector
                ));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        es.shutdown();
        boolean finished = es.awaitTermination(24, TimeUnit.HOURS);
        if (!finished) {
            throw new Error("Some Error");
        }
        return result;
    }

    private static JSpeexEnc getEncoder(Float threshold, String path, @NotNull Config.HideF0Type type) {
        JSpeexEnc encoder;
        if (type.equals(Config.HideF0Type.FIRST_LAST)) {
            encoder = getSpeexEncoder(new HideF0EncoderFirstLast(threshold.intValue(), path));
        } else if (type.equals(Config.HideF0Type.FIRST_FIRST)) {
            encoder = getSpeexEncoder(new HideF0EncoderFirstFirst(threshold.intValue(), path));
        } else if (type.equals(Config.HideF0Type.FIRST_LAST_RAND)) {
            encoder = getSpeexEncoder(new HideF0EncoderFirstLastRand(threshold.intValue(), path));
        } else if (type.equals(Config.HideF0Type.FIRST_MIDDLE)) {
            encoder = getSpeexEncoder(new HideF0EncoderFirstMiddle(threshold.intValue(), path));
        } else if (type.equals(Config.HideF0Type.FIRST_MIDDLE_RAND)) {
            encoder = getSpeexEncoder(new HideF0EncoderFirstMiddleRand(threshold.intValue(), path));
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


    private static JSpeexDec getSpeexDecoder(final String filename) {
        @NotNull JSpeexDec dec = new JSpeexDec();
        dec.setSrcFile(filename + ".spx");
        dec.setDestFile(filename + "-dec.wav");
        dec.setSrcFormat(JSpeexDec.FILE_FORMAT_OGG);
        dec.setDestFormat(JSpeexDec.FILE_FORMAT_WAVE);
        dec.setPrintlevel(JSpeexDec.ERROR);
        dec.setEnhanced(true);
        return dec;
    }

    private static List<String> getEncodedFiles() {
        return Config.getHumanTestSamples().stream()
                .map(s -> s.getPath() + "-hide-" + s.getThreshold().intValue() + "_" + s.getHideF0Type().getName())
                .collect(Collectors.toList());
    }
}
