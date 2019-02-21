package pl.pw.radeja.speex.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class AllowPlaces {
    private String path;
    private Integer threshold;
    private Integer numberOfAllowPlaces;

    public String getSampleName() {
        String temp = path.split("TIMIT_")[1];
        return temp.charAt(0) + temp.split("-")[0].substring(2);
    }
}
