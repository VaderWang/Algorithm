package ThompsonConstruction;

import java.util.Set;

public class NfaMachineConstructor {
	
	private Lexer lexer;
	private NfaManager nfaManager = null;
	
    public NfaMachineConstructor(Lexer lexer) throws Exception {
    	this.lexer = lexer;
    	nfaManager = new NfaManager();
    	
    	while (lexer.MatchToken(Lexer.Token.EOS)) {
    		lexer.advance();
    	}
    }
    
    public void expr(NfaPair pairOut) throws Exception {
    	/*
    	 * expr ��һ������cat_expr ֮����� OR �γ�
    	 * ������ʽֻ��һ��cat_expr ��ôexpr �͵ȼ���cat_expr
    	 * ������ʽ�ɶ��cat_expr�������ӹ�����ô expr-> cat_expr | cat_expr | ....
    	 * �ɴ˵õ�expr���﷨����Ϊ:
    	 * expr -> expr OR cat_expr
    	 *         | cat_expr 
    	 *
    	 */
    	 connExpr(pairOut);
    	 NfaPair localPair = new NfaPair();
    	
    	 while (lexer.MatchToken(Lexer.Token.OR)) {
    		 lexer.advance();
    		 connExpr(localPair);
    		 
    		 Nfa startNode = nfaManager.newNfa();
    		 startNode.next2 = localPair.startNode;
    		 startNode.next = pairOut.startNode;
    		 pairOut.startNode = startNode;
    		 
    		 Nfa endNode = nfaManager.newNfa();
    		 pairOut.endNode.next = endNode;
    		 localPair.endNode.next = endNode;
    		 pairOut.endNode = endNode;
    	 }
    	 
    }
    
   
    public void connExpr(NfaPair pairOut) throws Exception
    {
    	/*
    	 * cat_expr -> factor factor .....
    	 * ���ڶ��factor ǰ���Ͼ���һ��cat_expr����
    	 * cat_expr-> factor cat_expr
    	 */

    	if (isCorrectRex(lexer.getCurrentToken())) {
    		factor(pairOut);
    	}
    	
    	char c = (char)lexer.getLexeme();
    	
    	while (isCorrectRex(lexer.getCurrentToken()) ){
    		NfaPair pairLocal = new NfaPair();
    		factor(pairLocal);
    		
    		pairOut.endNode.next = pairLocal.startNode;
    		
    		pairOut.endNode = pairLocal.endNode;
    	}
    	
    	
    }
    
    private boolean isCorrectRex(Lexer.Token token) throws Exception {
    	switch (token) {
    	//��ȷ�ı��ʽ������ ) $ ��ͷ,�������EOS��ʾ������ʽ������ϣ���ô�Ͳ�Ӧ��ִ�иú���
    	case CLOSE_PAREN:
    	case AT_EOL:
    	case OR:
    	case EOS:
    		return false;
    	case CLOSURE:
    	case PLUS_CLOSE:
    	case OPTIONAL:
    		//*, +, ? �⼸������Ӧ�÷��ڱ��ʽ��ĩβ
    		ErrorHandler.parseErr(ErrorHandler.Error.E_CLOSE);
    		return false;
    	case CCL_END:
    		//���ʽ��Ӧ����]��ͷ
    		ErrorHandler.parseErr(ErrorHandler.Error.E_BRACKET);
    		return false;
    	case AT_BOL:
    		//^�����ڱ��ʽ���ʼ
    		ErrorHandler.parseErr(ErrorHandler.Error.E_BOL);
    		return false;
		default:
			break;
    	}
    	
    	return true;
    }
    
    public void factor(NfaPair pairOut) throws Exception {
    	term(pairOut);
    	
    	boolean handled = false;
    	handled = constructStarClosure(pairOut);
    	if (handled == false) {
    		handled = constructPlusClosure(pairOut);
    	}
    	
    	if (handled == false) {
    		handled = constructOptionsClosure(pairOut);
    	}
    	
    }
    
    
    public boolean constructStarClosure(NfaPair pairOut) throws Exception {
    	/*
    	 * term*
    	 */
    	Nfa start, end;
    //	term(pairOut);
    	
    	if (lexer.MatchToken(Lexer.Token.CLOSURE) == false) {
    		return false;
    	}
    	
    	start = nfaManager.newNfa();
    	end = nfaManager.newNfa();
    	
    	start.next = pairOut.startNode;
    	pairOut.endNode.next = pairOut.startNode;
    	
    	start.next2 = end;
    	pairOut.endNode.next2 = end;
    	
    	pairOut.startNode = start;
    	pairOut.endNode = end;
    	
    	lexer.advance();
    	
    	return true;
    }
    
    public boolean constructPlusClosure(NfaPair pairOut) throws Exception {
    	/*
    	 * term+
    	 */
    	Nfa start, end;
    //	term(pairOut);
    	
    	if (lexer.MatchToken(Lexer.Token.PLUS_CLOSE) == false) {
    		return false;
    	}
    	
    	start = nfaManager.newNfa();
    	end = nfaManager.newNfa();
    	
    	start.next = pairOut.startNode;
    	pairOut.endNode.next2 = end;
    	pairOut.endNode.next = pairOut.startNode;
    	
    	
    	pairOut.startNode = start;
    	pairOut.endNode = end;
    	
    	lexer.advance();
    	return true;
    }
    
