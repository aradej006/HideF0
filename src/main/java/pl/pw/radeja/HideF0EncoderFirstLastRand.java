package pl.pw.radeja;

import pl.pw.radeja.pitch.changers.LinearApproximateRand;

public class HideF0EncoderFirstLastRand extends HideF0EncoderFirstLast {

    public HideF0EncoderFirstLastRand(int logLevel, int threshold, String path) {
        super(new LinearApproximateRand(logLevel, threshold, 1L), path);
        hiddenPositionPerFrame = 2;
    }

}
