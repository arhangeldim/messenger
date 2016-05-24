package arhangel.dim.lections.threads.future;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

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

    static class ImagePanel extends JPanel {

        private BufferedImage image;

        public ImagePanel() throws Exception {

            image = ImageIO.read(new URL("https://gic6.mycdn.me/image?t=56&bid=816871160539&id=816871160539&plc=WEB&tkn=*iaoYC50VGni4tMfp7sTdPUMsBD0"));
            Dimension dd = new Dimension();
            dd.width = image.getWidth(null);
            dd.height = image.getHeight(null);
            setPreferredSize(dd);
        }

        @Override
        public void paintComponent(Graphics gg) {
            super.paintComponent(gg);
            Graphics2D g2d = (Graphics2D) gg;
            g2d.drawImage(image, 0, 0, null);
        }
    }


}
