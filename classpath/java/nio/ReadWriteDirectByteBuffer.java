/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package java.nio;

import java.nio.luni.PlatformAddress;
import java.nio.luni.PlatformAddressFactory;

/**
 * DirectByteBuffer, ReadWriteDirectByteBuffer and ReadOnlyDirectByteBuffer
 * compose the implementation of platform memory based byte buffers.
 * <p>
 * ReadWriteDirectByteBuffer extends DirectByteBuffer with all the write
 * methods.
 * </p>
 * <p>
 * This class is marked final for runtime performance.
 * </p>
 */
final class ReadWriteDirectByteBuffer extends DirectByteBuffer {

    static ReadWriteDirectByteBuffer copy(DirectByteBuffer other, int markOfOther) {
        ReadWriteDirectByteBuffer buf = new ReadWriteDirectByteBuffer(
                other.safeAddress, other.capacity(), other.offset);
        buf.limit = other.limit();
        buf.position = other.position();
        buf.mark = markOfOther;
        buf.order(other.order());
        return buf;
    }

    ReadWriteDirectByteBuffer(int capacity) {
        super(capacity);
    }

    // BEGIN android-added
    ReadWriteDirectByteBuffer(int pointer, int capacity) {
        this(PlatformAddressFactory.on(pointer, capacity),capacity,0);
    }
    // END android-added

    ReadWriteDirectByteBuffer(SafeAddress address, int capacity, int offset) {
        super(address, capacity, offset);
    }

    ReadWriteDirectByteBuffer(PlatformAddress address, int aCapacity,
            int anOffset) {
        super(new SafeAddress(address), aCapacity, anOffset);
    }

    // BEGIN android-added
    long getAddress() {
        return this.safeAddress.address.toLong();
    }
    // END android-added

    @Override
    public ByteBuffer asReadOnlyBuffer() {
        return ReadOnlyDirectByteBuffer.copy(this, mark);
    }

    @Override
    public ByteBuffer compact() {
        PlatformAddress effectiveAddress = getEffectiveAddress();
        effectiveAddress.offsetBytes(position).moveTo(effectiveAddress, remaining());
        position = limit - position;
        limit = capacity;
        mark = UNSET_MARK;
        return this;
    }

