/*
 *
 * Copyright (c) 2004-2008 Arizona State University.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ARIZONA STATE UNIVERSITY ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL ARIZONA STATE UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package pgraph.grid.tcgrid;


/**
 * Note this class borrows idea from the code in 
 * 	http://www.superliminal.com/sources/Pair.java.html.
 *
 * @author <a href='Yan.Qi@asu.edu'>Yan Qi</a>
 * @modified $Revision: 431 $
 * @latest $Id: Pair.java 431 2008-07-27 23:35:56Z qyan $
 *
 * @param <TYPE1>
 * @param <TYPE2>
 */
public class Quadriple<TYPE1, TYPE2,TYPE3,TYPE4>
{
    public TYPE1 o1;
    public TYPE2 o2;
    public TYPE3 o3;
    public TYPE4 o4;

    public Quadriple(TYPE1 o1, TYPE2 o2,TYPE3 o3,TYPE4 o4)
    { 
    	this.o1 = o1; this.o2 = o2; this.o3=o3;this.o4=o4;
    }

    public TYPE1 first()
    {
    	return o1;
    }
    
    public TYPE2 second()
    {
    	return o2;
    }

    public TYPE3 third()
    {
        return o3;
    }
	/* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     * Note, I don't know if it works well. Maybe in some tricky case, collision may happen.
     */
    public int hashCode() 
    {
        int code = 0;
        if(o1 != null)
            code = o1.hashCode();
        if(o2 != null)
            code = code/2 + o2.hashCode()/2;
        if(o3 != null)
            code = code/2 + o3.hashCode()/2;
        if(o4 != null)
            code = code/2 + o4.hashCode()/2;
        return code;
    }

    public static boolean same(Object o1, Object o2)
    {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    @SuppressWarnings("unchecked")
	public boolean equals(Object obj) 
    {
        if( ! (obj instanceof Quadriple))
            return false;
        Quadriple<TYPE1, TYPE2,TYPE3,TYPE4> p = (Quadriple<TYPE1, TYPE2,TYPE3,TYPE4>)obj;
        return same(p.o1, this.o1) && same(p.o2, this.o2)&& same(p.o3,this.o3)&& same(p.o4,this.o4);
    }

    public String toString() 
    {
        return "Pair{"+o1+", "+o2+", "+o3+","+o4+"}";
    }
}