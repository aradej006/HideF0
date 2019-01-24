package pl.pw.radeja.pitch.collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PitchValue {
    private int numberOfFrame;
    private List<Integer> pitchValues;
    private boolean changed;
    private int calculatedThreshold;
    private int calculatedThresholdAfterHideF0;
}
