package pl.pw.radeja.pesq.common;

public class PesqResult {
    private PesqFiles pesqFiles;
    private Float rawMos;
    private Float mosLqo;

    public PesqResult(PesqFiles pesqFiles, Float rawMos, Float mosLqo) {
        this.pesqFiles = pesqFiles;
        this.rawMos = rawMos;
        this.mosLqo = mosLqo;
    }

    public PesqFiles getPesqFiles() {
        return pesqFiles;
    }

    public Float getRawMos() {
        return rawMos;
    }

    public Float getMosLqo() {
        return mosLqo;
    }

    public String getSampleName() {
        String temp = pesqFiles.getPathToDegraded().split("TIMIT_")[1];
        return temp.charAt(0) + temp.split("-")[0].substring(2);
    }

    public Integer getSampleThreshold() {
        return Integer.parseInt(pesqFiles.getPathToDegraded().split("-")[3]);
    }
}
