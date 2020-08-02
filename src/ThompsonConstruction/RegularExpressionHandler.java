package ThompsonConstruction;

import java.util.ArrayList;

import inputsystem.Input;

public class RegularExpressionHandler {
	
	private Input input = null;
	private MacroHandler macroHandler = null;
	ArrayList<String> regularExprArr = new ArrayList<String>();
	private boolean inquoted = false; 
	
	public RegularExpressionHandler(Input input, MacroHandler macroHandler) throws Exception {
		this.input = input;
		this.macroHandler = macroHandler;
		
		processRegularExprs();
	}
	
	public int getRegularExpressionCount() {
		return regularExprArr.size();
	}
	
	public String getRegularExpression(int index) {
		if (index < 0 || index >= regularExprArr.size()) {
			return null;
		}
		
		return regularExprArr.get(index);
	}
	
	private void processRegularExprs() throws Exception {
		while (input.iLookAhead(1) != Input.EOF) {
			preProcessExpr();
		}
	}
	
	public void preProcessExpr() throws Exception {
		/*
		 * ��������ʽ����Ԥ���������ʽ�еĺ�����滻������
		 * D*\.D Ԥ��������
		 * [0-9]*\.[0-9]
		 * ע�⣬���ǿ��Լ��׵ģ����Ժ��滻ʱҪע�⴦����׵�����
		 */
		
		//ȥ����ͷ�Ŀո�����
		while (Character.isSpaceChar(input.iLookAhead(1)) || input.iLookAhead(1) == '\n') {
			input.iAdvance();
		}
		
		String regularExpr = "";
		char c = (char) input.iAdvance();
		while (Character.isSpaceChar(c) == false && c != '\n') {
			if (c == '"') {
				//�жϵ�ǰ�ַ��Ƿ���˫������
				inquoted = !inquoted;
			}
			
			if (!inquoted && c == '{') {
				String name = extracMacroNameFromInput();
				regularExpr += expandMacro(name);
			}
			else {
				regularExpr += c;
			}
			
			
			c = (char) input.iAdvance();
		}
		
		regularExprArr.add(regularExpr);
	}
	
	public String expandMacro(String macroName) throws Exception {
		String macroContent = macroHandler.expandMacro(macroName);
		int begin = macroContent.indexOf('{');
		while (begin != -1) {
			int end = macroContent.indexOf('}', begin);
			if (end == -1) {
				ErrorHandler.parseErr(ErrorHandler.Error.E_BADMAC);
				return null;
			}
			
			boolean inquoted = checkInQuoted(macroContent, begin, end);
			
			if (inquoted == false) {
			    macroName = macroContent.substring(begin+1, end);
			    String content = macroContent.substring(0, begin);
			    content += macroHandler.expandMacro(macroName);
			    content += macroContent.substring(end+1, macroContent.length());
			    macroContent = content;
			    //������滻���滻�����ݻ��к궨�壬��ô�����滻��ֱ�����к궼�滻��Ϊֹ
			    begin = macroContent.indexOf('{');
			}
			else {
				begin = macroContent.indexOf('{', end);
			}
			
		}
		
		return macroContent;
	}
	
	public boolean checkInQuoted(String macroContent, int curlyBracesBegin, int curlyBracesEnd) throws Exception {
		/*
		 * �Ȳ��Ҿ��� { �����һ�� ˫����
		 * Ȼ����ҵڶ���˫����
		 * ���˫����{}������˫����֮��
		 * ��ôinquoted����Ϊ true
		 */
		boolean inquoted = false;
		int quoteBegin = macroContent.indexOf('"');
		int quoteEnd = - 1;
		
	    while (quoteBegin != -1) {
	    	
	    	quoteEnd = macroContent.indexOf('"', quoteBegin + 1);
			if (quoteEnd == -1) {
				ErrorHandler.parseErr(ErrorHandler.Error.E_BADMAC);
			}
			
			if (quoteBegin < curlyBracesBegin && quoteEnd > curlyBracesEnd) {
				inquoted = true;
			}
			else if (quoteBegin < curlyBracesBegin && curlyBracesEnd < quoteEnd){
				/*
				 * "{" ... } 
				 * �����Ų�ƥ��
				 */
				ErrorHandler.parseErr(ErrorHandler.Error.E_BADMAC);
			}
			else if (quoteBegin > curlyBracesBegin && quoteEnd < curlyBracesEnd) {
				/*
				 * {...."}" 
				 * �����Ų�ƥ��
				 */
				ErrorHandler.parseErr(ErrorHandler.Error.E_BADMAC);
			}
			
			quoteBegin = macroContent.indexOf('"', quoteEnd + 1);
	    }
		
		return inquoted;
	}

	public String extracMacroNameFromInput() throws Exception{
		String name = "";
		char c = (char)input.iAdvance();
		while (c != '}' && c != '\n') {
			name += c;
			c = (char)input.iAdvance();
		}
		
		if (c == '}') {
			return name;
		}
		else {
			ErrorHandler.parseErr(ErrorHandler.Error.E_BADMAC);
			return null;
		}
	}
}
