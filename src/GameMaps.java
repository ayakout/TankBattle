import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

class GameMaps {
	private final int level = 0;
	JLabel label;
	Obstcal obstcal1[] = new Obstcal[11];
	Rectangle obstRect;
	private final Vector<Rectangle> rectVector = new Vector<Rectangle>();

	GameMaps(final JFrame frame) {
		// System.out.println ("in maps---------------------------");
		this.label = new JLabel();
		final Toolkit kit = Toolkit.getDefaultToolkit();
		Image img1 = kit
				.getImage(this.getClass().getResource("backGround.jpg"));
		img1 = img1.getScaledInstance(frame.getWidth(), frame.getHeight(),
				Image.SCALE_DEFAULT);
		final ImageIcon icon1 = new ImageIcon(img1);
		this.label.setSize(frame.getWidth(), frame.getHeight());
		this.label.setIcon(icon1);
		frame.validate();
		frame.add(this.label, -1);
		// frame.validate();

		this.obstcal1[0] = new Obstcal();
		this.obstcal1[0].setSize(80, 36);
		frame.add(this.obstcal1[0], this.level);
		frame.validate();
		this.obstcal1[0].setLocation(244, 85);
		this.obstRect = this.obstcal1[0].getBounds();
		this.rectVector.add(this.obstRect);
		frame.validate();

		this.obstcal1[1] = new Obstcal();
		this.obstcal1[1].setSize(80, 40);
		frame.add(this.obstcal1[1], this.level);
		frame.validate();
		this.obstcal1[1].setLocation(367, 85);
		frame.validate();
		this.obstRect = this.obstcal1[1].getBounds();
		this.rectVector.add(this.obstRect);

		this.obstcal1[2] = new Obstcal();
		this.obstcal1[2].setSize(80, 44);
		frame.add(this.obstcal1[2], this.level);
		frame.validate();
		this.obstcal1[2].setLocation(39, 157);
		frame.validate();
		this.obstRect = this.obstcal1[2].getBounds();
		this.rectVector.add(this.obstRect);

		this.obstcal1[3] = new Obstcal();
		this.obstcal1[3].setSize(86, 40);
		frame.add(this.obstcal1[3], this.level);
		frame.validate();
		this.obstcal1[3].setLocation(161, 160);
		frame.validate();
		this.obstRect = this.obstcal1[3].getBounds();
		this.rectVector.add(this.obstRect);

		this.obstcal1[4] = new Obstcal();
		this.obstcal1[4].setSize(153, 40);
		frame.add(this.obstcal1[4], this.level);
		frame.validate();
		this.obstcal1[4].setLocation(528, 160);
		frame.validate();
		this.obstRect = this.obstcal1[4].getBounds();
		this.rectVector.add(this.obstRect);

		this.obstcal1[5] = new Obstcal();
		this.obstcal1[5].setSize(83, 40);
		frame.add(this.obstcal1[5], this.level);
		frame.validate();
		this.obstcal1[5].setLocation(73, 308);
		frame.validate();
		this.obstRect = this.obstcal1[5].getBounds();
		this.rectVector.add(this.obstRect);

		this.obstcal1[6] = new Obstcal();
		this.obstcal1[6].setSize(82, 40);
		frame.add(this.obstcal1[6], this.level);
		frame.validate();
		this.obstcal1[6].setLocation(282, 324);
		frame.validate();
		this.obstRect = this.obstcal1[6].getBounds();
		this.rectVector.add(this.obstRect);

		this.obstcal1[7] = new Obstcal();
		this.obstcal1[7].setSize(157, 40);
		frame.add(this.obstcal1[7], this.level);
		frame.validate();
		this.obstcal1[7].setLocation(528, 321);
		frame.validate();
		this.obstRect = this.obstcal1[7].getBounds();
		this.rectVector.add(this.obstRect);

		this.obstcal1[8] = new Obstcal();
		this.obstcal1[8].setSize(82, 40);
		frame.add(this.obstcal1[8], this.level);
		frame.validate();
		this.obstcal1[8].setLocation(195, 403);
		frame.validate();
		this.obstRect = this.obstcal1[8].getBounds();
		this.rectVector.add(this.obstRect);

		this.obstcal1[9] = new Obstcal();
		this.obstcal1[9].setSize(72, 40);
		frame.add(this.obstcal1[9], this.level);
		frame.validate();
		this.obstcal1[9].setLocation(445, 400);
		frame.validate();
		this.obstRect = this.obstcal1[9].getBounds();
		this.rectVector.add(this.obstRect);

		this.obstcal1[10] = new Obstcal();
		this.obstcal1[10].setSize(27, 40);
		frame.add(this.obstcal1[10], this.level);
		frame.validate();
		this.obstcal1[10].setLocation(3, 431);
		frame.validate();
		this.obstRect = this.obstcal1[10].getBounds();
		this.rectVector.add(this.obstRect);

		// frame.repaint();

		// frame.validate();
		// frame.setComponentZOrder(label,5);

	}

	Vector<Rectangle> getObstcalRectArray() {
		return this.rectVector;
	}
}
