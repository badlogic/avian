import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

public class Buffers {
	private static void expect(boolean v) {
		if (!v)
			throw new RuntimeException();
	}
	
	private static void testBuffer() {
		ByteBuffer buffer = ByteBuffer.allocateDirect(10);
		buffer.order(ByteOrder.nativeOrder());
		buffer.put(0, (byte)123);
		System.out.println(buffer.get(0));
		
		CharBuffer cbuffer = buffer.asCharBuffer();
		cbuffer.put(0, (char)0xffff);
		System.out.println((int)cbuffer.get(0));
		
		ShortBuffer sbuffer = buffer.asShortBuffer();
		sbuffer.put(0, (short)0x1234);
		System.out.println((short)sbuffer.get(0));
		
		IntBuffer ibuffer = buffer.asIntBuffer();
		ibuffer.put(0, 0x13456789);
		System.out.println((short)ibuffer.get(0));
		
		LongBuffer lbuffer = buffer.asLongBuffer();
		lbuffer.put(0, 0x123456789abcdef0l);
		System.out.println(lbuffer.get(0));
		
		FloatBuffer fbuffer = buffer.asFloatBuffer();
		fbuffer.put(0, 123.789123f);
		System.out.println(fbuffer.get(0));
		System.out.println(fbuffer.get(0) == 123.789123f);
		
		DoubleBuffer dbuffer = buffer.asDoubleBuffer();
		dbuffer.put(0, 123.789123f);
		System.out.println(dbuffer.get(0));
		System.out.println(dbuffer.get(0) == 123.789123f);
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
