package maShiBing;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
/**
 * 客户端用于发送和接受消息的类
 * @author Administrator
 *
 */
public class NewTankMessage implements Message{
	int messageType = Message.NEW_TANK_MESSAGE;
	
	Tank aTank;
	TankClient aTankClient;
	
	public NewTankMessage(Tank aTank) {
		this.aTank = aTank;
	}
	
	public NewTankMessage(TankClient aTankClient) {
		this.aTankClient = aTankClient;
	}

	public NewTankMessage() {
		
	}

	/**
	 * 用于将客户端的Tank对象的数据发送到服务器端
	 * @param aDatagramSocket	Client端的UDP套接字(DatagramSocket)
	 * @param ip	服务器端的IP地址
	 * @param serverUDPport	服务器端的UDP端口
	 */
	public void send(DatagramSocket aDatagramSocket, String ip, int serverUDPport) {
		ByteArrayOutputStream aByteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream aDataOutputStream = new DataOutputStream(aByteArrayOutputStream);
		try {
			aDataOutputStream.writeInt(messageType);
			aDataOutputStream.writeInt(aTank.id);
			aDataOutputStream.writeInt(aTank.x);
			aDataOutputStream.writeInt(aTank.y);
			aDataOutputStream.writeInt(aTank.aDirection.ordinal());
			aDataOutputStream.writeBoolean(aTank.good);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/**
		 * 将ByteArrayOutputStream流转换为byte数组以便用DatagramPacket打包成字节包发送出去
		 */
		byte[] buffers = aByteArrayOutputStream.toByteArray();
		DatagramPacket aDatagramPacket = new DatagramPacket(buffers, buffers.length, new InetSocketAddress(ip, serverUDPport));
		try {
			aDatagramSocket.send(aDatagramPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将服务器端传输过来的数据进行解析
	 * @param aDataInputStream	从服务器端得到数据的流的管道
	 */
	public void parse(DataInputStream aDataInputStream) {
		try {
			int id = aDataInputStream.readInt();
			if(aTankClient.myTank.id == id) {	return;	}

			int x = aDataInputStream.readInt();
			int y = aDataInputStream.readInt();
			Direction aDirection = Direction.values()[aDataInputStream.readInt()];
			boolean good = aDataInputStream.readBoolean();
System.out.println("id: " + id +"--x: " + x + "--y: " + y + "--Direction: " + aDirection + "--good: " + good);
			boolean exist = false;
			for(int i=0; i<aTankClient.tanks.size(); i++) {
				Tank aTank = aTankClient.tanks.get(i);
				if(aTank.id == id) {
					exist = true;
					break;
				}
			}
			if(!exist) {

				NewTankMessage aNewTankMessage = new NewTankMessage(aTankClient.myTank);
				aTankClient.aNetClient.send(aNewTankMessage);
				
				Tank aTank = new Tank(x, y, good, aTankClient, aDirection);
				aTank.id = id;
				if(aDirection != Direction.STOP) aTank.barrelDirection = aDirection;
				aTankClient.tanks.add(aTank);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
