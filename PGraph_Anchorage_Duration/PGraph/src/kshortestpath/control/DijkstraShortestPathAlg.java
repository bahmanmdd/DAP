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

package kshortestpath.control;

import kshortestpath.model.abstracts.IGraph;
import kshortestpath.model.Graph;
import kshortestpath.model.KPath;
import kshortestpath.model.abstracts.IVertex;

import java.util.*;


/**
 * @author <a href='mailto:Yan.Qi@asu.edu'>Yan Qi</a>
 * @version $Revision: 430 $
 * @latest $Date: 2008-07-27 16:31:56 -0700 (Sun, 27 Jul 2008) $
 */
public class DijkstraShortestPathAlg
{
	// Input
	IGraph _graph = null;

	// Intermediate variables
	Set<IVertex> _determined_vertex_set = new HashSet<IVertex>();
	PriorityQueue<IVertex> _vertex_candidate_queue = new PriorityQueue<IVertex>();
	Map<IVertex, Double> _start_vertex_distance_index = new HashMap<IVertex, Double>();
	
	Map<IVertex, IVertex> _predecessor_index = new HashMap<IVertex, IVertex>();

	/**
	 * Default constructor.
	 * @param graph
	 */
	public DijkstraShortestPathAlg(final IGraph graph)
	{
		_graph = graph;
	}

	/**
	 * Clear intermediate variables.
	 */
	public void clear()
	{
		_determined_vertex_set.clear();
		_vertex_candidate_queue.clear();
		_start_vertex_distance_index.clear();
		_predecessor_index.clear();
	}

	/**
	 * Getter for the distance in terms of the start vertex
	 * 
	 * @return
	 */
	public Map<IVertex, Double> get_start_vertex_distance_index()
	{
		return _start_vertex_distance_index;
	}

	/**
	 * Getter for the index of the predecessors of vertices
	 * @return
	 */
	public Map<IVertex, IVertex> get_predecessor_index()
	{
		return _predecessor_index;
	}

	/**
	 * Construct a tree rooted at "root" with 
	 * the shortest paths to the other vertices.
	 * 
	 * @param root
	 */
	public void get_shortest_path_tree(IVertex root)
	{
		determine_shortest_paths(root, null, true);
	}
	
	/**
	 * Construct a flower rooted at "root" with 
	 * the shortest paths from the other vertices.
	 * 
	 * @param root
	 */
	public void get_shortest_path_flower(IVertex root)
	{
		determine_shortest_paths(null, root, false);
	}
	
	/**
	 * Do the work
	 */
	protected void determine_shortest_paths(IVertex source_vertex,
                                            IVertex sink_vertex, boolean is_source2sink)
	{
		// 0. clean up variables
		clear();
		
		// 1. initialize members
        IVertex end_vertex = is_source2sink ? sink_vertex : source_vertex;
        IVertex start_vertex = is_source2sink ? source_vertex : sink_vertex;
		_start_vertex_distance_index.put(start_vertex, 0d);
		start_vertex.set_weight(0d);
		_vertex_candidate_queue.add(start_vertex);

		// 2. start searching for the shortest path
		while(!_vertex_candidate_queue.isEmpty())
		{
            IVertex cur_candidate = _vertex_candidate_queue.poll();

			if(cur_candidate.equals(end_vertex)) break;

			_determined_vertex_set.add(cur_candidate);

			_improve_to_vertex(cur_candidate, is_source2sink);
		}
	}

	/**
	 * Update the distance from the source to the concerned vertex.
	 * @param vertex
	 */
	private void _improve_to_vertex(IVertex vertex, boolean is_source2sink)
	{
		// 1. get the neighboring vertices 
		Set<IVertex> neighbor_vertex_list = is_source2sink ?
			_graph.get_adjacent_vertices(vertex) : _graph.get_precedent_vertices(vertex);
			
		// 2. update the distance passing on current vertex
		for(IVertex cur_adjacent_vertex : neighbor_vertex_list)
		{
			// 2.1 skip if visited before
			if(_determined_vertex_set.contains(cur_adjacent_vertex)) continue;
			
			// 2.2 calculate the new distance
			double distance = _start_vertex_distance_index.containsKey(vertex)?
					_start_vertex_distance_index.get(vertex) : Graph.DISCONNECTED;
					
			distance += is_source2sink ? _graph.get_edge_weight(vertex, cur_adjacent_vertex)
					: _graph.get_edge_weight(cur_adjacent_vertex, vertex);

			// 2.3 update the distance if necessary
			if(!_start_vertex_distance_index.containsKey(cur_adjacent_vertex) 
			|| _start_vertex_distance_index.get(cur_adjacent_vertex) > distance)
			{
				_start_vertex_distance_index.put(cur_adjacent_vertex, distance);

				_predecessor_index.put(cur_adjacent_vertex, vertex);
				
				cur_adjacent_vertex.set_weight(distance);
				_vertex_candidate_queue.add(cur_adjacent_vertex);
			}
		}
	}
	
