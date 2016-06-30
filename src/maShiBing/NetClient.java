
package maShiBing;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * @author Administrator
 * 用于连接服务器端的客户端的类
 */
public class NetClient {
	TankClient aTankClient;
	private Random aRandom = new Random();
	private int udpPort = 12345;
	
	DatagramSocket aDatagramSocket = null;
	
	public NetClient(TankClient aTankClient) {
		udpPort += aRandom.nextInt(50000);
		this.aTankClient = aTankClient;
		try {
			aDatagramSocket = new DatagramSocket(udpPort);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void connect(String ip, int port) {
		Socket aSocket = null;
		try {
			aSocket = new Socket(ip, port);
			DataOutputStream aDataOutputStream = new DataOutputStream(aSocket.getOutputStream());
			aDataOutputStream.writeInt(udpPort);
			DataInputStream aDataInputStream = new DataInputStream(aSocket.getInputStream());
			int id = aDataInputStream.readInt();
			aTankClient.myTank.id = id;
			if(id%2 == 0) aTankClient.myTank.good = false;
			else aTankClient.myTank.good = true;
			System.out.println("connected to server. Sever gived ID: " + id);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				aSocket.close();
				aSocket = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		NewTankMessage aNewTankMessage = new NewTankMessage(aTankClient.myTank);
		send(aNewTankMessage);
		
		new Thread(new UDPRecieveThread()).start();
	}
	
	/**
	 * 调用NewTankMessage类将Tank的数据发送到服务器端
	 * @param aNewTankMessage	NewTankMessage类的对象
	 */
	public void send(Message aMessage) {
		aMessage.send(aDatagramSocket, "127.0.0.1", TankServer.UDP_PORT);
	}
	
	/**
	 * 用于接收服务器端发送的UDP包数据的类
	 * @author Administrator
	 *
	 */
	private class UDPRecieveThread implements Runnable {

		byte[] buffers = new byte[1024];
		@Override
		public void run() {
			while( aDatagramSocket != null) {
				DatagramPacket aDatagramPacket = new DatagramPacket(buffers, buffers.length);
				try {
					aDatagramSocket.receive(aDatagramPacket);
					parse(aDatagramPacket);
System.out.println("a package received from server.");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		/**
		 * 解析接收到的服务器端的数据，调用NewTankMessage解析自己类型的数据更为方便
		 * @param aDatagramPacket	UDP数据包的实例
		 * @throws IOException 
		 */
		private void parse(DatagramPacket aDatagramPacket) throws IOException {
			ByteArrayInputStream aByteArrayInputStream = new ByteArrayInputStream(buffers, 0, aDatagramPacket.getLength());
			DataInputStream aDataInputStream = new DataInputStream(aByteArrayInputStream);
			int messageType = aDataInputStream.readInt();
			Message aMessage = null;
			switch (messageType) {
			case Message.NEW_TANK_MESSAGE:				
				aMessage = new NewTankMessage(NetClient.this.aTankClient);
				aMessage.parse(aDataInputStream);
				break;
			case Message.TANK_MOVE_MESSAGE:
				aMessage = new TankMoveMessage(NetClient.this.aTankClient);
				aMessage.parse(aDataInputStream);
				break;
			case Message.NEW_MISSILE_MESSAGE:
				aMessage = new MissileMessage(NetClient.this.aTankClient);
				aMessage.parse(aDataInputStream);
				break;
			}
			
		}		
	}
}
