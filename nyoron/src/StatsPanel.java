import javax.swing.*;
import java.awt.*;
public class StatsPanel extends JPanel
{
	private JLabel title;

	public StatsPanel()
	{
		title = new JLabel("Capstone Robot Demo");
		title.setForeground(Color.red);

		this.setBackground(Color.black);
		this.setLayout(new FlowLayout());
		this.add(title);
		this.setPreferredSize(new Dimension(800,25));
	}
}