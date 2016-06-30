package maShiBing;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class MissileMessage implements Message{
	int messageType = Message.NEW_MISSILE_MESSAGE;
	Missile aMissile;
	TankClient aTankClient;
	int tankID;
	
	public MissileMessage(TankClient aTankClient) {
		this.aTankClient = aTankClient;
	}

	public MissileMessage(Missile aMissile) {
		this.aMissile = aMissile;
	}

	@Override
	public void send(DatagramSocket aDatagramSocket, String ip, int serverUDPPort) {
		ByteArrayOutputStream aByteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream aDataOutputStream = new DataOutputStream(aByteArrayOutputStream);
		try {
			aDataOutputStream.writeInt(messageType);
			aDataOutputStream.writeInt(aMissile.tankID);
			aDataOutputStream.writeInt(aMissile.x);
			aDataOutputStream.writeInt(aMissile.y);
			aDataOutputStream.writeInt(aMissile.missileDirection.ordinal());
			aDataOutputStream.writeBoolean(aMissile.good);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		byte[] buffers = aByteArrayOutputStream.toByteArray();
		DatagramPacket aDatagramPacket = new DatagramPacket(buffers, buffers.length, new InetSocketAddress(ip, serverUDPPort));
		try {
			aDatagramSocket.send(aDatagramPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void parse(DataInputStream aDataInputStream) {
		try {			
			int tankID = aDataInputStream.readInt();
			if(tankID == aTankClient.myTank.id) {	return;  }
			int x = aDataInputStream.readInt();
			int y = aDataInputStream.readInt();
			Direction missileDirection = Direction.values()[aDataInputStream.readInt()];
			boolean good = aDataInputStream.readBoolean();
			Missile aMissile = new Missile(tankID, x, y, missileDirection, aTankClient, good);
			aTankClient.missiles.add(aMissile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
