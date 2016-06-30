package maShiBing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
/**
 * @author 
 * �������˵ĳ���
 */
public class TankServer {
	/**
	 * ����������˵�TCP�˿ڵĳ���
	 */
	public static final int TCP_PORT = 58868;
	/**
	 * ����������˵�UDP�˿ڵĳ���
	 */
	public static final int UDP_PORT = 58888; 
	private static int client_ID = 100;
	
	List<Client> clients = new ArrayList<Client>();
	
	public void start() {
		
		new Thread(new UDPThread()).start();
		
		ServerSocket aServerSocket = null;
		try {
			aServerSocket = new ServerSocket(TCP_PORT);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		Socket aSocket =null;
		try {
			while(true) {
				aSocket = aServerSocket.accept();
				DataInputStream aDataInputStream = new DataInputStream(aSocket.getInputStream());
				int clientUDPport = aDataInputStream.readInt();
				String ip = aSocket.getInetAddress().getHostAddress();
				Client aClient = new Client(ip, clientUDPport);
				clients.add(aClient);
				DataOutputStream aDataOutputStream = new DataOutputStream(aSocket.getOutputStream());
				aDataOutputStream.writeInt(client_ID++);
				
System.out.println("A client connet. Address- " + aSocket.getInetAddress() + ":" + aSocket.getPort()+ "-----UDP prot: " + clientUDPport);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if( aSocket != null) {
				try {
					aSocket.close();
					aSocket = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		new TankServer().start();
	}
	
	private class Client {
		String ip;
		int clientUDPport;
		
		public Client(String ip, int clientUDPport) {
			this.clientUDPport = clientUDPport;
			this.ip = ip;
		}

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}
		
		
	}

	/**
	 * ���ڽ��տͻ������ݵ�UDP�߳�
	 * @author Administrator
	 *
	 */
	private class UDPThread implements Runnable {

		/**
		 * �洢���յ������ݵ�����
		 */
		byte[] buffers = new byte[1024];
		
		@Override
		public void run() {
			DatagramSocket aDatagramSocket = null;
				try {
					aDatagramSocket = new DatagramSocket(UDP_PORT);
				} catch (SocketException e) {
					e.printStackTrace();
				}
System.out.println("UDP thread started at port: " + UDP_PORT);
			while(aDatagramSocket != null) {
				/**
				 * ���ڽ������ݵı�����Ȼ��洢��buffers������
				 */
				DatagramPacket aDatagramPacket = new DatagramPacket(buffers, buffers.length);
				try {
					aDatagramSocket.receive(aDatagramPacket);
					for(int i=0; i<clients.size(); i++) {
						Client aClient = clients.get(i);
						aDatagramPacket.setSocketAddress(new InetSocketAddress(aClient.getIp(), aClient.clientUDPport));
						aDatagramSocket.send(aDatagramPacket);
					}
System.out.println("a package recieved.");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
}