	/**
	 * Note that, the source should not be as same as the sink! (we could extend 
	 * this later on)
	 *  
	 *
     * @param source_vertex
     * @param sink_vertex
     * @return
	 */
	public KPath get_shortest_path(IVertex source_vertex, IVertex sink_vertex)
	{
		determine_shortest_paths(source_vertex, sink_vertex, true);
		//
		List<IVertex> vertex_list = new Vector<IVertex>();
		double weight = _start_vertex_distance_index.containsKey(sink_vertex) ?  
			_start_vertex_distance_index.get(sink_vertex) : Graph.DISCONNECTED;
		if(weight != Graph.DISCONNECTED)
		{
            IVertex cur_vertex = sink_vertex;
			do{
				vertex_list.add(cur_vertex);
				cur_vertex = _predecessor_index.get(cur_vertex);
			}while(cur_vertex != null && cur_vertex != source_vertex);
			//
			vertex_list.add(source_vertex);
			Collections.reverse(vertex_list);
		}
		//
		return new KPath(vertex_list, weight);
	}
	
	/// for updating the cost
	
	/**
	 * Calculate the distance from the target vertex to the input 
	 * vertex using forward star form. 
	 * (FLOWER)
	 * 
	 * @param vertex
	 */
	public KPath update_cost_forward(IVertex vertex)
	{
		double cost = Graph.DISCONNECTED;
		
		// 1. get the set of successors of the input vertex
		Set<IVertex> adj_vertex_set = _graph.get_adjacent_vertices(vertex);
		
		// 2. make sure the input vertex exists in the index
		if(!_start_vertex_distance_index.containsKey(vertex))
		{
			_start_vertex_distance_index.put(vertex, Graph.DISCONNECTED);
		}
		
		// 3. update the distance from the root to the input vertex if necessary
		for(IVertex cur_vertex : adj_vertex_set)
		{
			// 3.1 get the distance from the root to one successor of the input vertex
			double distance = _start_vertex_distance_index.containsKey(cur_vertex)?
					_start_vertex_distance_index.get(cur_vertex) : Graph.DISCONNECTED;
					
			// 3.2 calculate the distance from the root to the input vertex
			distance += _graph.get_edge_weight(vertex, cur_vertex);
			//distance += ((VariableGraph)_graph).get_edge_weight_of_graph(vertex, cur_vertex);
			
			// 3.3 update the distance if necessary 
			double cost_of_vertex = _start_vertex_distance_index.get(vertex);
			if(cost_of_vertex > distance)
			{
				_start_vertex_distance_index.put(vertex, distance);
				_predecessor_index.put(vertex, cur_vertex);
				cost = distance;
			}
		}
		
		// 4. create the sub_path if exists
		KPath sub_path = null;
		if(cost < Graph.DISCONNECTED) 
		{
			sub_path = new KPath();
			sub_path.set_weight(cost);
			List<IVertex> vertex_list = sub_path.get_vertices();
			vertex_list.add(vertex);

            IVertex sel_vertex = _predecessor_index.get(vertex);
			while(sel_vertex != null)
			{
				vertex_list.add(sel_vertex);
				sel_vertex = _predecessor_index.get(sel_vertex);
			}
		}
		
		return sub_path;
	}
	
	/**
	 * Correct costs of successors of the input vertex using backward star form.
	 * (FLOWER)
	 * 
	 * @param vertex
	 */
	public void correct_cost_backward(IVertex vertex)
	{
		// 1. initialize the list of vertex to be updated
		List<IVertex> vertex_list = new LinkedList<IVertex>();
		vertex_list.add(vertex);
		
		// 2. update the cost of relevant precedents of the input vertex
		while(!vertex_list.isEmpty())
		{
            IVertex cur_vertex = vertex_list.remove(0);
			double cost_of_cur_vertex = _start_vertex_distance_index.get(cur_vertex);
			
			Set<IVertex> pre_vertex_set = _graph.get_precedent_vertices(cur_vertex);
			for(IVertex pre_vertex : pre_vertex_set)
			{
				double cost_of_pre_vertex = _start_vertex_distance_index.containsKey(pre_vertex) ?
						_start_vertex_distance_index.get(pre_vertex) : Graph.DISCONNECTED;
						
				double fresh_cost = cost_of_cur_vertex + _graph.get_edge_weight(pre_vertex, cur_vertex);
				//double fresh_cost = cost_of_cur_vertex + ((VariableGraph)_graph).get_edge_weight_of_graph(pre_vertex, cur_vertex);
				if(cost_of_pre_vertex > fresh_cost)
				{
					_start_vertex_distance_index.put(pre_vertex, fresh_cost);
					_predecessor_index.put(pre_vertex, cur_vertex);
					vertex_list.add(pre_vertex);
				}
			}
		}
	}
	
}
