package common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class PeekIteratorTest {
    @Test
    public void testPeek() throws Exception{
        var source  = "abcdefg";
        var it = new PeekIterator<>(source.chars().mapToObj(c -> (char) c));

        assertEquals('a',it.next());
        assertEquals('b',it.next());
        it.next();
        it.next();
        assertEquals('e',it.next());
        assertEquals('f',it.peek());
        assertEquals('f',it.peek());
        assertEquals('f',it.next());
        assertEquals('g',it.next());
    }
    @Test
    public void testLookHead() throws Exception{
        var source  = "abcdefg";
        var it = new PeekIterator<>(source.chars().mapToObj(c -> (char) c));

        assertEquals('a',it.next());
        assertEquals('b',it.next());
        assertEquals('c',it.next());
        it.putBack();
        it.putBack();
        //进去的时候是b、c,那么putBack就是倒过来，栈，放的就是c、b,每次next就是看看stack里面有没有，
        //有的话，就弹出来，没有，就从缓存中取出最后一个放进去，
        // 因为缓存每次要放CacheSize个，你每次走到那，不超过缓存的大小的话，这些个东西，
        //就又添加到缓存中去了，就是这俩东西，要不我弹出来放你那，要不你弹出来放我这。
        assertEquals('b',it.next());
    }
    @Test
    public void testEndToken() throws Exception{
        var source  = "abcdefg";
        var it = new PeekIterator<>(source.chars().mapToObj(c -> (char) c),(char)0);
        var i  = 0;
        //判断有没有下一个hasNext,就是看it.hasNext()是不是false,里面调用next的时候，用的是iterator内部的hasNext
        // 不是这里实现了iterator接口的hasNext，就是重写父类的那个，这里加一个endtoken，就是加强了程序的健壮性
        //既可以通过本来的hasNext，或者，也可以通过stack中的size,如果没有next，就可以把原来的endToken先接受一下
        //然后置空，返回接受的那个，也知道是个啥，然后再到while循环条件，这时候，endtoken就是null了，就可以跳出循环了
        while (it.hasNext()){
            if(i == 7){
                assertEquals((char)0,it.next());
            }else {
                assertEquals(source.charAt(i++),it.next());
            }
        }
    }

}