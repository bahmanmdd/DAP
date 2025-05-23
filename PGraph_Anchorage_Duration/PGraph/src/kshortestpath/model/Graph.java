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

import kshortestpath.model.abstracts.IGraph;
import kshortestpath.model.abstracts.IVertex;
import pgraph.base.BaseDirectedGraph;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;

import java.io.*;
import java.util.*;

/**
 * @author <a href='mailto:Yan.Qi@asu.edu'>Yan Qi</a>
 * @version $Revision: 783 $
 * @latest $Date: 2009-06-19 12:19:27 -0700 (Fri, 19 Jun 2009) $
 */
public class Graph implements IGraph
{
	public final static double DISCONNECTED = Double.MAX_VALUE;
	
	// index of fan-outs of one vertex
	protected Map<Integer, Set<IVertex>> _fanout_vertices_index =
		new HashMap<Integer, Set<IVertex>>();
	
	// index for fan-ins of one vertex
	protected Map<Integer, Set<IVertex>> _fanin_vertices_index =
		new HashMap<Integer, Set<IVertex>>();
	
	// index for edge weights in the graph
	protected Map<Pair<Integer, Integer>, Double> _vertex_pair_weight_index = 
		new HashMap<Pair<Integer,Integer>, Double>();
	
	// index for vertices in the graph
	protected Map<Integer, IVertex> _id_vertex_index =
		new HashMap<Integer, IVertex>();
	
	// list of vertices in the graph 
	protected List<IVertex> _vertex_list = new Vector<IVertex>();
	
	// the number of vertices in the graph
	protected int _vertex_num = 0;
	
	// the number of arcs in the graph
	protected int _edge_num = 0;
	
	/**
	 * Constructor 1 
	 * @param data_file_name
	 */
	public Graph(final String data_file_name)
	{
		import_from_file(data_file_name);
	}
	
	/**
	 * Constructor 2
	 * 
	 * @param graph
	 */
	public Graph(final Graph graph_)
	{
		_vertex_num = graph_._vertex_num;
		_edge_num = graph_._edge_num;
		_vertex_list.addAll(graph_._vertex_list);
		_id_vertex_index.putAll(graph_._id_vertex_index);
		_fanin_vertices_index.putAll(graph_._fanin_vertices_index);
		_fanout_vertices_index.putAll(graph_._fanout_vertices_index);
		_vertex_pair_weight_index.putAll(graph_._vertex_pair_weight_index);
	}
	
	/**
	 * Default constructor 
	 */
	public Graph(){};
	
	/**
	 * Clear members of the graph.
	 */
	public void clear()
	{
		Vertex.reset();
		_vertex_num = 0;
		_edge_num = 0; 
		_vertex_list.clear();
		_id_vertex_index.clear();
		_fanin_vertices_index.clear();
		_fanout_vertices_index.clear();
		_vertex_pair_weight_index.clear();
	}


    public void import_from_basegraph(BaseDirectedGraph g)
    {
        clear();
        _vertex_num = g.vertices.size();
        for(BaseVertex v: g.vertices)
        {
            IVertex vertex = new Vertex((int)v.id);
            _vertex_list.add(vertex);
            _id_vertex_index.put(vertex.get_id(), vertex);
        }

        for (BaseEdge e:g.edgeSet)
        {
            add_edge((int)e.start.id,(int) e.end.id, e.getEdgeWeight());
        }
    }


    public void import_from_reader(Reader input)
    {
        // 0. Clear the variables
        clear();

        try
        {
            BufferedReader bufRead = new BufferedReader(input);

            boolean is_first_line = true;
            String line; 	// String that holds current file line

            // 2. Read first line
            line = bufRead.readLine();
            while(line != null)
            {
                // 2.1 skip the empty line
                if(line.trim().equals(""))
                {
                    line = bufRead.readLine();
                    continue;
                }

                // 2.2 generate nodes and edges for the graph
                if(is_first_line)
                {
                    //2.2.1 obtain the number of nodes in the graph

                    is_first_line = false;
                    _vertex_num = Integer.parseInt(line.trim());
                    for(int i=0; i<_vertex_num; ++i)
                    {
                        IVertex vertex = new Vertex();
                        _vertex_list.add(vertex);
                        _id_vertex_index.put(vertex.get_id(), vertex);
                    }

                }else
                {
                    //2.2.2 find a new edge and put it in the graph
                    String[] str_list = line.trim().split("\\s");

                    int start_vertex_id = Integer.parseInt(str_list[0]);
                    int end_vertex_id = Integer.parseInt(str_list[1]);
                    double weight = Double.parseDouble(str_list[2]);
                    add_edge(start_vertex_id, end_vertex_id, weight);
                }
                //
                line = bufRead.readLine();
            }
            bufRead.close();

        }catch (IOException e)
        {
            // If another exception is generated, print a stack trace
            e.printStackTrace();
        }

    }