    @Override
    public ByteBuffer duplicate() {
        return copy(this, mark);
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public ByteBuffer put(byte value) {
        if (position == limit) {
            throw new BufferOverflowException();
        }
        getBaseAddress().setByte(offset + position++, value);
        return this;
    }

    @Override
    public ByteBuffer put(int index, byte value) {
        if (index < 0 || index >= limit) {
            throw new IndexOutOfBoundsException();
        }
        getBaseAddress().setByte(offset + index, value);
        return this;
    }

    /*
     * Override ByteBuffer.put(byte[], int, int) to improve performance.
     *
     * (non-Javadoc)
     *
     * @see java.nio.ByteBuffer#put(byte[], int, int)
     */
    @Override
    public ByteBuffer put(byte[] src, int off, int len) {
        int length = src.length;
        if (off < 0 || len < 0 || (long) off + (long) len > length) {
            throw new IndexOutOfBoundsException();
        }
        if (len > remaining()) {
            throw new BufferOverflowException();
        }
        getBaseAddress().setByteArray(offset + position, src, off, len);
        position += len;
        return this;
    }

    // BEGIN android-added
    /**
     * Writes <code>short</code>s in the given short array, starting from the
     * specified offset, to the current position and increase the position by
     * the number of <code>short</code>s written.
     *
     * @param src
     *            The source short array
     * @param off
     *            The offset of short array, must be no less than zero and no
     *            greater than <code>src.length</code>
     * @param len
     *            The number of <code>short</code>s to write, must be no less
     *            than zero and no greater than <code>src.length - off</code>
     * @return This buffer
     * @exception BufferOverflowException
     *                If <code>remaining()</code> is less than
     *                <code>len</code>
     * @exception IndexOutOfBoundsException
     *                If either <code>off</code> or <code>len</code> is
     *                invalid
     * @exception ReadOnlyBufferException
     *                If no changes may be made to the contents of this buffer
     */
    ByteBuffer put(short[] src, int off, int len) {
        int length = src.length;
        if (off < 0 || len < 0 || (long)off + (long)len > length) {
            throw new IndexOutOfBoundsException();
        }
        if (len << 1 > remaining()) {
            throw new BufferOverflowException();
        }
        if (isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        boolean swap = order() != ByteOrder.nativeOrder();
        getBaseAddress().setShortArray(offset + position, src, off, len, swap);
        position += len << 1;
        return this;
    }

    /**
     * Writes <code>int</code>s in the given int array, starting from the
     * specified offset, to the current position and increase the position by
     * the number of <code>int</code>s written.
     *
     * @param src
     *            The source int array
     * @param off
     *            The offset of int array, must be no less than zero and no
     *            greater than <code>src.length</code>
     * @param len
     *            The number of <code>int</code>s to write, must be no less
     *            than zero and no greater than <code>src.length - off</code>
     * @return This buffer
     * @exception BufferOverflowException
     *                If <code>remaining()</code> is less than
     *                <code>len</code>
     * @exception IndexOutOfBoundsException
     *                If either <code>off</code> or <code>len</code> is
     *                invalid
     * @exception ReadOnlyBufferException
     *                If no changes may be made to the contents of this buffer
     */
    ByteBuffer put(int[] src, int off, int len) {
        int length = src.length;
        if (off < 0 || len < 0 || (long)off + (long)len > length) {
            throw new IndexOutOfBoundsException();
        }
        if (len << 2 > remaining()) {
            throw new BufferOverflowException();
        }
        if (isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        boolean swap = order() != ByteOrder.nativeOrder();
        getBaseAddress().setIntArray(offset + position, src, off, len, swap);
        position += len << 2;
        return this;
    }

    /**
     * Writes <code>float</code>s in the given float array, starting from the
     * specified offset, to the current position and increase the position by
     * the number of <code>float</code>s written.
     *
     * @param src
     *            The source float array
     * @param off
     *            The offset of float array, must be no less than zero and no
     *            greater than <code>src.length</code>
     * @param len
     *            The number of <code>float</code>s to write, must be no less
     *            than zero and no greater than <code>src.length - off</code>
     * @return This buffer
     * @exception BufferOverflowException
     *                If <code>remaining()</code> is less than
     *                <code>len</code>
     * @exception IndexOutOfBoundsException
     *                If either <code>off</code> or <code>len</code> is
     *                invalid
     * @exception ReadOnlyBufferException
     *                If no changes may be made to the contents of this buffer
     */
    ByteBuffer put(float[] src, int off, int len) {
        int length = src.length;
        if (off < 0 || len < 0 || (long)off + (long)len > length) {
            throw new IndexOutOfBoundsException();
        }
        if (len << 2 > remaining()) {
            throw new BufferOverflowException();
        }
        if (isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        boolean swap = order() != ByteOrder.nativeOrder();
        getBaseAddress().setFloatArray(offset + position, src, off, len, swap);
        position += len << 2;
        return this;
    }
    // END android-added

    @Override
    public ByteBuffer putDouble(double value) {
        int newPosition = position + 8;
        if (newPosition > limit) {
            throw new BufferOverflowException();
        }
        getBaseAddress().setDouble(offset + position, value, order);
        position = newPosition;
        return this;
    }

    @Override
    public ByteBuffer putDouble(int index, double value) {
        if (index < 0 || (long) index + 8 > limit) {
            throw new IndexOutOfBoundsException();
        }
        getBaseAddress().setDouble(offset + index, value, order);
        return this;
    }

    @Override
    public ByteBuffer putFloat(float value) {
        int newPosition = position + 4;
        if (newPosition > limit) {
            throw new BufferOverflowException();
        }
        getBaseAddress().setFloat(offset + position, value, order);
        position = newPosition;
        return this;
    }

    @Override
    public ByteBuffer putFloat(int index, float value) {
        if (index < 0 || (long) index + 4 > limit) {
            throw new IndexOutOfBoundsException();
        }
        getBaseAddress().setFloat(offset + index, value, order);
        return this;
    }

    @Override
    public ByteBuffer putInt(int value) {
        int newPosition = position + 4;
        if (newPosition > limit) {
            throw new BufferOverflowException();
        }
        getBaseAddress().setInt(offset + position, value, order);
        position = newPosition;
        return this;
    }

    @Override
    public ByteBuffer putInt(int index, int value) {
        if (index < 0 || (long) index + 4 > limit) {
            throw new IndexOutOfBoundsException();
        }
        getBaseAddress().setInt(offset + index, value, order);
        return this;
    }

    @Override
    public ByteBuffer putLong(long value) {
        int newPosition = position + 8;
        if (newPosition > limit) {
            throw new BufferOverflowException();
        }
        getBaseAddress().setLong(offset + position, value, order);
        position = newPosition;
        return this;
    }

    @Override
    public ByteBuffer putLong(int index, long value) {
        if (index < 0 || (long) index + 8 > limit) {
            throw new IndexOutOfBoundsException();
        }
        getBaseAddress().setLong(offset + index, value, order);
        return this;
    }

    @Override
    public ByteBuffer putShort(short value) {
        int newPosition = position + 2;
        if (newPosition > limit) {
            throw new BufferOverflowException();
        }
        getBaseAddress().setShort(offset + position, value, order);
        position = newPosition;
        return this;
    }

    @Override
    public ByteBuffer putShort(int index, short value) {
        if (index < 0 || (long) index + 2 > limit) {
            throw new IndexOutOfBoundsException();
        }
        getBaseAddress().setShort(offset + index, value, order);
        return this;
    }

    @Override
    public ByteBuffer slice() {
        ReadWriteDirectByteBuffer buf = new ReadWriteDirectByteBuffer(
                safeAddress, remaining(), offset + position);
        buf.order = order;
        return buf;
    }

}
