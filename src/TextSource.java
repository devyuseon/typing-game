import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Vector;
import java.util.Scanner;

public class TextSource {
	private Vector<String> v = new Vector<String>();
	private String lang = "en";
	
	public TextSource() {
		
	}
	
	public TextSource(String lang) { // 파일에서 읽기
		this.lang = lang;
		readFile(lang);
	}
	
	public void readFile(String lang) {
		String fileName;
		String word;
		
		// 선택한 언어에 따라 다른 txt파일 읽어온다
		if ("ko".equals(lang)) {
			fileName = "ko.txt";
			try {
				String line;
				// 한글 깨지는 것 때문에 UTF-8로 인코딩
				BufferedReader br = new BufferedReader(
						new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
				while((line = br.readLine()) != null) {
					word = line.trim(); // 양옆 White space 제거
					v.add(word);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}	        
		}
		else {
			fileName = "en.txt";
			try {
				System.out.println("영타");
				Scanner fscanner = new Scanner(new FileReader(fileName));
				while(fscanner.hasNext()) {
					word = fscanner.nextLine();
					v.add(word);
				}
				fscanner.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public String get(String lang) {
		int index = (int)(Math.random()*v.size());
		return v.get(index);
	}
}
