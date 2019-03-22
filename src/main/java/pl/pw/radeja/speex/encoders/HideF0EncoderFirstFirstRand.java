package pl.pw.radeja.speex.encoders;

import pl.pw.radeja.speex.pitch.changers.LinearApproximateRand;

public class HideF0EncoderFirstFirstRand extends HideF0EncoderFirstFirst {

    public HideF0EncoderFirstFirstRand(int threshold, String path) {
        super(new LinearApproximateRand(threshold, 1L), path);
        this.type = null;
    }
}
