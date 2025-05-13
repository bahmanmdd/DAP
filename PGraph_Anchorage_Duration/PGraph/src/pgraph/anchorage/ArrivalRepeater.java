package pgraph.anchorage;

import pgraph.anchorage.distributions.ArrivalGenerator;

import java.util.ArrayList;


class DoubleList extends ArrayList<Double> {}

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 03.12.2013
 * Time: 10:14
 * To change this template use File | Settings | File Templates.
 */
public class ArrivalRepeater {
    ArrivalGenerator arrivalGenerator= null;
    private DoubleList arrivalMemory[]= null;
    private int memoryIndex[]=null;
    private int experimentCount=0;


    public ArrivalRepeater(ArrivalGenerator arrivalGenerator,int experimentCount) {
        this.arrivalGenerator = arrivalGenerator;
        this.arrivalMemory = new DoubleList[experimentCount];
        this.experimentCount = experimentCount;

        _initMemory();
    }

    private void _initMemory()
    {
        this.memoryIndex = new int[experimentCount];
        for (int i = 0 ;i<experimentCount;i++)
        {
            arrivalMemory[i] = new DoubleList();
        }

    }
    public ArrivalGenerator getArrivalGenerator() {
        return arrivalGenerator;
    }

    public void setArrivalGenerator(ArrivalGenerator arrivalGenerator) {
        this.arrivalGenerator = arrivalGenerator;
    }

    public void resetMemoryIndex(int experimentCount)
    {
        memoryIndex[experimentCount]=0;
    }

    public void resetMemoryIndex()
    {
        for (int i=0; i<memoryIndex.length;i++)
            resetMemoryIndex(i);
    }

    public void clearMemory()
    {
        for (int i=0; i<arrivalMemory.length;i++)
            arrivalMemory[i].clear();
    }


    public void clearMemory(int experimentCount)
    {
        arrivalMemory[experimentCount].clear();
    }

    public ArrivalInterface generate(int experimentcount)
    {
        ArrivalInterface val = null;
        if (memoryIndex[experimentcount]>=arrivalMemory[experimentcount].size())
        {
            val = arrivalGenerator.generate();
            arrivalMemory[experimentcount].add(val.getLength());
            memoryIndex[experimentcount] = arrivalMemory[experimentcount].size();
        }
        else {
            val = new Arrival(arrivalMemory[experimentcount].get(memoryIndex[experimentcount]++));
        }
        return val;
    }

}