	/**
	 * There is a requirement for the input graph. 
	 * The ids of vertices must be consecutive. 
	 *  
	 * @param data_file_name
	 */
	public void import_from_file(final String data_file_name)
	{
        FileReader input = null;
        try {
            input = new FileReader(data_file_name);
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        import_from_reader(input);
	}
    public void import_from_string(final String text)
    {
        StringReader input = new StringReader(text);
        import_from_reader(input);
    }

	/**
	 * Note that this may not be used externally, because some other members in the class
	 * should be updated at the same time. 
	 * 
	 * @param start_vertex_id
	 * @param end_vertex_id
	 * @param weight
	 */
	protected void add_edge(int start_vertex_id, int end_vertex_id, double weight)
	{
		// actually, we should make sure all vertices ids must be correct. 
		if(!_id_vertex_index.containsKey(start_vertex_id)
		|| !_id_vertex_index.containsKey(end_vertex_id) 
		|| start_vertex_id == end_vertex_id)
		{
			throw new IllegalArgumentException("The edge from "+start_vertex_id
					+" to "+end_vertex_id+" does not exist in the graph.");
		}
		
		// update the adjacent-list of the graph
		Set<IVertex> fanout_vertex_set = new HashSet<IVertex>();
		if(_fanout_vertices_index.containsKey(start_vertex_id))
		{
			fanout_vertex_set = _fanout_vertices_index.get(start_vertex_id);
		}
		fanout_vertex_set.add(_id_vertex_index.get(end_vertex_id));
		_fanout_vertices_index.put(start_vertex_id, fanout_vertex_set);
		
		//
		Set<IVertex> fanin_vertex_set = new HashSet<IVertex>();
		if(_fanin_vertices_index.containsKey(end_vertex_id))
		{
			fanin_vertex_set = _fanin_vertices_index.get(end_vertex_id);
		}
		fanin_vertex_set.add(_id_vertex_index.get(start_vertex_id));
		_fanin_vertices_index.put(end_vertex_id, fanin_vertex_set);

		// store the new edge 
		_vertex_pair_weight_index.put(
				new Pair<Integer, Integer>(start_vertex_id, end_vertex_id), 
				weight);
		
		++_edge_num;
	}
	
	/**
	 * Store the graph information into a file. 
	 * 
	 * @param file_name
	 */
	public void export_to_file(final String file_name)
	{
		//1. prepare the text to export
		StringBuffer sb = new StringBuffer();
		sb.append(_vertex_num+"\n\n");
		for(Pair<Integer, Integer> cur_edge_pair : _vertex_pair_weight_index.keySet())
		{
			int starting_pt_id = cur_edge_pair.first();
			int ending_pt_id = cur_edge_pair.second();
			double weight = _vertex_pair_weight_index.get(cur_edge_pair);
			sb.append(starting_pt_id+"	"+ending_pt_id+"	"+weight+"\n");
		}
		//2. open the file and put the data into the file. 
		Writer output = null;
		try {
			// use buffering
			// FileWriter always assumes default encoding is OK!
			output = new BufferedWriter(new FileWriter(new File(file_name)));
			output.write(sb.toString());
		}catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}catch(IOException e)
		{
			e.printStackTrace();
		}finally {
			// flush and close both "output" and its underlying FileWriter
			try
			{
				if (output != null) output.close();
			} catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.asu.emit.qyan.alg.model.abstracts.IGraph#get_adjacent_vertices(edu.asu.emit.qyan.alg.model.abstracts.IVertex)
	 */
	public Set<IVertex> get_adjacent_vertices(IVertex vertex)
	{
		return _fanout_vertices_index.containsKey(vertex.get_id()) 
				? _fanout_vertices_index.get(vertex.get_id()) 
				: new HashSet<IVertex>();
	}

	/* (non-Javadoc)
	 * @see edu.asu.emit.qyan.alg.model.abstracts.IGraph#get_precedent_vertices(edu.asu.emit.qyan.alg.model.abstracts.IVertex)
	 */
	public Set<IVertex> get_precedent_vertices(IVertex vertex)
	{
		return _fanin_vertices_index.containsKey(vertex.get_id()) 
				? _fanin_vertices_index.get(vertex.get_id()) 
				: new HashSet<IVertex>();
	}
	
	/* (non-Javadoc)
	 * @see edu.asu.emit.qyan.alg.model.abstracts.IGraph#get_edge_weight(edu.asu.emit.qyan.alg.model.abstracts.IVertex, edu.asu.emit.qyan.alg.model.abstracts.IVertex)
	 */
	public double get_edge_weight(IVertex source, IVertex sink)
	{
		return _vertex_pair_weight_index.containsKey(
					new Pair<Integer, Integer>(source.get_id(), sink.get_id()))? 
							_vertex_pair_weight_index.get(
									new Pair<Integer, Integer>(source.get_id(), sink.get_id())) 
						  : DISCONNECTED;
	}

	/**
	 * Set the number of vertices in the graph
	 * @param num
	 */
	public void set_vertex_num(int num)
	{
		_vertex_num = num;
	}
	
	/**
	 * Return the vertex list in the graph.
	 */
	public List<IVertex> get_vertex_list()
	{
		return _vertex_list;
	}
	
	/**
	 * Get the vertex with the input id.
	 * 
	 * @param id
	 * @return
	 */
	public IVertex get_vertex(int id)
	{
		return _id_vertex_index.get(id);
	}
}
