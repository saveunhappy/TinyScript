package common;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Stream;

public class PeekIterator<T> implements Iterator<T> {
    private Iterator<T> it;
    //目前理解为，因为流用完之后就没了，但是你又想获取之前最近的几个，就定义一下，最多缓存几个，多了，就删除
    // 最先进去的元素
    private LinkedList<T> queueCache = new LinkedList<>();
    private LinkedList<T> stackPutBacks = new LinkedList<>();
    private static final int CACHE_SIZE = 10;
    //endToken算是一个flag，看看是不是结束了，
    private T _endToken = null;

    public PeekIterator(Stream<T> stream) {
        it = stream.iterator();
    }

    public PeekIterator(Stream<T> stream, T endToken) {
        it = stream.iterator();
        _endToken = endToken;
    }


    //目前理解为就是猫一眼，不会说流用完了就没了
    //因为你peek的时候，还是要调用next方法的，就是往下走，就是调用
    // T val = next();但是你一会儿调用peek的话，还得是原来的啊，那怎么办？存起来，放到stackPutBacks
    //中去，然后你每次peek，看看stack里面有没有，没有，先putBack，放进去，
    // 有的话都是返回回去，而且，没有调用next，只是get方法，
    // 那你下次真正调用next，就得和这次peek的值一样了吧，那个值，其实已经弹出来了，但是弹到了stack里面，
    //所以你next的时候，就看看stackPutBacks里面有没有，有的话就弹出来，那绝对是你peek的时候putBack
    //再不懂，就debug,看的很清楚
    public T peek() {
        if (this.stackPutBacks.size() > 0) {
            return this.stackPutBacks.getFirst();
        }
        if (!it.hasNext()) {
            return _endToken;
        }
        T val = next();
        this.putBack();
        return val;
    }

    //缓存：A->B->C->D
    //放回：D->C->B->A
    //这个就是要往回找的，从缓存中找到最后一个，添加到stack的第一个，
    public void putBack() {
        if (this.queueCache.size() > 0) {
            //这个push就是栈，你看底层，addFirst,
            //后面的add,底层是linkLast,就是往最后加的，
            this.stackPutBacks.push(this.queueCache.pollLast());
        }
    }

    @Override
    public boolean hasNext() {
        return _endToken != null || this.stackPutBacks.size() > 0 || it.hasNext();
    }

    @Override
    public T next() {
        T val;
        if (this.stackPutBacks.size() > 0) {
            val = this.stackPutBacks.pop();
        } else {
            if (!this.it.hasNext()) {
                //程序进到这里说明endToken可能是null，那么就得吧endToken置null，其他调用hasNext的时候不会报错
                //然后把原来的endtToken给返回
                T tmp = _endToken;
                _endToken = null;
                return tmp;
            }
            val = it.next();
        }
        while (queueCache.size() > CACHE_SIZE - 1) {
            queueCache.poll();
        }
        queueCache.add(val);
        return val;
    }
}
