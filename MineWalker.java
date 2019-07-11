import javax.swing.JFrame;

public class MineWalker {

	public static void main(String[] args) {
		JFrame f = new JFrame("MineWalker!");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new MineWalkerPanel());
		f.pack();
		f.setVisible(true);
		// centers window
		f.setLocationRelativeTo(null);

	}

}
