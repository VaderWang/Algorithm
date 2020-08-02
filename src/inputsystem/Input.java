package inputsystem;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Input {
	
	public static final int EOF = 0;
	private final int MAXLOOK = 16;//lookahead��󳤶�
	private final int MAXLEX = 1024;//�ִʺ���󳤶�
	private final int BUFSIZE = (MAXLEX * 3) + (2 * MAXLOOK);
	private       int endBuf = BUFSIZE;//������������ַ
	private final int DANGER = (endBuf - MAXLOOK);
	private final int END = BUFSIZE;
	private final byte[] startBuf = new byte[BUFSIZE];
	
	private int next = END;//ָ��ǰҪ������ַ�λ��
	private int sMark = END;//��ǰ���ʷ��������������ַ���λ��
	private int eMark = END;//��ǰ���������ַ�������λ��
	private int pMark = END;//��һ�����������ַ���λ��
	private int pLineNum = 0;
	private int pLength = 0;
	
	private FileHandler fileHandler = null;
	
	private int lineNum = 1;//��ǰ�к�
	private int mLine = 1;
	
	public boolean EofRead = false;//�Ƿ��пɶ���Ϣ
	
	public Input() {
		;
	}
	
	public boolean noMoreChars() {
		return EofRead && (next >= endBuf);
	}
	
	private FileHandler getFileHandler(String fileName) {
		if (fileName != null) {
			return new DiskFileHandler(fileName);
		} else {
			return new StdinHandler();
		}
	}
	
	public void iNewFile(String fileName) {
		if(fileHandler != null) {
			fileHandler.close();
		}
		
		fileHandler = getFileHandler(fileName);
		fileHandler.open();
		
		EofRead = false;
		next     = END;
		pMark    = END;
		sMark    = END;
		eMark    = END;
		endBuf  = END;
		lineNum   = 1;
		mLine    = 1;
	}
	
	public String iText() {
		byte[] str = Arrays.copyOfRange(startBuf, sMark, sMark + iLength());
		return new String(str, StandardCharsets.UTF_8);
	}
	
	public int iLength() {
		return eMark - sMark;
	}
	
	public int iLineNum() {
		return lineNum;
	}
	
	public String iPText() {
		byte[] str = Arrays.copyOfRange(startBuf, pMark, pMark + pLength);
		return new String(str, StandardCharsets.UTF_8);
	}
	
	public int iPLength() {
		return pLength;
	}
	
	public int iPLineNum() {
		return pLineNum;
	}
	
	public int iMarkStart() {
		mLine = lineNum;
		eMark = sMark = next;
		return sMark;
	}
	
	public int iMarkend() {
		mLine = lineNum;
		eMark = next;
		return eMark;
	}
	
	public int iMoveStart() {
		if (sMark >= eMark) {
			return -1;
		}
		else {
			sMark++;
			return sMark;
		}
	}
	
	public int iToMark() {
		lineNum = mLine;
		next = eMark;
		return next;
	}
	
	public int iMarkPrev() {
		/*
		 * ִ�������������һ�����ʷ��������������ַ������޷��ڻ��������ҵ�
		 */
		pMark = sMark;
		pLineNum = lineNum;
		pLength = eMark - sMark;
		return pMark;
	}
	
	public byte iAdvance() {
		/*
		 * �ӻ�������ȡ�ַ���next��1�����Next��λ�þ��뻺�������߼�ĩβ(End_buf)����
		 * MAXLOOK ʱ�� ����Ի���������һ��flush ����
		 */
		if(noMoreChars()) {
			return 0;
		}
		
		//System.out.println(EofRead);
		//int i;
		if(EofRead == false && iFlush(false) < 0) {
			//System.out.println(i);
			return -1;
		}
		
		if(startBuf[next] == '\n') {
			lineNum++;
		}
		
		return startBuf[next++];
	}
	
	public static int NO_MORE_CHARS_TO_READ = 0;
	public static int FLUSH_OK = 1;
	public static int FLUSH_FAIL = -1;
	
	private int iFlush(boolean force) {
		
		int copy_amt, shift_amt, left_edge;
		if (noMoreChars()) {
			return NO_MORE_CHARS_TO_READ;
		}
		
		if (EofRead) {
			//�������Ѿ�û�ж�����Ϣ��
			return FLUSH_OK;
		}
		
		if (next > DANGER || force) {
			left_edge = pMark < sMark ? pMark : sMark;
			shift_amt = left_edge;
			if (shift_amt < MAXLEX) {
				if (!force) {
					return FLUSH_FAIL;
				}
				
				left_edge = iMarkStart();
				iMarkPrev();
				shift_amt = left_edge;
			}
			
			copy_amt = endBuf - left_edge;
			System.arraycopy(startBuf, 0, startBuf, left_edge, copy_amt);
			
			if (iFillBuf(copy_amt) == 0) {
				System.err.println("Internal Error, iFlush: Buffer full, can't read");
			}
			
			if (pMark != 0) {
				pMark -= shift_amt;
			}
			
			sMark -= shift_amt;
			eMark -= shift_amt;
			next  -= shift_amt;
		}
		
		return FLUSH_OK;
	}
	
	private int iFillBuf(int starting_at) {
		/*
		 * ���������ж�ȡ��Ϣ����仺����ƽ�ƺ�Ŀ��ÿռ䣬���ÿռ�ĳ����Ǵ�starting_atһֱ��End_buf
		 * ÿ�δ��������ж�ȡ�����ݳ�����MAXLEXд������
		 * 
		 */
		
		int need; //��Ҫ���������ж�������ݳ���
		int got = 0; //ʵ���ϴ��������ж��������ݳ���
		need = ((END - starting_at) / MAXLEX) * MAXLEX;
		if (need < 0) {
			System.err.println("Internal Error (iFillbuf): Bad read-request starting addr.");
		}
		
		if (need == 0) {
			return 0;
		}
		
		if ((got = fileHandler.read(startBuf, starting_at, need)) == -1) {
			System.err.println("Can't read input file");
		}
		
		endBuf = starting_at + got;
		//System.out.println(got + "  " + need);
		if (got < need) {
			//�������Ѿ���ĩβ
			EofRead = true;
		}
		
		return got;
	}

	public boolean iPushBack(int n) {
		/*
		 * ��Ԥ��ȡ�����ɸ��ַ��˻ػ�����
		 */
		while (--n >= 0 && next > sMark) {
			if (startBuf[--next] == '\n' || startBuf[next] == '\0') {
				--lineNum;
			}
		}
		
		if (next < eMark) {
			eMark = next;
			mLine = lineNum;
		}
		
		return (next > sMark);
	}
	
	public byte iLookAhead(int n) {
		/*
		 * Ԥ��ȡ���ɸ��ַ�
		 */
		byte p = startBuf[next + n - 1];
		if (EofRead && next + n - 1 >= endBuf) {
			return EOF;
		}
		
		return (next + n - 1 < 0 || next + n - 1 >= endBuf) ? 0 : p;
	}
}
