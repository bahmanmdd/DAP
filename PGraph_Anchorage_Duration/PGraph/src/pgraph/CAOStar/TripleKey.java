package pgraph.CAOStar;


import pgraph.grid.GridVertex;
import pgraph.rdp.RDPObstacleInterface;

import java.util.HashMap;
import java.util.Iterator;

public class TripleKey
{
    public RDPObstacleInterface o1;
    public GridVertex o2;
    public HashMap<RDPObstacleInterface, Character> o3;


    public TripleKey(RDPObstacleInterface o1, GridVertex o2, HashMap<RDPObstacleInterface, Character> o3)
    { 
    	this.o1 = o1; this.o2 = o2; this.o3=o3;
    }

    public RDPObstacleInterface first()
    {
    	return o1;
    }
    
    public GridVertex second()
    {
    	return o2;
    }

    public HashMap<RDPObstacleInterface, Character> third()
    {
        return o3;
    }
	/* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     * Note, I don't know if it works well. Maybe in some tricky case, collision may happen.
     */

    public static boolean same(Object o1, Object o2)
    {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    public String toString()
    {
        return "Pair{"+o1+", "+o2+", "+o3+"}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TripleKey)) return false;

        TripleKey tripleKey = (TripleKey) o;

        if (!o1.equals(tripleKey.o1)) return false;
        if (!o2.equals(tripleKey.o2)) return false;

        // techically, the two checks below are the same
        // but the second one seems to run faster:
        //if (!o3.equals(tripleKey.o3)) return false;
        Iterator it = o3.keySet().iterator();
        while (it.hasNext()) {
            RDPObstacleInterface key = (RDPObstacleInterface) it.next();
            if (!tripleKey.o3.get(key).equals(o3.get(key))) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = o1.hashCode();
        result = 31 * result + o2.hashCode();
        return result;
    }
}