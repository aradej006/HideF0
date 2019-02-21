package pl.pw.radeja.speex.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class SpeexBits {
    private SpeexBitsName speexBitsName;
    private int bitsData;
    private int numberOfBits;

    public SpeexBits setBitsData(int bitsData) {
        this.bitsData = bitsData;
        return this;
    }
}
