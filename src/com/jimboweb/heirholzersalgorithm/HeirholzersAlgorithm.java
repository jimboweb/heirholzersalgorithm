package com.jimboweb.heirholzersalgorithm;

import java.util.*;

public class HeirholzersAlgorithm {

    public static void main(String[] args) {
	// write your code here
    }

    public <I extends Integer, N extends Node> Path<I> findPath(Graph<N> graph, Path<I> oddVertices, Path<I> path){
        Path<I> newPath = new Path<>();
        Optional<I> currentVertex = Optional.empty();
        if (path.isSemiEulerian()){
            if(path.isEmpty() || path.contains(oddVertices.getOptional(0))){
                currentVertex=oddVertices.getOptional(0);
            } else if(path.contains(oddVertices.getOptional(1))){
                currentVertex = oddVertices.getOptional(1);
            }
        } else {
            // TODO: 2/3/18 algorithm line 7
        }
        return path;
    }

    public <N extends Node, I extends Integer> Path<I> oddVertices(Graph<N> graph){
        Path<I> rtrn = new Path<>();
        Iterator<N> graphIterator = graph.iterator();
        while (graphIterator.hasNext()){
            Node n = graphIterator.next();
            if(!n.isEven()){
                rtrn.add((I)n.getVertex());
            }
        }
        return rtrn;
    }
}

class Node<V extends Integer, A extends List<V>>{
    private final V vertex;
    private List<V> adjacentVertices;

    public Node(V vertex, A vertices) {
        this.vertex = vertex;
        this.adjacentVertices = vertices;
    }

    public V getVertex() {
        return vertex;
    }

    public boolean hasAdjacent() {
        return !adjacentVertices.isEmpty();
    }

    public Optional<V> getFirstAdjacent() {
        if(hasAdjacent()) {
            return Optional.of(adjacentVertices.get(0));
        } else {
            return Optional.empty();
        }
    }

    public Optional<V> getAdjacentVertex (int i){
        if(adjacentVertices.size()>=i) {
            return Optional.empty();
        }
        return Optional.of(adjacentVertices.get(i));
    }

    public void removeAdjacentVertex(int i) {
        adjacentVertices.remove(i);
    }

    public boolean isEven(){
        return adjacentVertices.size()%2==0;
    }
}

class Graph<N extends Node>  extends ArrayList<N>{
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
}

class Path<I extends Integer> extends ArrayList<I>{

    public Optional<I> getOptional(int i){
        if (super.isEmpty()){
            return Optional.empty();
        } else {
            return Optional.of(super.get(i));
        }
    }


    public boolean isSemiEulerian(){
        return super.size()==2;
    }
}