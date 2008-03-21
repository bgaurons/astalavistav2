import javax.swing.*;
import java.awt.*;


/**
<p>TempPanel.java - A GUI element giving a representation readings from the
thermopile array.</p>
	<h1>Revision History:</h1>
	<ul>
		<li>March 19, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Created file.</li>
		</ul>
	</ul>


  @author                      Benjamin Garuonskas
  @version                     0.1
 */
public class TempPanel extends JPanel
{
	/**
		Size of the "pixels."
	*/
	public static final int SIZE = 10;

	/**
		A multiplier to make the color of the pixels more pronounced
	*/
	public static final byte DEF = 3;

	/**
		An array representing all the temperature readings from the thermopile.
	*/
	public byte[][] heatMap;

    /**
	Constructor. Makes the panel and paints it black.
	@author		Benjamin Gauronskas
    */
	public TempPanel ()
	{

		this.setFocusable(true);
		this.setPreferredSize(
			new Dimension(	Thermopile.HOR_WIDTH*SIZE,
							Thermopile.VERT_WIDTH*SIZE));

		heatMap = new byte[Thermopile.HOR_WIDTH][Thermopile.VERT_WIDTH];

	}

    /**
	Paints the panels according to the heatmap array.
	@author		Benjamin Gauronskas
    */
	public void paintComponent(Graphics page)
	{
	super.paintComponent(page);
		int red;
		for(int j=0; j<Thermopile.VERT_WIDTH; j++){
			for(int k=0; k<Thermopile.HOR_WIDTH; k++){
				red = Math.abs((int)(heatMap[k][j]*DEF));
				//System.out.println("!@#$\tRed is: " + red);
				Color shit = new Color(red,0,0);

				//System.out.println(heatMap[k][j]);
				page.setColor(shit);
				page.fillRect(k*SIZE,j*SIZE,SIZE,SIZE);
			}
		}
	}

    /**
	Renews the heat map and redraws the picture.
	@param	map		The new heat map.
	@author		Benjamin Gauronskas
    */
	public void refresh(byte[][] map){
		heatMap = map;
		repaint();
	}


}

