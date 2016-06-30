package maShiBing;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
/**
 * �ͻ������ڷ��ͺͽ�����Ϣ����
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
	 * ���ڽ��ͻ��˵�Tank��������ݷ��͵���������
	 * @param aDatagramSocket	Client�˵�UDP�׽���(DatagramSocket)
	 * @param ip	�������˵�IP��ַ
	 * @param serverUDPport	�������˵�UDP�˿�
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
		 * ��ByteArrayOutputStream��ת��Ϊbyte�����Ա���DatagramPacket������ֽڰ����ͳ�ȥ
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
	 * ���������˴�����������ݽ��н���
	 * @param aDataInputStream	�ӷ������˵õ����ݵ����Ĺܵ�
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
