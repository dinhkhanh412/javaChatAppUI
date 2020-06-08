package Controller;



import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.io.IOException;


public class ChatClient implements Runnable {
	private String name;
	private InputStream in;
	private OutputStream out;
	private Socket socket;
	private LoginController loginController;
	private ChatUIController chatUIController;
	
	class Control {
		private volatile boolean loginStatus = false;
		private boolean disconnect = false;
		private boolean reLogin = false;
	}
	
	final Control control = new Control();
	
	public ChatClient(String name, InputStream in, OutputStream out, Socket socket) {
		this.name = name;
		this.in = in;
		this.out = out;
		this.socket = socket;
	}
	
	public String getName() { return name; }

	public void setLoginController(LoginController loginController){
		this.loginController = loginController;
		this.loginController.setChatClient(this);
	}


	public Socket getSocket() { return socket; }
	public InputStream getInputStream() { return in; }
	public OutputStream getOutputStream() { return out; }
	public boolean isLogin() { return control.loginStatus; }
	public void loginSuccess() {control.loginStatus = true; }
	public boolean reLogin() {return control.reLogin; }
	public void reLoginRequest() {control.reLogin = true; }
	public void reLoginDone() {control.reLogin = false; }
	private boolean disconnect = false;
	public void setName(String _name) {name = _name;}
	public void disconnect() {
		control.loginStatus = false;
		control.disconnect = true;
		try{
			socket.close();
		} catch (IOException e) {
			System.out.println("Not Disconnected");
		}
	}
	
	public boolean hasDisconnected() {
		return control.disconnect;
	}

	private SendThread send;
	private ReceiveThread recv;

	public SendThread getSend() { return send;}
	public ReceiveThread getRecv() {return recv;}

	@Override
	public void run() {
		send = new SendThread(out, this);
		recv = new ReceiveThread(in, this);

		send.setLoginController(this.loginController);
		recv.setLoginController(this.loginController);
		Thread sender = new Thread(send);
		Thread receiver = new Thread(recv);
		sender.start();
		receiver.start();
	}
}
