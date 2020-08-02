package ThompsonConstruction;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Nfa {

	public enum ANCHOR {
		NONE,
		START,
		END,
		BOTH 
	}
	public static final int EPSILON = -1; //�߶�Ӧ���Ǧ�
	public static final int CCL = -2; //�߶�Ӧ�����ַ���
	public static final int EMPTY = -3; //�ýڵ�û�г�ȥ�ı�
	private static final int ASCII_COUNT = 127;
	
    private int edge; //��¼ת���߶�Ӧ�����룬��������ǿ�, �ţ��ַ���(CCL),��գ�Ҳ����û�г�ȥ�ı�

	public int getEdge() {
		return edge;
	}

	public void setEdge(int type) {
		this.edge = type;
	}
	
	public Set<Byte> inputSet; //�����洢�ַ�����
    public Nfa     next;  //��ת����һ��״̬�������ǿ�
    public Nfa     next2; //��ת����һ��״̬����״̬���������ű�ʱ�����ָ�����Ч
    private ANCHOR  anchor;  //��Ӧ��������ʽ�Ƿ�ͷ����^, ���β����$,  �������������
    private int     stateNum; //�ڵ���
    private boolean visited = false; //�ڵ��Ƿ񱻷��ʹ������ڽڵ��ӡ

    public Nfa() {
    	inputSet = new HashSet<Byte>();
    	clearState();
    }

	public void clearState() {
		inputSet.clear();
		next = next2 = null;
		anchor = ANCHOR.NONE;
		stateNum = -1;
	}

    public void addToSet(Byte b) {
    	inputSet.add(b);
    }
	
    public void setComplement() {
    	Set<Byte> newSet = new HashSet<Byte>();
    	
    	for (byte b = 0; b < ASCII_COUNT; b++) {
    		if (inputSet.contains(b) == false) {
    			newSet.add(b);
    		}
    	}
    	
    	inputSet = null;
    	inputSet = newSet;
    }
    
    public void cloneNfa(Nfa nfa) {
    	inputSet.clear();
    	Iterator<Byte> it = nfa.inputSet.iterator();
    	while (it.hasNext()) {
    		inputSet.add(it.next());
    	}
    	
    	anchor = nfa.getAnchor();
    	this.next = nfa.next;
    	this.next2 = nfa.next2;
    	this.edge = nfa.getEdge();
    }
    
	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
	public ANCHOR getAnchor() {
		return anchor;
	}

	public void setAnchor(ANCHOR anchor) {
		this.anchor = anchor;
	}

	public int getStateNum() {
		return stateNum;
	}

	public void setStateNum(int stateNum) {
		this.stateNum = stateNum;
	}
	
	
}
