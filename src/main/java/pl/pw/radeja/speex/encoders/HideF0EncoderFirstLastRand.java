package pl.pw.radeja.speex.encoders;

import pl.pw.radeja.speex.pitch.changers.LinearApproximateRand;

public class HideF0EncoderFirstLastRand extends HideF0EncoderFirstLast {

    public HideF0EncoderFirstLastRand(int threshold, String path) {
        super(new LinearApproximateRand(threshold, 1L), path);
        hiddenPositionPerFrame = 2;
    }

}
