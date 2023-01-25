package com.github.cosycode.common.ext.struct;

import com.github.cosycode.common.util.common.ArrUtils;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <b>Description : </b> 有限的栈, 特点是初始有一个容量, 如果存放的数据超过这个容量, 则最老的对象数据会被抛弃
 * <p>
 * <b>created in </b> 2021/3/24
 *
 * @author CPF
 * @since 1.3
 **/
public class LimitStack<E extends Serializable> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 存放对象的数组
     */
    private final Serializable[] objects;

    @Getter
    private final int maxSize;

    /**
     * 数组大小
     */
    private int size;

    /**
     * 索引, 指向数组中的元素
     */
    private int idx;

    /**
     * 向其中插入的元素总量
     */
    @Getter
    private int total;

    public LimitStack(int maxSize) {
        this.maxSize = maxSize;
        objects = new Serializable[size];
    }

    /**
     * @return 当前栈的大小
     */
    public int size() {
        return size;
    }

    /**
     * 向数组中添加元素
     *
     * @param t 元素
     */
    private void addElement(E t) {
        objects[idx] = t;
        idx++;
        total++;
        if (size < maxSize) {
            size++;
        }
        if (idx >= maxSize) {
            idx %= maxSize;
        }
    }

    /**
     * 从数组中移除元素
     */
    private E removeElement() {
        if (size <= 0) {
            throw new EmptyStackException();
        }
        idx--;
        if (idx < 0) {
            idx = maxSize - 1;
        }
        Object object = objects[idx];
        objects[idx] = null;
        total--;
        size--;
        return (E) object;
    }

    /**
     * 将元素添加到栈中
     * <blockquote><pre>addElement(item)</pre></blockquote>
     *
     * @param item the item to be pushed onto this stack.
     * @return the <code>item</code> argument.
     */
    public E push(E item) {
        addElement(item);
        return item;
    }

    /**
     * Removes the object at the top of this stack and returns that
     * object as the value of this function.
     *
     * @return The object at the top of this stack (the last item
     * of the <tt>Vector</tt> object).
     * @throws EmptyStackException if this stack is empty.
     */
    public synchronized E pop() {
        E obj;
        obj = peek();
        removeElement();
        return obj;
    }

    /**
     * Looks at the object at the top of this stack without removing it from the stack.
     *
     * @return the object at the top of this stack (the last item
     * of the <tt>Vector</tt> object).
     * @throws EmptyStackException if this stack is empty.
     */
    public synchronized E peek() {
        if (size() == 0) {
            throw new EmptyStackException();
        }
        return (E) objects[idx - 1];
    }

    /**
     * Tests if this stack is empty.
     *
     * @return <code>true</code> if and only if this stack contains
     * no items; <code>false</code> otherwise.
     */
    public boolean empty() {
        return size() == 0;
    }

    /**
     * Returns the 1-based position where an object is on this stack.
     * If the object <tt>o</tt> occurs as an item in this stack, this
     * method returns the distance from the top of the stack of the
     * occurrence nearest the top of the stack; the topmost item on the
     * stack is considered to be at distance <tt>1</tt>. The <tt>equals</tt>
     * method is used to compare <tt>o</tt> to the
     * items in this stack.
     *
     * @param o the desired object.
     * @return the 1-based position from the top of the stack where
     * the object is located; the return value <code>-1</code>
     * indicates that the object is not on the stack.
     */
    public synchronized int search(E o) {
        int i = ArrUtils.indexOf(objects, o);
        if (i < 0) {
            return -1;
        }
        if (i < idx) {
            return idx - i;
        } else {
            return i - idx + maxSize;
        }
    }

    public E prev(int num) {
        num %= size;
        int d = idx - num;
        if (d < 0) {
            d += size;
        }
        return (E) objects[d];
    }

    public List<E> prevList(int num) {
        List<E> list = new ArrayList<>(num);
        for (int i = num; i > 0; i--) {
            list.add(prev(i));
        }
        return list;
    }

    public List<E> prevList(int lastStartNum, int lastEndNum) {
        final List<E> ts = prevList(lastStartNum);
        return ts.subList(0, ts.size() - lastEndNum);
    }

    @Override
    public String toString() {
        return "LimitStack{" +
                "objects=" + IntStream.range(0, 19).mapToObj(this::prev).map(Objects::toString).collect(Collectors.joining()) +
                ", size=" + size +
                ", idx=" + idx +
                '}';
    }

}
