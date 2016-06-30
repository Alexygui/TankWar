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
	// 声明坦克的变量，并且把自己的类的值传给这个坦克的构造器，使得坦克可以在自己的构造器中使用TankClient的对象
	Tank myTank = new Tank(400, 300, true, this, Direction.STOP);
	//Tank enemyTank = new Tank(50, 300, false);
	
	List<Explode> explodes = new ArrayList<Explode>();
	List<Missile> missiles = new ArrayList<Missile>();
	List<Tank> tanks = new ArrayList<Tank>(); 
	// 画图的方法
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
		// 画出爆炸效果
		for(int i=0; i< explodes.size(); i++){
			Explode anExplode = explodes.get(i);
			anExplode.draw(g);
		}
		// 画出敌方的坦克
		
		for(int i=0; i<tanks.size(); i++) {
			Tank anEnemyTank = tanks.get(i);
			anEnemyTank.draw(g);
		}
		
		
		myTank.draw(g);
		//enemyTank.draw(g);
		g.setColor(c);
	}

	// 双缓冲，削弱闪烁现象	
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
	
	// 键盘监听的类
	private class keyMonitor extends KeyAdapter {
		
		@Override
		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
			//myTank.move();   //draw()方法中已经调用过move()，此处不能再次调用，否则坦克会有双倍的速度
		}

		@Override
		public void keyPressed(KeyEvent e) {
			myTank.keyPressed(e);
			//myTank.move();  //draw()方法中已经调用过move()，此处不能再次调用，否则坦克会有双倍的速度
		}
		
	}

	// 实现Runnable接口，从而可以从launch中启动一个线程
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
	
	// 画界面，键盘监听
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
		
		addKeyListener(new keyMonitor());	// 键盘监听
		
		new Thread(new PaintThread()).start();;	// 添加画界面的线程
		/**
		 * 客户端连接到服务器端
		 */
		aNetClient.connect("127.0.1.1", TankServer.TCP_PORT);
	}
	
	
	public static void main(String[] args){
		TankClient aTankClient = new TankClient();
		aTankClient.launch();
	}
	
	class ConnectDialog extends Dialog {
		Button aButton = new Button("确定");
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
