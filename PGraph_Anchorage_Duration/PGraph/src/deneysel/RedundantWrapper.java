package deneysel;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 07.01.2014
 * Time: 20:20
 * To change this template use File | Settings | File Templates.
 */

import java.lang.reflect.*;
import java.util.Hashtable;


public class RedundantWrapper implements InvocationHandler {
    private final int voteCount;
    private Object testImpl;


    Hashtable<Comparable,Integer> hashTable = new Hashtable<>();


    public RedundantWrapper(Object impl,int voteCount) {
        this.voteCount = voteCount;
        this.testImpl = impl;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
        throws Throwable
    {
        if(Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            if("equals".equals(name)) {
                return proxy == args[0];
            } else if("hashCode".equals(name)) {
            return System.identityHashCode(proxy);
            } else if("toString".equals(name)) {
            return proxy.getClass().getName() + "@" +
                Integer.toHexString(System.identityHashCode(proxy)) +
                ", with InvocationHandler " + this;
                } else {
                    throw new IllegalStateException(String.valueOf(method));
                }
            }
        //System.out.println("Proxy method called");

        Object retVal =  method.invoke(testImpl, args);
        if (retVal instanceof Comparable )
        {
            return _performVoting(method, args, retVal,voteCount);

        }
        return retVal;
    }

    private Object _performVoting(Method method, Object[] args, Object retVal,int voteCount)throws Throwable
    {
        hashTable.clear();
        hashTable.put((Comparable)retVal,1);

        for (int i= 1; i<voteCount;i++)
        {
            Comparable c = (Comparable)method.invoke(testImpl, args);

            if (hashTable.containsKey(c))
                hashTable.put(c,hashTable.get(c)+1);
            else
                hashTable.put(c,1);
        }

        Object maxVoted = _getMaxVoted(hashTable);
        return maxVoted;
    }

    private Comparable _getMaxVoted(Hashtable<Comparable, Integer> hashTable) {
        Comparable maxVoted = null;
        int maxValue = 0;
        for (Comparable c: hashTable.keySet())
        {
            if (hashTable.get(c)>maxValue)
            {
                maxValue = hashTable.get(c);
                maxVoted = c;
            }
        }
        return maxVoted;
    }

    public static Object getRedundantInstanceOf(Class<?> c, Object o,int voteCount)
    {
        Object redundant = Proxy.newProxyInstance(c.getClassLoader(), new Class<?>[]{c},new RedundantWrapper(o,voteCount) );
        return redundant;
    }


    public static void main(String[] args)
    {

        TestInterface redundant =(TestInterface) RedundantWrapper.getRedundantInstanceOf(TestInterface.class, new TestClass(),5);
        TestInterface normal = new TestClass();

        redundant.func2();

        for (int i = 0 ; i<20;i++)
            System.out.printf("redundant.func1(): %s  normal.func1(): %s%n", redundant.funcDouble(),normal.funcDouble());
    }

}