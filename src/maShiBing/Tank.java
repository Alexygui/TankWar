
package maShiBing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Tank {
	/**
	 * ��ս̹�˵�����ID����
	 */
	int id;
	public static final int XSPEED=5;
	public static final int YSPEED=5;
	public static final int WIDTH = 30;
	public static final int HEIGHT = 30;
	/**
	 * ����̹��δ�䶯����֮ǰ�ķ��򣬵�����䶯ʱ�������֪ͨ
	 */
	Direction oldDirection;
	/**
	 * ��ʾ̹����Ӫ�Ĳ���ֵ������trueΪ��ս̹�ˣ�falseΪ�з�̹��
	 */
	boolean good;
	private boolean tankAlive = true;
	// ��Directionת��Ϊ����
	Direction[] directions = Direction.values();
	// �з�̹������һ�������ƶ��Ĵ����������
	private int enemyStep;
	// �з�̹�˵�����ƶ��������������
	private static Random random = new Random();
	// ��Ͳ���̹�˵ĳ���
	public static final int BARREL_LONG = 6;
	// ����̹�˵�x��y����ֵ
	/**
	 * ̹�˵������x��yֵ
	 */
	int x, y;
		
	// �����������жϷ�����Ƿ񱻰���
	private boolean leftPressed = false,
					rightPressed = false,
					upPressed = false,
					downPressed = false;
	
	TankClient aTankClient = null;

	/**
	 * ��ʾ̹����Ͳ�����ö�����ͱ���
	 */
	Direction barrelDirection = Direction.U;
	/**
	 * ��ʾ̹�˷����ö�����ͱ�������ʼ��ΪSTOPֵ����ֹ
	 */
	Direction aDirection = Direction.STOP;
	
	// ���췽��������̹�˵���������
	public Tank(int x, int y, boolean good) {
		this.x = x;
		this.y = y;
		this.good = good;
	}
	
	// ���췽������������TankClient��Ķ���ʹ��Tank�Ķ����������TankClient�Ķ��󣬲��������ڽ�
	// Tank��Ĳ������ݸ�TankClient�Ķ���Ʃ�磺��fire()�������ɵ�Missile���󴫵ݸ�TankClient�Ķ���
	public Tank(int x, int y,Boolean good, TankClient aTankClient, Direction aDirection) {
		this(x, y, good);
		this.aTankClient = aTankClient;
	}
	
	// ����̹�˸��ݷ�����������ƶ�
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
	
	// ����̹�˿���ķ���������һ���ڵ������Ҽӵ�TankClient���ArrayList�б���
	public void fire() {
		if(!tankAlive) return;
		Missile aMissile = new Missile(id, x+(Tank.WIDTH - Missile.WIDTH)/2, y+(Tank.HEIGHT - Missile.HEIGHT)/2, barrelDirection, this.aTankClient, this.good);
		aTankClient.missiles.add(aMissile);
		
		MissileMessage aMissileMessage = new MissileMessage(aMissile);
		aTankClient.aNetClient.send(aMissileMessage);
	}
	
	// �жϰ�������Ͼ���Tank���˶�����
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
	
	// ��̹��
	public void draw(Graphics g) {
		// ̹�˲��ǻ��žͲ�����
		if(!tankAlive) {
			if(!good)
				aTankClient.tanks.remove(this);
			return;
		}
		Color c = g.getColor();		
		// �ҷ�Ϊ��ɫ���з�Ϊ��ɫ
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
	
	// �������̰������£�����������̹�˵Ĳ���
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		// ������WASD�����Ʒ���
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
	// �������̰������𣬲���������̹�˵Ĳ���
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
	
	// �����ж��ڵ���̹���Ƿ��ཻ�����Ƿ���ײ�ķ���
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
