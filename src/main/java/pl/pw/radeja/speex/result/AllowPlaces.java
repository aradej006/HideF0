package pl.pw.radeja.speex.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.pw.radeja.Config;

@Getter
@AllArgsConstructor
public final class AllowPlaces {
    private String path;
    private float threshold;
    private Integer numberOfAllowPlaces;

    public String getSampleName() {
        return Config.getSampleNameFromPath(path);
    }
}
