package ThompsonConstruction;

import inputsystem.Input;

public class ThompsonConstruction {

	private Input input = new Input();;
	private MacroHandler macroHandler = null;
	RegularExpressionHandler regularExpr = null;
	private Lexer lexer = null;
	private NfaMachineConstructor nfaMachineConstructor = null;
    private NfaPrinter nfaPrinter = new NfaPrinter();
	private NfaPair pair = new NfaPair();
	private NfaIntepretor nfaIntepretor = null;
	private DfaConstructor dfaConstructor = null;
	private MinimizeDFA miniDfa = null;
	
	public void runMacroExample() {
    	System.out.println("Please enter macro definition");
    	
    	renewInputBuffer();
    	macroHandler = new MacroHandler(input);
    	macroHandler.printMacs();
	}
	
	public void runMacroExpandExample() throws Exception {
    	System.out.println("Enter regular expression");
    	renewInputBuffer();
    	
    	regularExpr = new RegularExpressionHandler(input, macroHandler);
    	System.out.println("regular expression after expanded: ");
    	for (int i = 0; i < regularExpr.getRegularExpressionCount(); i++) {
    		System.out.println(regularExpr.getRegularExpression(i));	
    	}
    	
    }
	
	private void runLexerExample() {
		lexer = new Lexer(regularExpr);
		int exprCount = 0;
		System.out.println("��ǰ���������������ʽ: " + regularExpr.getRegularExpression(exprCount));
	    lexer.advance();
	    while (lexer.MatchToken(Lexer.Token.END_OF_INPUT) == false) {
	     	   
	      if(lexer.MatchToken(Lexer.Token.EOS) == true) {
	   		   System.out.println("������һ��������ʽ");
	   		   exprCount++;
	   		   System.out.println("��ǰ���������������ʽ: " + regularExpr.getRegularExpression(exprCount));
	   		   lexer.advance();
    	   }
	   	   else {
	     	   printLexResult();
	       }  
	    } 
	}
	
	public void runNfaIntepretorExample() {
    	nfaIntepretor = new NfaIntepretor(pair.startNode, input);
    	nfaIntepretor.intepretNfa();
    }
	
	private void runNfaMachineConstructorExample() throws Exception {
    	lexer = new Lexer(regularExpr);
    	nfaMachineConstructor = new NfaMachineConstructor(lexer);
    	
    	//nfaMachineConstructor.constructNfaForSingleCharacter(pair);
    	//nfaMachineConstructor.constructNfaForDot(pair);
    	//nfaMachineConstructor.constructNfaForCharacterSetWithoutNegative(pair);
    	//nfaMachineConstructor.constructNfaForCharacterSet(pair);
    	//nfaMachineConstructor.term(pair);
    	//nfaMachineConstructor.constructStarClosure(pair);
    	//nfaMachineConstructor.constructPlusClosure(pair);
    	//nfaMachineConstructor.constructOptionsClosure(pair);
    	//nfaMachineConstructor.factor(pair);
    	//nfaMachineConstructor.andExpr(pair);
    	nfaMachineConstructor.expr(pair);
    	
    	nfaPrinter.printNfa(pair.startNode);
    }
	
	public void runDfaConstructorExample() {
		   dfaConstructor = new DfaConstructor(pair, nfaIntepretor);
		   dfaConstructor.convertNfaToDfa();
		   dfaConstructor.printDFA();
	   }
	
	private void runMinimizeDFAExample() {
    	miniDfa = new MinimizeDFA(dfaConstructor);
    	miniDfa.minimize();
    }
	
	private void printLexResult() {
    	while (lexer.MatchToken(Lexer.Token.EOS) == false) {
    		System.out.println("��ǰʶ���ַ���: " + (char)lexer.getLexeme());
    		
    		if (lexer.MatchToken(Lexer.Token.L) != true) {
    			System.out.println("��ǰ�ַ��������⺬��");
    			
    			printMetaCharMeaning(lexer);
    		}
    		else {
    			System.out.println("��ǰ�ַ�����ͨ�ַ�����");
    		}
    		
    		lexer.advance();
    	}
    }
    
    private void printMetaCharMeaning(Lexer lexer) {
    	String s = "";
    	if (lexer.MatchToken(Lexer.Token.ANY)) {
    		s = "��ǰ�ַ��ǵ�ͨ���";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.AT_BOL)) {
    		s = "��ǰ�ַ��ǿ�ͷƥ���";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.AT_EOL)) {
    		s = "��ǰ�ַ���ĩβƥ���";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.CCL_END)) {
    		s = "��ǰ�ַ����ַ������β����";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.CCL_START)) {
    		s = "��ǰ�ַ����ַ�����Ŀ�ʼ����";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.CLOSE_CURLY)) {
    		s = "��ǰ�ַ��ǽ�β������";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.CLOSE_PAREN)) {
    		s = "��ǰ�ַ��ǽ�βԲ����";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.DASH)) {
    		s = "��ǰ�ַ��Ǻ��";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.OPEN_CURLY)) {
    		s = "��ǰ�ַ�����ʼ������";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.OPEN_PAREN)) {
    		s = "��ǰ�ַ�����ʼԲ����";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.OPTIONAL)) {
    		s = "��ǰ�ַ��ǵ��ַ�ƥ���?";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.OR)) {
    		s = "��ǰ�ַ��ǻ������";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.PLUS_CLOSE)) {
    		s = "��ǰ�ַ������հ�������";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.CLOSURE)) {
    		s = "��ǰ�ַ��Ǳհ�������";
    	}
    	
    	System.out.println(s);
    }

	public void renewInputBuffer() {
		//input  = new Input();
		input.iNewFile(null);
		input.iAdvance();
		//System.out.println(input.iAdvance());
		input.iPushBack(1);
	}
	
	public static void main(String[] args) throws Exception {
    	ThompsonConstruction construction = new ThompsonConstruction();
    	construction.runMacroExample();
    	construction.runMacroExpandExample();
    	construction.runLexerExample();
    	construction.runNfaMachineConstructorExample();
    	construction.runNfaIntepretorExample();
    	construction.runDfaConstructorExample();
    	construction.runMinimizeDFAExample();
    }
}
