package com.dc.echo.simple;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class 无边框窗口 {
	public static void main(String[] args) throws Throwable {
		//第一种方法：
		JFrame f = new JFrame("test");
		f.setUndecorated(true);
		f.getGraphicsConfiguration().getDevice().setFullScreenWindow(f);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Graphics gg = f.getGraphics();
		BufferedImage imgBuf = ImageIO.read(new File("0.jpg"));
        gg.drawImage(imgBuf, 0, 0, null);
        gg.dispose();
//		第二种方法：
//		JFrame f = new JFrame("test");
//		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//		Rectangle bounds = new Rectangle(screenSize);
//		f.setBounds(bounds);
//		f.setVisible(true);

	}
}
