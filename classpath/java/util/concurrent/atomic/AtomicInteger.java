package java.util.concurrent.atomic;

/**
 * Dummy FIXME
 * @author mzechner
 *
 */
public class AtomicInteger {
	int value;
	
	public synchronized int get() {
		return value;
	}
	
	public synchronized void set(int value) {
		this.value = value;
	}
}
