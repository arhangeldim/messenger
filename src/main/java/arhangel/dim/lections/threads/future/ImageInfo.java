package arhangel.dim.lections.threads.future;

/**
 *
 */
public class ImageInfo {

    private int width;
    private int height;
    private String mime;

    public ImageInfo(int width, int height, String mime) {
        this.width = width;
        this.height = height;
        this.mime = mime;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getMime() {
        return mime;
    }
}
