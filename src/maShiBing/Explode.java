package maShiBing;
import java.awt.Color;
import java.awt.Graphics;

public class Explode {
	private TankClient aTankClient;
	int x, y;
	private boolean alive = true;

	public Explode(TankClient aTankClient, int x, int y) {
		this.aTankClient = aTankClient;
		this.x = x;
		this.y = y;
	}

	int[] diameter = {	4, 7, 12, 18, 26, 32, 49, 30, 14, 6	};
	int step = 0;
	
	public void draw(Graphics g) {
		if(!alive){
			aTankClient.explodes.remove(this);
			return;
		}
		if(step == diameter.length) {
			alive = false;
			step = 0;
			return;
		}
		Color c = g.getColor();
		g.setColor(Color.orange);
		g.fillOval(x-Tank.WIDTH/2, y-Tank.HEIGHT/2, diameter[step], diameter[step]);
		step++;
		g.setColor(c);
	}
}
