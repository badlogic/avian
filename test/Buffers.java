import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Buffers {
	private static void expect(boolean v) {
		if (!v)
			throw new RuntimeException();
	}
	
	private static void testBuffer() {
		ByteBuffer buffer = ByteBuffer.allocateDirect(10);
		buffer.put(0, (byte)123);
		System.out.println(buffer.get(0));
	}
	
	public static void main(String[] args) {
		System.out.println("byte order: " + ByteOrder.nativeOrder());
		System.out.println(Math.cbrt(0));
		System.out.println(Math.atan2(1, 0));
		testBuffer();
		System.gc();
		testBuffer();
	}
}
