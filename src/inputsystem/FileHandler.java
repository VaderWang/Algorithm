package inputsystem;

/*
 *	���������л�ȡ��Ϣ�Ľӿڣ������ļ�����system.in 
 */
public interface FileHandler {

	public void open();
	public int close();
	
	/*
	 * ���ض�ȡ�ĳ���
	 */
	public int read(byte[] buf, int begin, int len);
}
