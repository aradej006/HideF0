package pl.pw.radeja.speex.encoders;

import pl.pw.radeja.Config;
import pl.pw.radeja.speex.pitch.changers.LinearApproximateRand;

public class HideF0EncoderFirstLastRand extends HideF0EncoderFirstLast {

    public HideF0EncoderFirstLastRand(float threshold, String path) {
        super(new LinearApproximateRand(threshold, 1L), path);
        this.type = Config.HideF0Type.FIRST_LAST_RAND;
    }

}
