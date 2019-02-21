package pl.pw.radeja.speex.encoders.config;

import org.xiph.speex.AudioFileWriter;
import org.xiph.speex.OggSpeexWriter;

import java.io.File;
import java.io.IOException;

public final class HideF0SpeexConfig {
    private static final int MODE = 0;
    private static final int SAMPLE_RATE = 8000;
    private static final int CHANNELS = 1;
    private static final int N_FRAMES = 1;
    private static final boolean VBR = false;
    //    private static final String VERSION = "HideF0-encoder";
    public static final String VERSION = "Java Speex Command Line Encoder v0.9.7 ($Revision$)";

    public static AudioFileWriter getAudioFileWriter(File destPath) throws IOException {
        AudioFileWriter writer = new OggSpeexWriter(MODE, SAMPLE_RATE, CHANNELS, N_FRAMES, VBR);
        writer.open(destPath);
        writer.writeHeader("Encoded with: " + VERSION);
        return writer;
    }
}
