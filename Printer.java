import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;


public class Printer {
	private static int PAGE_WIDTH = 100;
	private static int BYTE_ARR_SIZE = 2000;
	private static final int EXTRA_READ = 2;
	private static final String TAB_SPACE = "    ";
	
	private static char[] remainChar = new char[0];
	
	public static void main(String[] args){
		
		Scanner sc = new Scanner(System.in);
		try{
			System.out.println("Enter page width (in bytes: e.g. 50/100): ");
			PAGE_WIDTH = sc.nextInt();
			sc.nextLine();
			System.out.println("Enter file name:");
			String fileName = sc.nextLine();
			sc.close();
			printBook(fileName);
		}catch(InputMismatchException e){
			System.out.println("Please enter a valid input");
		}
		
	}

	/**
	 * prints the input from fileName
	 * as a book
	 * 
	 * @param fileName
	 */
	private static void printBook(String fileName){
		char[] cbuf = null;
		try{
			FileReader fis = new FileReader(new File(fileName));
			int numCharRead = 0;
			
	
			do{
				cbuf = new char[BYTE_ARR_SIZE];
				numCharRead = fis.read(cbuf,0,BYTE_ARR_SIZE);
				
				boolean finalRead = false; 
				if(numCharRead < BYTE_ARR_SIZE){
					finalRead = true;
					cbuf = Arrays.copyOfRange(cbuf, 0, numCharRead);
				}
				if(remainChar.length != 0){
					cbuf = prefixArr(cbuf);
				}
				
				cbuf = processSpaces(cbuf);
				int errState = processLineBulk(cbuf, finalRead);
				if(errState == -1){
					System.out.println("Ending Program");
					break;
				}
			}while(numCharRead == BYTE_ARR_SIZE);
			
			fis.close();
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("Program Error!");
		}
	}
	
	/**
	 * prefixes the remaining bytes unprinted from previous
	 * cycle
	 * 
	 * @param cbuf
	 * @return
	 */
	private static char[] prefixArr(char[] cbuf) {
		int cbufLen = cbuf.length;
		int remArrLen = remainChar.length;
		
		char[] concatArr = new char[cbufLen + remArrLen];
		System.arraycopy(remainChar, 0, concatArr, 0, remArrLen);
        System.arraycopy(cbuf, 0, concatArr, remArrLen, cbufLen);
        
        return concatArr;
	}
	
	/**
	 * Converts Tabs and carriage returns to spaces
	 * and "\n" respectively
	 * 
	 * @param cbuf
	 * @return
	 */
	private static char[] processSpaces(char[] cbuf){
		String allBuff = String.valueOf(cbuf);
		allBuff = allBuff.replaceAll("\t", TAB_SPACE);
		allBuff = allBuff.replaceAll("\r\n", "\n");
		
		return allBuff.toCharArray();
	}
	
	/**
	 * Processes and prints the bytes read from the
	 * input stream
	 * 
	 * @param cbuf
	 * @param finalRead
	 * @return
	 */
	private static int processLineBulk(char[] cbuf, boolean finalRead){
		int cursor = 0, cursorEnd = 0, offset = 0;
		int errState = 0;
		while(cursor + PAGE_WIDTH + EXTRA_READ < cbuf.length){
			if(errState == -1){
				return errState;
			}
			
			cursorEnd = cursor + PAGE_WIDTH;
			char[] targetRange = Arrays.copyOfRange(cbuf, cursor, cursorEnd);
			
			int leadingSpaceCount = countLeadingSpaces(targetRange);
			cursor += leadingSpaceCount;
			cursorEnd = cursor + PAGE_WIDTH > cbuf.length ? cbuf.length : cursor + PAGE_WIDTH;
			targetRange = Arrays.copyOfRange(cbuf, cursor, cursorEnd);
			
			int newLinePos = findNewLinePos(targetRange);
			if(newLinePos != -1){
				targetRange = Arrays.copyOfRange(cbuf, cursor, cursor + newLinePos);
				cursor += newLinePos;
				errState = lineCutter(targetRange, false);
				continue;
			}
			
			boolean isWordSplit = false;
			if(cursorEnd + 1 < cbuf.length){
				isWordSplit = checkWordSplit(cbuf[cursorEnd-1], cbuf[cursorEnd], cbuf[cursorEnd+1]);
			}
			
			offset = lineCutter(targetRange, isWordSplit);
			cursor += PAGE_WIDTH - offset + 1;
			
			errState = offset;
		}
		
		if(finalRead && cursor < cbuf.length){
			while(cursor < cbuf.length){
				char[] targetRange = Arrays.copyOfRange(cbuf, cursor, cbuf.length);
				int leadingSpaceCount = countLeadingSpaces(targetRange);
				cursor += leadingSpaceCount;
				
				targetRange = Arrays.copyOfRange(cbuf, cursor, cbuf.length);
				int newLinePos = findNewLinePos(targetRange);
				if(newLinePos != -1){
					targetRange = Arrays.copyOfRange(cbuf, cursor, cursor + newLinePos);
					cursor += newLinePos;
					errState = lineCutter(targetRange, false);
					continue;
				}
				
				lineCutter(targetRange, false);
				cursor = cbuf.length;
			}
		}
		else if(cursor < cbuf.length){
			remainChar = Arrays.copyOfRange(cbuf, cursor, cbuf.length);
		}
		return errState;
	}

	private static int findNewLinePos(char[] cbuf){
		int pos = -1;
		
		String line = String.valueOf(cbuf);
		pos = line.indexOf("\n");
		
		return pos;
	}
	
	/**
	 * counts and returns the number of leading spaces " " or "\n"
	 * @param cbuf
	 * @return
	 */
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
	
	/**
	 * checks if word is split
	 * 
	 * @param left
	 * @param middle
	 * @param right
	 * @return
	 */
	private static boolean checkWordSplit(char left, char middle, char right){
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
	
	/**
	 * Checks if char is an aplhabet
	 * 
	 * 
	 * @param x
	 * @return
	 */
	private static boolean isAlpha(char x){
		x = String.valueOf(x).toUpperCase().charAt(0);
		if(x >= 'A' && x <= 'Z'){
			return true;
		}
		return false;
	}
	
	/**
	 * Splits sentences into max width
	 * 
	 * @param cbuf
	 * @param isWordSplit
	 * @return
	 */
	private static int lineCutter(char[] cbuf, boolean isWordSplit){
		int offset = 0;
		
		if(isWordSplit){
			int cutPos = findEndPos(cbuf);
			
			if(cutPos == -1){
				return -1;
			}
			
			String line = String.valueOf(cbuf).substring(0,cutPos);
			System.out.println(line.trim());// no need to append \n since I'm using sysout
			
			offset = String.valueOf(cbuf).length() - cutPos;
			
		}else{
			String line = String.valueOf(cbuf);
			System.out.println(line.trim());
		}
		
		return offset;
	}
	
	/**
	 * Find position of the split word
	 * 
	 * @param cbuf
	 * @return
	 */
	private  static int findEndPos(char[] cbuf){
		int pos = 0;
		
		try{
			for(int idx = cbuf.length - 1; idx < cbuf.length; idx--){
				if(cbuf[idx] == ' ' || cbuf[idx] == '\t' || cbuf[idx] == '\n'){
					return idx;
				}
			}
		}catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Assumption: Single Word cannot be greater than width of book");
			pos = -1;
		}
		
		return pos;
	}
}