    public boolean constructOptionsClosure(NfaPair pairOut) throws Exception {
    	/*
    	 * term?
    	 */
    	Nfa start, end;
  //  	term(pairOut);
    	
    	if (lexer.MatchToken(Lexer.Token.OPTIONAL) == false) {
    		return false;
    	}
    	
    	start = nfaManager.newNfa();
    	end = nfaManager.newNfa();
    	
    	start.next = pairOut.startNode;
    	pairOut.endNode.next = end;
    	
    	start.next2 = end;
    	
    	pairOut.startNode = start;
    	pairOut.endNode = end;
    	
    	lexer.advance();
    	
    	return true;
    }
    
    public void term(NfaPair pairOut)throws Exception {
        /*
         * term ->  character | [...] | [^...] | [character-charcter] | . | (expr)
         * 
         */
    	
    	boolean handled = constructExprInParen(pairOut);
    	if (handled == false) {
    		handled = constructNfaForSingleCharacter(pairOut);
    	}
    			
    	if (handled == false) {
    		handled = constructNfaForDot(pairOut);
    	}
    	
    	if (handled == false) {
    		constructNfaForCharacterSet(pairOut);
    	}
    	
    	
    }
    
    private boolean constructExprInParen(NfaPair pairOut) throws Exception {
    	if (lexer.MatchToken(Lexer.Token.OPEN_PAREN)) {
    		lexer.advance();
    		expr(pairOut);
    		if (lexer.MatchToken(Lexer.Token.CLOSE_PAREN)) {
    			lexer.advance();
    		}
    		else {
    			ErrorHandler.parseErr(ErrorHandler.Error.E_PAREN);
    		}
    		
    		return true;
    	}
    	
    	return false;
    }
    
    public boolean constructNfaForSingleCharacter(NfaPair pairOut) throws Exception {
    	if (lexer.MatchToken(Lexer.Token.L) == false) {
    		return false;
    	}
    	
    	Nfa start = null;
    	start = pairOut.startNode = nfaManager.newNfa();
    	pairOut.endNode = pairOut.startNode.next = nfaManager.newNfa();
    	
    	start.setEdge(lexer.getLexeme());
    	
    	lexer.advance();
    	
    	return true;
    }
    
    public boolean constructNfaForDot(NfaPair pairOut) throws Exception {
    	if (lexer.MatchToken(Lexer.Token.ANY) == false) {
    		return false;
    	}
    	
    	Nfa start = null;
    	start = pairOut.startNode = nfaManager.newNfa();
    	pairOut.endNode = pairOut.startNode.next = nfaManager.newNfa();
    	
    	start.setEdge(Nfa.CCL);
    	start.addToSet((byte)'\n');
    	start.addToSet((byte)'\r');
    	start.setComplement();
    	
    	lexer.advance();
    	
    	return true;
    }
    
    public boolean constructNfaForCharacterSetWithoutNegative(NfaPair pairOut) throws Exception {
    	
    	if (lexer.MatchToken(Lexer.Token.CCL_START) == false) {
    		return false;
    	}
    	
    	lexer.advance();
    	
    	Nfa start = null;
    	start = pairOut.startNode = nfaManager.newNfa();
    	pairOut.endNode = pairOut.startNode.next = nfaManager.newNfa();
    	start.setEdge(Nfa.CCL);
    	
    	if (lexer.MatchToken(Lexer.Token.CCL_END) == false) {
    		dodash(start.inputSet);
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.CCL_END) == false) {
    		ErrorHandler.parseErr(ErrorHandler.Error.E_BADEXPR);
    	}
    	lexer.advance();
    	
    	return true;
    }
    
    public boolean constructNfaForCharacterSet(NfaPair pairOut) throws Exception {
    	if (lexer.MatchToken(Lexer.Token.CCL_START) == false) {
    		return false;
    	}
    	
    	lexer.advance();
    	boolean negative = false;
    	if (lexer.MatchToken(Lexer.Token.AT_BOL)) {
    		negative = true;
    	}
    	
    	Nfa start = null;
    	start = pairOut.startNode = nfaManager.newNfa();
    	pairOut.endNode = pairOut.startNode.next = nfaManager.newNfa();
    	start.setEdge(Nfa.CCL);
    	
    	if (lexer.MatchToken(Lexer.Token.CCL_END) == false) {
    		dodash(start.inputSet);
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.CCL_END) == false) {
    		ErrorHandler.parseErr(ErrorHandler.Error.E_BADEXPR);
    	}
    	
    	if (negative) {
    		start.setComplement();
    	}
    	
    	lexer.advance();
    	
    	return true;
    }
    
    private void dodash(Set<Byte> set) {
    	int first = 0;
    	
    	while (lexer.MatchToken(Lexer.Token.EOS) == false && 
    			lexer.MatchToken(Lexer.Token.CCL_END) == false) {
    		
    		if (lexer.MatchToken(Lexer.Token.DASH) == false) {
    			first = lexer.getLexeme();
    			set.add((byte)first);
    		}
    		else {
    			lexer.advance(); //Խ�� -
    			for (; first <= lexer.getLexeme(); first++) {
    				set.add((byte)first);
    			}
    		}
    		
    		lexer.advance();
    	}
    	
    		
    }
}
