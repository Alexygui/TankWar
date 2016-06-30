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
	// ���ֵ���˫�����ӵ����ͣ�ʹ�ü������ܴ򼺷���̹��
	boolean good;
	// ������ʾ�ڵ��Ƿ���õ��ֶΣ�������������ڳ����и�Ϊfalse�������÷�������ɾ��������ռ���ڴ�
	private boolean missileLife = true;
	private TankClient aTankClient;
	

	// �ڵ��Ĺ�������ͬʱ�õ�x��y��ͷ���������������Tank.fire()�������ɣ�
	// ��������������Tank���ṩ��̹�˵�����ͷ������
	public Missile(int x, int y, Direction missileDirection) {
		// "(Tank.WIDTH - Missile.WIDTH)/2"�������ڵ���λ�ã�ʹ���ڵ���̹�˵������Ĵ��
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

	// �����ڵ���ͼƬ
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
	
	// ���ݷ�������޸��ڵ��ƶ���x��y�����
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
		// �ڵ��������÷�Χ��ʹ�ArrayList�н���ɾ��������ռ���ڴ�
		if(x<0 || y<25 || x>TankClient.GAME_WIDTH || y>TankClient.GAME_HEIGHT){
			missileLife = false;
		}			
	}
	
	// �����ж��ڵ���̹���Ƿ��ཻ�����Ƿ���ײ�ķ���
	public Rectangle getRectangle() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}
	
	// ���̹���Ƿ񱻻��У��ڵ�����Ϊ���������̹���ཻ������̹�˻��ǻ��ŵģ�����̹�˵���Ӫ��ͬ
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
	
	// �ж�̹�������Ƿ񱻻��У�����hitTankʵ��
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
