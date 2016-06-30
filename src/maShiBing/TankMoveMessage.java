package maShiBing;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankMoveMessage implements Message{
	int messageType = Message.TANK_MOVE_MESSAGE;
	int id, x, y;
	TankClient aTankClient;
	Direction aDirection;
	
	public TankMoveMessage(int id, int x, int y, Direction aDirection) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.aDirection = aDirection;
	}
	
	public TankMoveMessage(TankClient aTankClient) {
		this.aTankClient = aTankClient;
	}

	@Override
	public void send(DatagramSocket aDatagramSocket, String ip, int serverUDPport) {
		ByteArrayOutputStream aByteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream aDataOutputStream = new DataOutputStream(aByteArrayOutputStream);
		try {
			aDataOutputStream.writeInt(messageType);
			aDataOutputStream.writeInt(id);
			aDataOutputStream.writeInt(x);
			aDataOutputStream.writeInt(y);
			aDataOutputStream.writeInt(aDirection.ordinal());
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
	
	@Override
	public void parse(DataInputStream aDataInputStream) {
		try {
			int id = aDataInputStream.readInt();
			if(aTankClient.myTank.id == id) {	return;	}
			int x = aDataInputStream.readInt();
			int y = aDataInputStream.readInt();
			Direction aDirection = Direction.values()[aDataInputStream.readInt()];
System.out.println("id: " + id +"--x: " + x + "--y: " + y + "--Direction: " + aDirection);
			boolean exist = false;
			for(int i=0; i<aTankClient.tanks.size(); i++) {
				Tank aTank = aTankClient.tanks.get(i);
				if(aTank.id == id) {
					aTank.x = x;
					aTank.y = y;
					aTank.aDirection = aDirection;
					exist = true;
					break;
				}
				
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
