//file: ColorPan.java
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class ColorPan extends JComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BufferedImage image;

	public void initialize() {
		final int width = this.getSize().width;
		final int height = this.getSize().height;
		final int[] data = new int[width * height];
		int i = 0;
		for (int y = 0; y < height; y++) {
			final int red = (y * 255) / (height - 1);
			for (int x = 0; x < width; x++) {
				final int green = (x * 255) / (width - 1);
				final int blue = 128;
				data[i++] = (red << 16) | (green << 8) | (blue << 24);
			}
		}
		this.image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		this.image.setRGB(0, 0, width, height, data, 0, width);
	}

	@Override
	public void paint(final Graphics g) {
		if (this.image == null) {
			this.initialize();
		}
		g.drawImage(this.image, 0, 0, this);
	}

	@Override
	public void setBounds(final int x, final int y, final int width,
			final int height) {
		super.setBounds(x, y, width, height);
		this.initialize();
	}

	public static void main(final String[] args) {
		final JFrame frame = new JFrame("ColorPan");
		frame.getContentPane().add(new ColorPan());
		frame.setSize(300, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
