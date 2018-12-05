package pl.pw.radeja;

public final class AllowPlaces {
    private String path;
    private Integer threshold;
    private Integer numberOfAllowPlaces;

    public AllowPlaces(String path, Integer threshold, Integer numberOfAllowPlaces) {
        this.path = path;
        this.threshold = threshold;
        this.numberOfAllowPlaces = numberOfAllowPlaces;
    }

    public String getPath() {
        return path;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public Integer getNumberOfAllowPlaces() {
        return numberOfAllowPlaces;
    }

    public String getSampleName() {
        String temp = path.split("TIMIT_")[1];
        return temp.charAt(0) + temp.split("-")[0].substring(2);
    }
}
