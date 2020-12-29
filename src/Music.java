import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.net.URISyntaxException;

import com.sun.tools.javac.Main;

import java.io.File;

import javazoom.jl.player.Player;

// ���� ��� ������
public class Music extends Thread{
	private Player player;
	private boolean isLoop;
	private File file = null;
	private FileInputStream fis;
	private BufferedInputStream bis;
	
	public Music(String name, boolean isLoop) {
		try {
			this.isLoop = isLoop;
			File file = new File(name);
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			player = new Player(bis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// ���� ����
	public void close() {
		isLoop = false;
		player.close();
		this.interrupt();
	}
	
	public void run() {
		try {
			do {
				player.play();
				fis = new FileInputStream(file);
				bis = new BufferedInputStream(fis);
				player = new Player(bis);
			} while(isLoop); // isLoop�� true�ε����� ���� ���
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
