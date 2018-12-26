package pl.pw.radeja;

import pl.pw.radeja.pitch.changers.LinearApproximateRand;

public class HideF0EncoderFirstFirstRand extends HideF0EncoderFirstFirst {

    public HideF0EncoderFirstFirstRand(int logLevel, int threshold, String path) {
        super(new LinearApproximateRand(logLevel, threshold, 1L), path);
        hiddenPositionPerFrame = 2;
    }
}
