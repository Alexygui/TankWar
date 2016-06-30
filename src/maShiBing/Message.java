package maShiBing;

import java.io.DataInputStream;
import java.net.DatagramSocket;

public interface Message {
	public static final int NEW_TANK_MESSAGE = 1;
	public static final int TANK_MOVE_MESSAGE = 2;
	public static final int NEW_MISSILE_MESSAGE = 3;
	
	public void send(DatagramSocket aDatagramSocket, String ip, int udpPort);
	public void parse(DataInputStream aDataInputStream);
}
