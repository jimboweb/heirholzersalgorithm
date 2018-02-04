package com.jimboweb.heirholzersalgorithm;

import java.util.*;

public class HeirholzersAlgorithm {

    public static void main(String[] args) {
	// write your code here
    }

    /**
     * finds the Eulerian path
     * @param graph contains ArrayList of Nodes
     * @param path previous path if there is one
     * @param <V> V is an object of type Integer which is a vertex, an index of a node
     * @param <N> N is an object of type Node
     * @return the path of Integer vertices
     */
    public <V extends Integer, N extends Node> Path<V> findPath(Graph<N> graph, Path<V> path){
        Optional<V> currentVertex = findFirstVertex(graph, path);
        if(!currentVertex.isPresent()){
            return path;
        }
        Path<V> newPath = makeNewPath(graph,path,currentVertex);
        if(path.isEmpty()){
            path=newPath;
        } else if(newPath.size()>1){
            path = addNewPath(path, newPath);
        }
        path = findPath(graph,path);
        return path;
    }


    /**
     * find the first vertex in the new path
     * @param graph graph of what's left
     * @param path
     * @param <V> V is an object of type Integer which is a vertex, an index of a node
     * @param <N> N is an object of type Node
     * @return
     */
    public <V extends Integer, N extends Node> Optional<V> findFirstVertex(Graph<N> graph, Path<V> path){
        Optional<V> currentVertex;
        ArrayList<V> oddVertices = graph.oddVertices();
        if (oddVertices.size()==2){
            currentVertex = firstVertexIfSemiEulerian(path, oddVertices);
        } else {
            currentVertex = firstVertexIfEulerian(graph,path);
        }
        return currentVertex;
    }

    /**
     * first vertex if path is Eulerian
     * @param graph what's left of the graph
     * @param path the previous path
     * @param <V> V is an object of type Integer which is a vertex, an index of a node
     * @param <N> N is an object of type Node
     * @return
     */
    private <V extends Integer, N extends Node> Optional<V> firstVertexIfEulerian(Graph<N> graph, Path<V> path){
        Optional<V> currentVertex = Optional.empty();
        Iterator<N> graphIterator = graph.iterator();
        while (graphIterator.hasNext()){
            N n = graphIterator.next();
            if(n.hasAdjacent()){
                if(path.isEmpty()||path.contains(n)){
                    currentVertex = Optional.of((V)n.getVertex());
                }
            }
        }
        return currentVertex;
    }

    /**
     * finds the first vertex from endpoints of semiEulerianGrapph
     * @param path previous path
     * @param endPoints end points of semi Eulerian graph
     * @param <V> V is an object of type Integer which is a vertex, an index of a node
     * @return
     */
    private <V extends Integer> Optional<V> firstVertexIfSemiEulerian(Path<V> path, ArrayList<V> endPoints) {
        Optional<V> currentVertex = Optional.empty();
        Optional<V> firstEndpoint = Optional.of(endPoints.get(0));
        Optional<V> secondEndpoint = Optional.of(endPoints.get(1));
        if(path.isEmpty() || path.contains(firstEndpoint)){
            currentVertex=firstEndpoint;
        } else if(path.contains(secondEndpoint)){
            currentVertex = secondEndpoint;
        }
        return currentVertex;
    }

    /**
     * finds the next new path
     * @param graph what's left of the graph
     * @param path  the previous path
     * @param currentVertex the vertex to start on
     * @param <V> V is an object of type Integer which is a vertex, an index of a node
     * @param <N> N is an object of type Node
     * @return the new path from a vertex of the old one
     */
    public <V extends Integer, N extends Node> Path<V> makeNewPath(Graph<N> graph, Path<V> path, Optional<V> currentVertex){
        Path<V> newPath = new Path<>();
        while(currentVertex.isPresent()){
            newPath.add(currentVertex.get());
            N currentNode = graph.get(currentVertex.get());
            if(currentNode.hasAdjacent()){
                Optional<V> nextVertex = currentNode.getFirstAdjacent();
                currentNode.removeAdjacentVertex(nextVertex.get());
                currentVertex=nextVertex;
            } else {
                currentVertex = Optional.empty();
            }
        }
        return path;

    }

