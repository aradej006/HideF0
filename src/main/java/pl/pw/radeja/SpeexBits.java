package pl.pw.radeja;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class SpeexBits {
    private NamesOfBits namesOfBits;
    private int bitsData;
    private int numberOfBits;

    public SpeexBits setBitsData(int bitsData) {
        this.bitsData = bitsData;
        return this;
    }
}
