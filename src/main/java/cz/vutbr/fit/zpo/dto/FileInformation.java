package cz.vutbr.fit.zpo.dto;

public class FileInformation {
    private String name;
    private String mimeType;
    private long size;

    /**
     * Create structure holding file information
     * @param name name of file
     * @param mimeType mime type of file
     * @param size size of file in bytes
     */
    public FileInformation(String name, String mimeType, long size) {
        this.name = name;
        this.mimeType = mimeType;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public long getSize() {
        return size;
    }
}
