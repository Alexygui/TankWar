package maShiBing;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

public class Missile {
	public static final int XSPEED = 10;
	public static final int YSPEED = 10;
	public static final int WIDTH = 10;
	public static final int HEIGHT = 10;
	int x, y;
	int tankID;
	Direction missileDirection;
	// 区分敌我双方的子弹类型，使得己方不能打己方的坦克
	boolean good;
	// 用来表示炮弹是否可用的字段，如果不可用则在程序中改为false，并调用方法将其删除，以免占用内存
	private boolean missileLife = true;
	private TankClient aTankClient;
	

	// 炮弹的构造器，同时得到x，y轴和方向三个参数，由Tank.fire()方法生成，
	// 故这三个参数是Tank类提供的坦克的坐标和方向参数
	public Missile(int x, int y, Direction missileDirection) {
		// "(Tank.WIDTH - Missile.WIDTH)/2"修正了炮弹的位置，使得炮弹从坦克的正中心打出
		//this.x = x+(Tank.WIDTH - Missile.WIDTH)/2;	
		//this.y = y+(Tank.HEIGHT - Missile.HEIGHT)/2;
		this.x = x;
		this.y = y;
		this.missileDirection = missileDirection;
	}
	
	public Missile(int x, int y, Direction missileDirection, TankClient aTankClient, boolean good) {
		this(x, y, missileDirection);
		this.aTankClient = aTankClient;
		this.good = good;
	}
	
	public Missile(int tankID, int x, int y, Direction missileDirection, TankClient aTankClient, boolean good) {
		
		this(x, y, missileDirection, aTankClient, good);
		this.tankID = tankID;
	}

	// 画出炮弹的图片
	public void draw(Graphics g) {
		if(!missileLife) {
			aTankClient.missiles.remove(this);
			return;
		}
		Color c = g.getColor();
		g.setColor(Color.green);
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.setColor(c);
		
		move();
	}
	
	// 根据方向参数修改炮弹移动的x，y轴参数
	public void move() {
		switch (missileDirection) {
		case L: x -= XSPEED;	break;
		case R: x += XSPEED;	break;
		case U: y -= YSPEED;	break;
		case D: y += YSPEED;	break;
		case LU: x -= XSPEED;	y -= YSPEED;	break;
		case LD: x -= XSPEED;	y += YSPEED;	break;
		case RU: x += XSPEED;	y -= YSPEED;	break;
		case RD: x += XSPEED;	y += YSPEED;	break;
		}
		// 炮弹出了作用范围后就从ArrayList中将其删除，以免占用内存
		if(x<0 || y<25 || x>TankClient.GAME_WIDTH || y>TankClient.GAME_HEIGHT){
			missileLife = false;
		}			
	}
	
	// 用来判断炮弹和坦克是否相交，即是否碰撞的方法
	public Rectangle getRectangle() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}
	
	// 检测坦克是否被击中：炮弹与作为参数传入的坦克相交，并且坦克还是活着的，并且坦克的阵营不同
	public boolean hitTank(Tank aTank) {
		if(this.missileLife && this.getRectangle().intersects(aTank.getRectangle()) && aTank.getTankAlive() && this.good != aTank.isGood()) {
			aTank.setTankAlive(false);
			this.missileLife = false;
			Explode anExplode = new Explode(aTankClient, x, y);
			aTankClient.explodes.add(anExplode);
			return true;
		}
		else return false;
	}
	
	// 判断坦克数组是否被击中，调用hitTank实现
	public boolean hitTanks(List<Tank> tankList) {
		for(int i=0; i<tankList.size(); i++)
			if(hitTank(tankList.get(i)))	return true;
		return false;
	}

	public boolean getMissileLife() {
		return missileLife;
	}

	public void setMissileLife(boolean missileLife) {
		this.missileLife = missileLife;
	}
}