    /**
     * connects the previous path to the new one
     * @param path previous path
     * @param newPath path to add
     * @param <V> V is an object of type Integer which is a vertex, an index of a node
     * @return the previous path joined to new path
     */
    private <V extends Integer> Path<V> addNewPath(Path<V> path, Path<V> newPath) {
        Path<V> adjustedPath = new Path<>();
        boolean newPathNotAdded = true;
        for(V vertex:path){
            adjustedPath.add(vertex);
            if(newPathNotAdded && vertex.equals(newPath.getStart())){
                for(V newVertex:newPath){
                    adjustedPath.add(newVertex);
                }
                newPathNotAdded = false;
            }
        }
        path = adjustedPath;
        return path;
    }


}

/**
 * A directed node
 * @param <V> V is an object of type Integer which is a vertex, an index of a node
 * @param <A> A is a list of adjacent vertices coming in or out
 */
class Node<V extends Integer, A extends List<V>>{
    private final V vertex;
    private A adjacentVertices;
    private A incomingVertices;

    /**
     *
     * @param vertex the number of the vertex
     * @param vertices adjacent vertices
     * @param incomingVertices incoming vertices
     */
    public Node(V vertex, A vertices, A incomingVertices) {
        this.vertex = vertex;
        this.adjacentVertices = vertices;
        this.incomingVertices = incomingVertices;
    }

    /**
     * constructor with just the vertex
     * @param vertex the vertex number
     */
    public Node(V vertex){
        this.vertex = vertex;
    }

    /**
     *
     * @return the vertex number
     */
    public V getVertex() {
        return vertex;
    }

    /**
     *
     * @return true if adjecent vertices, false if not
     */
    public boolean hasAdjacent() {
        return !adjacentVertices.isEmpty();
    }

    /**
     *
     * @return first adjacent vertex
     */
    public Optional<V> getFirstAdjacent() {
        if(hasAdjacent()) {
            return Optional.of(adjacentVertices.get(0));
        } else {
            return Optional.empty();
        }
    }

    /**
     *
     * @param i index of adjacent vertex
     * @return vertex number of adjacent vertex
     */
    public Optional<V> getAdjacentVertex (int i){
        if(adjacentVertices.size()>=i) {
            return Optional.empty();
        }
        return Optional.of(adjacentVertices.get(i));
    }

    /**
     * removes vertex of index
     * @param i index to remove
     */
    public void removeAdjacentVertex(int i) {
        adjacentVertices.remove(i);
    }

    /**
     *
     * @return true if in==out, false if not
     */
    public boolean isEven(){
        return incomingVertices.size()==adjacentVertices.size();
    }
}

/**
 * List of Nodes
 * @param <N> object of type Node
 */
class Graph<N extends Node>  extends ArrayList<N>{
    /**
     * get Node of index as optional
     * @param i index to get
     * @return Node or Optional.empty if it's not there
     */
    public Optional<N> getOptional(int i){
        if (super.isEmpty()){
            return Optional.empty();
        } else {
            return Optional.of(super.get(i));
        }
    }

    @Override
    public Iterator<N> iterator() {
        return super.iterator();
    }
    public <N extends Node, V extends Integer> Path<V> oddVertices(){
        Path<V> rtrn = new Path<>();
        Iterator<N> graphIterator = (Iterator<N>)iterator();
        while (graphIterator.hasNext()){
            N n = graphIterator.next();
            if(!n.isEven()){
                rtrn.add((V)n.getVertex());
            }
        }
        return rtrn;
    }

    private <N extends Node, V extends Integer> ArrayList<V> semiEulerianVertices(){
        ArrayList<V> oddVertices = oddVertices();
        if(oddVertices.size()==2){
            return oddVertices;
        } else {
            return new ArrayList<>();
        }
    }

}

/**
 * ArrayList of vertex numbers, will be final return of findPath method
 * @param <V> V is an object of type Integer which is a vertex, an index of a node
 */
class Path<V extends Integer> extends ArrayList<V>{
    /**
     * get vertex as optional integer
     * @param i index to get
     * @return vertex or Optional.empty if it's not there
     */
    public Optional<V> getOptional(int i){
        if (super.isEmpty()){
            return Optional.empty();
        } else {
            return Optional.of(super.get(i));
        }
    }

    /**
     *
     * @return first vertex or Optional.empty if path is empty
     */
    public Optional<V> getStart(){
        if(isEmpty()){
            return Optional.empty();
        } else {
            return Optional.of(super.get(0));
        }
    }

}