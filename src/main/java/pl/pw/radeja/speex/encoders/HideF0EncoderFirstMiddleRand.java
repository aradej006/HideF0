package pl.pw.radeja.speex.encoders;

import pl.pw.radeja.Config;
import pl.pw.radeja.speex.pitch.changers.LinearApproximateMiddleRand;

public class HideF0EncoderFirstMiddleRand extends HideF0EncoderFirstMiddle {

    public HideF0EncoderFirstMiddleRand(int threshold, String path) {
        super(new LinearApproximateMiddleRand(threshold, 1L), path);
        this.type = Config.HideF0Type.FIRST_MIDDLE_RAND;
    }

}
