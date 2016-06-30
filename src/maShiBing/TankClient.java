package maShiBing;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class TankClient extends Frame{
	NetClient aNetClient = new NetClient(this);
	
	static final int GAME_WIDTH= 800;
	static final int GAME_HEIGHT= 600;
	// ����̹�˵ı��������Ұ��Լ������ֵ�������̹�˵Ĺ�������ʹ��̹�˿������Լ��Ĺ�������ʹ��TankClient�Ķ���
	Tank myTank = new Tank(400, 300, true, this, Direction.STOP);
	//Tank enemyTank = new Tank(50, 300, false);
	
	List<Explode> explodes = new ArrayList<Explode>();
	List<Missile> missiles = new ArrayList<Missile>();
	List<Tank> tanks = new ArrayList<Tank>(); 
	// ��ͼ�ķ���
	@Override
	public void paint(Graphics g) {
		Color c = g.getColor();
		g.setColor(Color.white);
		g.drawString("missiles count:" + missiles.size() , 10, 50);
		g.drawString("explodes count: "+ explodes.size(), 10, 70);
		g.drawString("tanks count: "+ tanks.size(), 10, 90);
		
		for(int i=0; i<missiles.size(); i++){
			Missile aMissile = missiles.get(i);
			aMissile.hitTanks(tanks);
			aMissile.hitTank(myTank);
			aMissile.draw(g);
			//if(!aMissile.getMissileLife()) missiles.remove(aMissile);
			//else aMissile.draw(g);
		}
		// ������ըЧ��
		for(int i=0; i< explodes.size(); i++){
			Explode anExplode = explodes.get(i);
			anExplode.draw(g);
		}
		// �����з���̹��
		
		for(int i=0; i<tanks.size(); i++) {
			Tank anEnemyTank = tanks.get(i);
			anEnemyTank.draw(g);
		}
		
		
		myTank.draw(g);
		//enemyTank.draw(g);
		g.setColor(c);
	}

	// ˫���壬������˸����	
	Image offScreenImage = null;
	@Override
	public void update(Graphics g) {
		if(offScreenImage == null)
			offScreenImage = this.createImage(GAME_WIDTH, GAME_HEIGHT);
		Graphics aGraphics = offScreenImage.getGraphics();
		Color c = aGraphics.getColor();
		aGraphics.setColor(Color.black);
		aGraphics.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
		aGraphics.setColor(c);
		paint(aGraphics);
		g.drawImage(offScreenImage, 0, 0, null);
	}
	
	// ���̼�������
	private class keyMonitor extends KeyAdapter {
		
		@Override
		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
			//myTank.move();   //draw()�������Ѿ����ù�move()���˴������ٴε��ã�����̹�˻���˫�����ٶ�
		}

		@Override
		public void keyPressed(KeyEvent e) {
			myTank.keyPressed(e);
			//myTank.move();  //draw()�������Ѿ����ù�move()���˴������ٴε��ã�����̹�˻���˫�����ٶ�
		}
		
	}

	// ʵ��Runnable�ӿڣ��Ӷ����Դ�launch������һ���߳�
	private class PaintThread implements Runnable {

		@Override
		public void run() {
			while(true){
				repaint();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	// �����棬���̼���
	public void launch() {
		/*
		for(int i=0; i< 10; i++) {
			enemyTanks.add(new Tank(50+40*(i+1), 50, false, this, Direction.D));
		}
		*/
		setLocation(300, 100);
		setSize(GAME_WIDTH, GAME_HEIGHT);
		setVisible(true);
		setBackground(Color.black);
		setResizable(false);
		setTitle("TankWar");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}			
		});
		
		addKeyListener(new keyMonitor());	// ���̼���
		
		new Thread(new PaintThread()).start();;	// ��ӻ�������߳�
		/**
		 * �ͻ������ӵ���������
		 */
		aNetClient.connect("127.0.1.1", TankServer.TCP_PORT);
	}
	
	
	public static void main(String[] args){
		TankClient aTankClient = new TankClient();
		aTankClient.launch();
	}
	
	class ConnectDialog extends Dialog {
		Button aButton = new Button("ȷ��");
		TextField aTextField = new TextField();
		public ConnectDialog() {
			super(TankClient.this, true);
			this.setLayout(new FlowLayout());
			this.add(aTextField);
			this.add(aButton);
			this.setLocation(600, 400);
			this.pack();
			this.addWindowListener(new WindowAdapter() {

				@Override
				public void windowClosing(WindowEvent e) {
					setVisible(false);
				}
				
			});
			aButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
		}
		
	}
	
}
