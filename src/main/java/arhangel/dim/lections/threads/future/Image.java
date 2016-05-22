package arhangel.dim.lections.threads.future;

/**
 *
 */
public class Image {
    private byte[] bitmap;
    private ImageInfo imageInfo;

    public Image(byte[] bitmap, ImageInfo imageInfo) {
        this.bitmap = bitmap;
        this.imageInfo = imageInfo;
    }

    public byte[] getBitmap() {
        return bitmap;
    }

    public ImageInfo getImageInfo() {
        return imageInfo;
    }
}
