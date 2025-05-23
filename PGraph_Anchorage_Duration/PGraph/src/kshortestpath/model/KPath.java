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
package kshortestpath.model;

import kshortestpath.model.abstracts.IElementWithWeight;
import kshortestpath.model.abstracts.IVertex;

import java.awt.*;
import java.util.List;
import java.util.Vector;

/**
 * @author <a href='mailto:Yan.Qi@asu.edu'>Yan Qi</a>
 * @version $Revision: 673 $
 * @latest $Date: 2009-02-05 01:19:18 -0700 (Thu, 05 Feb 2009) $
 */
public class KPath implements IElementWithWeight
{
	List<IVertex> _vertex_list = new Vector<IVertex>();
	double _weight = -1;
	public Color color;

	public KPath(){};
	
	public KPath(List<IVertex> _vertex_list, double _weight)
	{
		this._vertex_list = _vertex_list;
		this._weight = _weight;
	}

	public double get_weight()
	{
		return _weight;
	}
	
	public void set_weight(double weight)
	{
		_weight = weight;
	}
	
	public List<IVertex> get_vertices()
	{
		return _vertex_list;
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object right)
	{
		if(right instanceof KPath)
		{
			KPath r_path = (KPath) right;
			return _vertex_list.equals(r_path._vertex_list);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return _vertex_list.hashCode();
	}
	
	public String toString()
	{
		return _vertex_list.toString()+":"+_weight;
	}
}
