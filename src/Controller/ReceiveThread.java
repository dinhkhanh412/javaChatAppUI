package Controller;

import javax.print.DocFlavor;
import java.io.*;

public class ReceiveThread implements Runnable {
	private InputStream inputStream;
	private BufferedReader bf;
	private ChatClient client;
	private LoginController loginController;
	private ChatUIController chatUIController;
	Message message = new Message();
	String msg = "";

	public void setLoginController(LoginController loginController){
		this.loginController = loginController;
		loginController.setReceiver(this);
	}

	public void setChatUIController(ChatUIController chatUIController) {
		this.chatUIController = chatUIController;
	}

	public ReceiveThread(InputStream in, ChatClient client) {
		this.inputStream = in;
		this.client = client;
//		bf = new BufferedReader(new InputStreamReader(in));
	}

	private byte[] data;

	@Override 
	public void run() {
		while (client.getSocket().isConnected()) {
			data = null;
			try {
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();

				int count = 0;
				int sumRead = 0;
				byte[] data = new byte[1048576];
				int tmp = 0;
				while (inputStream.available() > 0) {
					tmp = inputStream.read();
					data[count] = (byte)tmp;
					count+=1;
				}
				if (count == 0) continue;
				buffer.write(data, 0, count);
				System.out.println(count);
				byte[] res = buffer.toByteArray();
				handleData(res);
				data = null;
				buffer.flush();
			} catch (IOException e) {
				continue;
			}

		}
	}

	public void handleData(byte[] data) {
		System.out.println(data.length);
		String line, msg;
		byte[] cloneData;
		cloneData = data.clone();
		InputStream is = new ByteArrayInputStream(data);
		bf = new BufferedReader(new InputStreamReader(is));
		if (!client.isLogin()) {
			msg = "";
			try {
				if((line = bf.readLine()).equals("<start>")) {
					while (!((line = bf.readLine()).equals("<end>"))) {
						msg += line + "\n";
					}
					message = new Message(msg);
					handleMsg(message);
					if (client.isLogin()) return;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			if (client.hasDisconnected()) return;
			msg = "";
			try {
				while ((line = bf.readLine()) == null) ;
				if(line.equals("<start>")) {
					boolean isHeaderEnd = false;
					boolean hasFile = false;
					byte[] fileContent = null;
					try {
						while (!((line = bf.readLine()).equals("<end>"))) {
							if (!isHeaderEnd && line.contains("FILE")) {
								hasFile = true;
							}
							int length;
							if (isHeaderEnd && hasFile) {
								length = Integer.parseInt(line);
								msg += line + "\n"; // length of file
								line = bf.readLine();
								msg += line + "\n"; // Name of file+

								fileContent = new byte[(int) length];
								int startPos = data.length - length - 8;
								System.out.println(data.length);
								for (int i = 0; i < length; i++) {
									fileContent[i] = cloneData[startPos + i];
								}
								System.out.println(fileContent[0]);
								try (FileOutputStream fout = new FileOutputStream("/home/khanh/Desktop/hello.png")) {
									fout.write(fileContent, 0, fileContent.length);
								}
								break;
							}
							if (line.equals("")) {
								isHeaderEnd = true;
							}
							if (fileContent == null) msg += line + "\n";
						}
					}
					catch (IOException e) {
						client.disconnect();
						return;
					}
					if (fileContent != null) message.createNew(msg, fileContent);
					else message.createNew(msg);
//					System.out.println(message.getBody());
					handleMsg(message);
				}
			} catch (IOException e) {
				client.disconnect();
				return;
			}
		}
	}
	
	public String getMsg() {
		return msg;
	}

	public void handleMsg(Message msg) throws FileNotFoundException {
		if (msg.getMethod().equals("NOTI")){
			if (message.getMethod().equals("NOTI")) {
				if (message.getCommand().equals("200")) {
					this.client.loginSuccess();
					this.client.setName(message.getReceiver());
//					System.out.println("Name: "+ this.client.getName());
					try {
						Reader inputString = new StringReader(message.getBody());
						BufferedReader rd = new BufferedReader(inputString);
						String line = rd.readLine();
						if (line.equals("Online:")) {
							while((line=rd.readLine()) != null) {
//								System.out.println("fr: " + line);
								chatUIController.online(line);
							}
						}
					} catch (IOException e) {
						System.out.println("Not good");
					}

//					System.out.println("New notification:\n" + message.getBody() + "From: " + msg.getSender()+ "\n");
					return;
				}
				if (message.getCommand().equals("ONL")) {
					Reader inputString = new StringReader(message.getBody());
					BufferedReader rd = new BufferedReader(inputString);
					try {
						String line = rd.readLine();
						chatUIController.online(line);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}
				if (message.getCommand().equals("OFF")) {
					Reader inputString = new StringReader(message.getBody());
					BufferedReader rd = new BufferedReader(inputString);
					try {
						String line = rd.readLine();
						chatUIController.offline(line);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}
				if (message.getCommand().equals("NEW_GR")) {
					Reader inputString = new StringReader(message.getBody());
					BufferedReader rd = new BufferedReader(inputString);
					try {
						String line = rd.readLine();
						chatUIController.newGr(line);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}
				if (message.getCommand().equals(("FAIL"))) {
					client.reLoginRequest();
//					System.out.println(message.getBody() + "\n");
					return;
				}
			}
		}
		else if (msg.getMethod().equals("RECV")) {
			if (msg.getCommand().equals("MSG")){
				chatUIController.receiveMess(message.getBody(), message.getSender(), false);
//				System.out.println("[" + message.getSender() + "]: " + message.getBody());
				return;
			}
			if (msg.getCommand().equals("GROUP")) {
				chatUIController.receiveMess(message.getSender() + ": " + message.getBody(), message.getReceiver(), true);
//				System.out.println("[" + message.getSender() + " to " + message.getReceiver() + "]: " + message.getBody());
				return;
			}
			if (msg.getCommand().equals("FILE")){
				try {
					byte[] fileContent = msg.getFileContent();
					try (FileOutputStream fos = new FileOutputStream("/home/khanh/Desktop/hello.png")) {
   						fos.write(fileContent);
						System.out.println("[" + message.getSender() + "]: " + message.getBody());
   						fos.close(); 
						// There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
					}
					inputStream.close();
				} catch (IOException e) {
					System.out.println("Not good");
				}
				return;
			}
		}
	}
}
