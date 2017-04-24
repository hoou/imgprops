package cz.vutbr.fit.zpo.dto;

public class ImageInformation {
    private int width;
    private int height;
    private int bitDepth;
    private int channelsCount;
    private double size;
    private long uniqueColors;

    public ImageInformation(int width, int height, int bitDepth, int channelsCount, double size, long uniqueColors) {
        this.width = width;
        this.height = height;
        this.bitDepth = bitDepth;
        this.channelsCount = channelsCount;
        this.size = size;
        this.uniqueColors = uniqueColors;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getBitDepth() {
        return bitDepth;
    }

    public int getChannelsCount() {
        return channelsCount;
    }

    public double getSize() {
        return size;
    }

    public long getUniqueColors() {
        return uniqueColors;
    }
}
