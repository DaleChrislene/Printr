import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Date;


public class Printer {
	private static final int PAGE_WIDTH = 30;
	private static final int BYTE_ARR_SIZE = 2000;
	private static final int EXTRA_READ = 2;
	private static final String TAB_SPACE = "    ";
	
	public static void main(String[] args) throws IOException{
		
		//testCharBuf();
		test();
		//char[] arr = {'a','b','c'};
		//System.out.println(Arrays.copyOfRange(arr,1,2));
		
	}
	
	public static void testCharBuf() throws IOException{
		FileReader fis = new FileReader(new File("test.txt"));
		CharBuffer cb = CharBuffer.allocate(BYTE_ARR_SIZE);
		int numCharRead = fis.read(cb);
		
		Date start = new Date();
		StringWriter sw = new StringWriter(BYTE_ARR_SIZE);
		while(numCharRead >= BYTE_ARR_SIZE){
			
			sw.write(cb.array(), 0, numCharRead);
			
			System.out.println(sw.toString());
			System.out.println(checkWordSplit(cb.array()));
			cb.clear(); sw.getBuffer().setLength(0);
			numCharRead = fis.read(cb);
		}
		Date end = new Date();
		long timeDiff = end.getTime() - start.getTime();
		System.out.println("****");
		System.out.println("TIME: " + timeDiff);
		System.out.println(numCharRead);
		fis.close();
		
	}
	
	public static void test() throws IOException{
		char[] cbuf = new char[BYTE_ARR_SIZE];
		FileReader fis = new FileReader(new File("test2.txt"));
		int numCharRead = fis.read(cbuf);
		
		Date start = new Date();
		StringWriter sw = new StringWriter(BYTE_ARR_SIZE);
		while(numCharRead >= BYTE_ARR_SIZE || numCharRead > 0){
			    
				
			sw.write(cbuf, 0, numCharRead);
			processLinkBulkTabs(cbuf);
			sw.getBuffer().setLength(0);
			numCharRead = fis.read(cbuf, -1, 100);
		}
		Date end = new Date();
		long timeDiff = end.getTime() - start.getTime();
		System.out.println("****");
		System.out.println("TIME: " + timeDiff);
		System.out.println(numCharRead);
		fis.close();
	}
	
	public static void processLinkBulkTabs(char[] cbuf){
		
		String allBuff = String.valueOf(cbuf);
		allBuff = allBuff.replaceAll("\t", TAB_SPACE);
		char[] scanned = tabChanger(cbuf);
		
		processLineBulk(scanned);
	}
	
	public static void processLineBulk(char[] cbuf){
		int cursor = 0, offset = 0;
		for(int i=0; i < cbuf.length; i++){
			offset = lineCutter(Arrays.copyOfRange(cbuf, cursor, cursor + PAGE_WIDTH + EXTRA_READ));
			cursor += PAGE_WIDTH - offset + 1;
		}
	}
	
	public static boolean checkWordSplit(char[] cbuf){
		boolean isSplit = false;
		
		char lastChar = cbuf[PAGE_WIDTH];
		char prevChar = cbuf[PAGE_WIDTH - 1];
		char nextChar = cbuf[PAGE_WIDTH + 1];
		
		if(isAlpha(lastChar)){
			if(isAlpha(prevChar)){
				isSplit = true;//pp
			}
			else if(prevChar == ' ' && isAlpha(nextChar)){//_p_
				isSplit = true;
			}
		}
		
		return isSplit;
	}
	
	private static boolean isAlpha(char x){
		x = String.valueOf(x).toUpperCase().charAt(0);
		if(x >= 'A' && x <= 'Z'){
			return true;
		}
		return false;
	}
	
	private static char[] tabChanger(char[] cbuf){
		String line = String.valueOf(cbuf);
		line = line.replaceAll("\t", TAB_SPACE);
		return line.toCharArray();
	}
	
	//takes in cbuf[101]
	public static int lineCutter(char[] cbuf){
		int offset = 0;
		boolean isWordSplit = checkWordSplit(cbuf);
		
		cbuf = Arrays.copyOf(cbuf, cbuf.length - EXTRA_READ);
		if(isWordSplit){
			int cutPos = findEndPos(cbuf);
			String line = String.valueOf(cbuf).substring(0,cutPos);
			System.out.println(line);// no need to append \n since I'm using sysout
			
			offset = String.valueOf(cbuf).length() - cutPos;
			
		}else{
			System.out.println(cbuf);
		}
		
		return offset;
	}
	
	public  static int findEndPos(char[] cbuf){
		int pos = 0;
		
		for(int idx = cbuf.length - 1; idx < cbuf.length; idx--){
			if(cbuf[idx] == ' ' || cbuf[idx] == '\t' || cbuf[idx] == '\n'){
				return idx;
			}
		}
		
		return pos;
	}
}
