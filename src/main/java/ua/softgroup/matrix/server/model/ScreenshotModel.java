package ua.softgroup.matrix.server.model;

public class ScreenshotModel extends TokenModel {
    private static final long serialVersionUID = 1L;
    private byte[] file;

    public ScreenshotModel(String token) {
        super(token);
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
