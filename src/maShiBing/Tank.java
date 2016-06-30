
package maShiBing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Tank {
	/**
	 * 主战坦克的网络ID号码
	 */
	int id;
	public static final int XSPEED=5;
	public static final int YSPEED=5;
	public static final int WIDTH = 30;
	public static final int HEIGHT = 30;
	/**
	 * 保存坦克未变动方向之前的方向，当方向变动时向服务器通知
	 */
	Direction oldDirection;
	/**
	 * 表示坦克阵营的布尔值变量，true为主战坦克，false为敌方坦克
	 */
	boolean good;
	private boolean tankAlive = true;
	// 将Direction转化为数组
	Direction[] directions = Direction.values();
	// 敌方坦克沿着一个方向移动的次数的随机数
	private int enemyStep;
	// 敌方坦克的随机移动的随机数产生器
	private static Random random = new Random();
	// 炮筒伸出坦克的长度
	public static final int BARREL_LONG = 6;
	// 保存坦克的x，y坐标值
	/**
	 * 坦克的坐标的x，y值
	 */
	int x, y;
		
	// 声明变量：判断方向键是否被按下
	private boolean leftPressed = false,
					rightPressed = false,
					upPressed = false,
					downPressed = false;
	
	TankClient aTankClient = null;

	/**
	 * 表示坦克炮筒方向的枚举类型变量
	 */
	Direction barrelDirection = Direction.U;
	/**
	 * 表示坦克方向的枚举类型变量，初始化为STOP值，静止
	 */
	Direction aDirection = Direction.STOP;
	
	// 构造方法，传送坦克的坐标数据
	public Tank(int x, int y, boolean good) {
		this.x = x;
		this.y = y;
		this.good = good;
	}
	
	// 构造方法，用来接收TankClient类的对象，使得Tank的对象可以引用TankClient的对象，并且以利于将
	// Tank类的参数传递给TankClient的对象，譬如：将fire()方法生成的Missile对象传递给TankClient的对象
	public Tank(int x, int y,Boolean good, TankClient aTankClient, Direction aDirection) {
		this(x, y, good);
		this.aTankClient = aTankClient;
	}
	
	// 控制坦克根据方向参数进行移动
	public void move() {
		
		switch (aDirection) {
		case L: x -= XSPEED;	break;
		case R: x += XSPEED;	break;
		case U: y -= YSPEED;	break;
		case D: y += YSPEED;	break;
		case LU: x -= XSPEED;	y -= YSPEED;	break;
		case LD: x -= XSPEED;	y += YSPEED;	break;
		case RU: x += XSPEED;	y -= YSPEED;	break;
		case RD: x += XSPEED;	y += YSPEED;	break;
		case STOP: 	break;
		}
		if(aDirection != Direction.STOP) barrelDirection = aDirection; 
		if(x<0) x=0;
		if(y<25) y=25;
		if(x>aTankClient.GAME_WIDTH-WIDTH) x= aTankClient.GAME_WIDTH-WIDTH;
		if(y>aTankClient.GAME_HEIGHT-HEIGHT) y= aTankClient.GAME_HEIGHT-HEIGHT;
		/*
		if(!good) {	
			
			if(enemyStep == 0) {
			enemyStep = random.nextInt(30)+3;
			int r = random.nextInt(directions.length-1);
			//barrelDirection = directions[r];
			aDirection = directions[r];
			}
			enemyStep--;
			if(random.nextInt(40)>38) fire();
		}
		*/
	}
	
	// 控制坦克开火的方法，生成一发炮弹，并且加到TankClient里的ArrayList中保存
	public void fire() {
		if(!tankAlive) return;
		Missile aMissile = new Missile(id, x+(Tank.WIDTH - Missile.WIDTH)/2, y+(Tank.HEIGHT - Missile.HEIGHT)/2, barrelDirection, this.aTankClient, this.good);
		aTankClient.missiles.add(aMissile);
		
		MissileMessage aMissileMessage = new MissileMessage(aMissile);
		aTankClient.aNetClient.send(aMissileMessage);
	}
	
	// 判断按键的组合决定Tank的运动方向
	void tankDirection() {
		oldDirection = this.aDirection;
		
		if(leftPressed & !rightPressed & !upPressed & !downPressed) aDirection = Direction.L;
		else if(!leftPressed & rightPressed & !upPressed & !downPressed) aDirection = Direction.R;
		else if(!leftPressed & !rightPressed & upPressed & !downPressed) aDirection = Direction.U;
		else if(!leftPressed & !rightPressed & !upPressed & downPressed) aDirection = Direction.D;
		else if(leftPressed & !rightPressed & upPressed & !downPressed) aDirection = Direction.LU;
		else if(leftPressed & !rightPressed & !upPressed & downPressed) aDirection = Direction.LD;
		else if(!leftPressed & rightPressed & upPressed & !downPressed) aDirection = Direction.RU;
		else if(!leftPressed & rightPressed & !upPressed & downPressed) aDirection = Direction.RD;
		else if(!leftPressed & !rightPressed & !upPressed & !downPressed) aDirection = Direction.STOP;
		
		if(aDirection != oldDirection) {
			TankMoveMessage aTankMoveMessage = new TankMoveMessage(id, x, y, aDirection);
			aTankClient.aNetClient.send(aTankMoveMessage);
		}
	}
	
	// 画坦克
	public void draw(Graphics g) {
		// 坦克不是活着就不画了
		if(!tankAlive) {
			if(!good)
				aTankClient.tanks.remove(this);
			return;
		}
		Color c = g.getColor();		
		// 我方为红色，敌方为蓝色
		if(good) g.setColor(Color.red);
		else g.setColor(Color.blue);
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.drawString("ID: " + id, x, y - 10);
		g.setColor(Color.white);
		switch (barrelDirection) {
		case L: g.drawLine(x+WIDTH/2, y+HEIGHT/2, x-BARREL_LONG, y+HEIGHT/2);	break;
		case R: g.drawLine(x+WIDTH/2, y+HEIGHT/2, x+WIDTH+BARREL_LONG, y+HEIGHT/2);	break;
		case U: g.drawLine(x+WIDTH/2, y+HEIGHT/2, x+WIDTH/2, y-BARREL_LONG);	break;
		case D: g.drawLine(x+WIDTH/2, y+HEIGHT/2, x+WIDTH/2, y+HEIGHT+BARREL_LONG);	break;
		case LU: g.drawLine(x+WIDTH/2, y+HEIGHT/2, x, y);	break;
		case LD: g.drawLine(x+WIDTH/2, y+HEIGHT/2, x, y+HEIGHT);	break;
		case RU: g.drawLine(x+WIDTH/2, y+HEIGHT/2, x+WIDTH, y);	break;
		case RD: g.drawLine(x+WIDTH/2, y+HEIGHT/2, x+WIDTH, y+WIDTH);	break;
		} 
		g.setColor(c);
		move();
	}
	
	// 监听键盘按键按下，并调整控制坦克的参数
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		// 加入了WASD键控制方向
		switch (keyCode) {
		case KeyEvent.VK_J:
		case KeyEvent.VK_CONTROL:	fire();		break;
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_A:			leftPressed= true;	break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_D:			rightPressed = true;	break;
		case KeyEvent.VK_UP:
		case KeyEvent.VK_W:			upPressed = true;	break;
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_S:			downPressed = true;	break;
		}
		tankDirection();
	}
	// 监听键盘按键弹起，并调整控制坦克的参数
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_A:			leftPressed= false;	break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_D:			rightPressed = false;	break;
		case KeyEvent.VK_UP:
		case KeyEvent.VK_W:			upPressed = false;	break;
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_S:			downPressed = false;	break;
		}
		tankDirection();
	}
	
	// 用来判断炮弹和坦克是否相交，即是否碰撞的方法
	public Rectangle getRectangle() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}
	

	public boolean getTankAlive() {
		return tankAlive;
	}

	public void setTankAlive(boolean tankAlive) {
		this.tankAlive = tankAlive;
	}

	public boolean isGood() {
		return good;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
}
