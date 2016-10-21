package ua.softgroup.matrix.server.persistent.databasemodels;

public class ScreenshotModel extends TokenModel{
    private static final long serialVersionUID = 1L;
    private byte[] file;

    public ScreenshotModel() {
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
