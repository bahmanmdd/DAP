package deneysel;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 28.01.2014
 * Time: 14:57
 * To change this template use File | Settings | File Templates.
 */
public class TestClass implements TestInterface {

    private static Random r = new Random(System.currentTimeMillis());

    public Double func1()
    {
        //System.out.println("func1 called");
        double a = 0;

        int c = r.nextInt(100);
        if (c<90)
            a= 3;
        else a= 5;
        return a;
    }

    @Override
    public double funcDouble() {
        double a = 0;

        int c = r.nextInt(100);
        if (c<90)
            a= 3;
        else a= 5;
        return a;
    }

    @Override
    public int funcInt() {
        int a = 0;

        int c = r.nextInt(100);
        if (c<90)
            a= 3;
        else a= 5;
        return a;
    }

    public void func2()
    {
        System.out.println("func2 called");
    }
}
