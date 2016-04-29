package arhangel.dim.lections.threads.future;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class Sandbox {

    public static void main(String[] args) throws Exception {
        JFrame frame = buildFrame();

        frame.add(new ImagePanel());
    }

    private static JFrame buildFrame() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setVisible(true);
        return frame;
    }

    private static class ImagePanel extends JPanel {

        private BufferedImage image;

        ImagePanel() throws Exception {

            image = ImageIO.read(new URL("https://gic6.mycdn.me/image?t=56&bid=816871160539&id=816871160539&plc=WEB&tkn=*iaoYC50VGni4tMfp7sTdPUMsBD0"));
            Dimension dimension = new Dimension();
            dimension.width = image.getWidth(null);
            dimension.height = image.getHeight(null);
            setPreferredSize(dimension);
        }

        @Override
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.drawImage(image, 0, 0, null);
        }
    }


}
