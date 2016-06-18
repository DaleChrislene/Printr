import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Date;


public class Printer {
	private static final int PAGE_WIDTH = 200;
	private static final int BYTE_ARR_SIZE = 2000;
	private static final int EXTRA_READ = 2;
	private static final String TAB_SPACE = "    ";
	
	public static void main(String[] args) throws IOException{
		
		test();
		
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
		allBuff = allBuff.replaceAll("\r\n", "\n");
		
		processLineBulk(allBuff.toCharArray());
	}
	
	public static void processLineBulk(char[] cbuf){
		int cursor = 0, cursorEnd = 0, offset = 0;
		while(cursor + PAGE_WIDTH + EXTRA_READ < cbuf.length){
			char[] targetRange = Arrays.copyOfRange(cbuf, cursor, cursor + PAGE_WIDTH);
			
			//starting with spaces
			int leadingSpaceCount = countLeadingSpaces(targetRange);
			cursor += leadingSpaceCount;
			
			cursorEnd = cursor + PAGE_WIDTH;
			targetRange = Arrays.copyOfRange(cbuf, cursor, cursorEnd);
			
			//newlines
			int newLinePos = findNewLinePos(targetRange);
			if(newLinePos != -1){
				targetRange = Arrays.copyOfRange(cbuf, cursor, cursor + newLinePos);
				cursor += newLinePos;
				lineCutter(targetRange, false);
				continue;
			}
			
			boolean isWordSplit = checkWordSplit2(cbuf[cursorEnd-1], cbuf[cursorEnd], cbuf[cursorEnd+1]);
			offset = lineCutter(targetRange, isWordSplit);
			cursor += PAGE_WIDTH - offset + 1;
		}
	}
	
	private static int findNewLinePos(char[] cbuf){
		int pos = -1;
		
		String line = String.valueOf(cbuf);
		pos = line.indexOf("\n");
		
		return pos;
	}
	
	private static int countLeadingSpaces(char[] cbuf){
		int ctr = 0;
		
		int maxValidCheck = cbuf.length > PAGE_WIDTH ? PAGE_WIDTH : cbuf.length;
		for(int idx = 0; idx < maxValidCheck; idx++){
			if(cbuf[idx] == ' ' || cbuf[idx] == '\n'){
				ctr++;
			}
			else{
				break;
			}
		}
		
		return ctr;
	}
	
	public static boolean checkWordSplit2(char left, char middle, char right){
		boolean isSplit = false;
		if(isAlpha(middle)){
			if(isAlpha(left)){
				isSplit = true;//pp
			}
			else if(left == ' ' && isAlpha(right)){//_p_
				isSplit = true;
			}
		}
		
		return isSplit;
	}
	
	public static boolean checkWordSplit(char[] cbuf){
		boolean isSplit = false;
		
		int maxLen = cbuf.length;
		char middle = cbuf[maxLen - 2];
		char left = cbuf[maxLen - 3];
		char right = cbuf[maxLen - 1];
		
		if(isAlpha(middle)){
			if(isAlpha(left)){
				isSplit = true;//pp
			}
			else if(left == ' ' && isAlpha(right)){//_p_
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
	public static int lineCutter(char[] cbuf, boolean isWordSplit){
		int offset = 0;
		
		
		if(isWordSplit){
			int cutPos = findEndPos(cbuf);
			String line = String.valueOf(cbuf).substring(0,cutPos);
			System.out.println(line.trim());// no need to append \n since I'm using sysout
			
			offset = String.valueOf(cbuf).length() - cutPos;
			
		}else{
			String line = String.valueOf(cbuf);
			System.out.println(line.trim());
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
