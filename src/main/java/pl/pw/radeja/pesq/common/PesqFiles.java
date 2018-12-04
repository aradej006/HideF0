package pl.pw.radeja.pesq.common;

public class PesqFiles {
    private String pathToReference;
    private String pathToDegraded;

    public PesqFiles(String pathToReference, String pathToDegraded) {
        this.pathToReference = pathToReference;
        this.pathToDegraded = pathToDegraded;
    }

    public String getPathToReference() {
        return pathToReference;
    }

    public String getPathToDegraded() {
        return pathToDegraded;
    }
}
