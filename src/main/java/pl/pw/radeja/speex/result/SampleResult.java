package pl.pw.radeja.speex.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.pw.radeja.speex.pitch.collectors.PitchCollector;

@Getter
@AllArgsConstructor
public class SampleResult {
    private AllowPlaces allowPlaces;
    private PitchCollector pitchCollector;
    private BitsCollector bitsCollector;
}
