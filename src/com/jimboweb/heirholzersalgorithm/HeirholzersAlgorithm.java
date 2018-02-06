package com.jimboweb.heirholzersalgorithm;


import java.io.IOException;
import java.util.*;


public class HeirholzersAlgorithm {

    public static void main(String[] args) {
        new Thread(null, new Runnable() {
            public void run() {
                try {
                    new HeirholzersAlgorithm().run();
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
        }, "1", 1 << 26).start();
    }
    public void run() throws IOException {
        Inputter inputter = new ConsoleInput();
        Outputter outputter = new ConsoleOutput();
        hierholzersAlgorithm(inputter,outputter);
    }

    public void hierholzersAlgorithm(Inputter inputter, Outputter outputter){
        ArrayList<ArrayList<Integer>> input = inputter.getInput();
        Graph graph = buildGraph(input);
        Path<Integer> path = new Path<>();
        if(graph.isGraphEven()){
            path = findPath(graph, path);
            String output = "1\n";
            for(int i=0;i<path.size()-1;i++){
                int outputNode = path.get(i) + 1;
                output += outputNode + " ";
            }
            outputter.output(output);
        } else {
            outputter.output("0");
        }

    }

    /**
     * creates basic graph from inputs
     * @param inputs ArrayList of Integers from input
     * @return
     */
    private Graph<Node> buildGraph(ArrayList<ArrayList<Integer>> inputs){
        int n = inputs.get(0).get(0);
        int m = inputs.get(0).get(0);
        Graph<Node> g = new Graph<>();
        for(int i=0;i<n;i++){
            Node<Integer,ArrayList<Integer>> newNode = new Node(i,new ArrayList<>(), new ArrayList<>());
            g.addNode(newNode);
        }
        for(int i=1;i<inputs.size();i++){
            int from = inputs.get(i).get(0)-1;
            int to = inputs.get(i).get(1)-1;
            if(from==to){
                g.addSelfLoop(from);
            }else {
                g.addEdge(from, to);
            }
        }
        return g;
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
     * @return endpoints of semi-Eulerian graph or open node of Eulerian graph
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
            V currentVertexNum = currentVertex.get();
            newPath.add(currentVertexNum);
            N currentNode = graph.get(currentVertexNum);
            while(currentNode.hasSelfLoops()){
                newPath.add(currentVertexNum);
                graph.removeSelfLoop(currentVertexNum);
            }
            if(currentNode.hasAdjacent()){
                Optional<V> nextVertex = currentNode.getFirstAdjacent();
                currentNode.removeFirstAdjacent();
                graph.getNode(nextVertex.orElseThrow(EmptyStackException::new)).removeIncomingVertexByVertexNumber(currentNode.getVertex());
                currentVertex=nextVertex;
            } else {
                currentVertex = Optional.empty();
            }
        }
        return newPath;

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
 * List of Nodes
 * @param <N> object of type Node
 */
class Graph<N extends Node>  extends ArrayList<N>{

    public Graph(){

    }
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

    public void addSelfLoop(int n){
        getNode(n).addSelfLoop();
    }

    public void removeSelfLoop(int n){
        getNode(n).removeSelfLoop();
    }

    public void addNode(N n){
        this.add(n);
    }

    public N getNode(int i){
        return get(i);
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

    public <V extends Integer> boolean isGraphEven(){
        Path<V> oddVertices = oddVertices();
        return oddVertices.isEmpty();
    }

    public void addEdge(int from, int to){
        getNode(from).addAdjacentVertex(to);
        getNode(to).addIncomingVertex(from);

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
    private int selfLoops = 0;
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

    public boolean hasSelfLoops(){
        return selfLoops>0;
    }

    public void addSelfLoop(){
        selfLoops++;
    }

    public void removeSelfLoop(){
        selfLoops--;
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

    public void removeFirstAdjacent(){
        if(!adjacentVertices.isEmpty()){
            adjacentVertices.remove(0);
        }
    }

    public void removeIncomingVertexByVertexNumber(int vertexNumber){
        int index = incomingVertices.indexOf(vertexNumber);
        if(index!=-1){
            incomingVertices.remove(index);
        }
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

    public void addAdjacentVertex(V i){
        adjacentVertices.add(i);
    }

    public void addIncomingVertex(V i){
        incomingVertices.add(i);
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

interface Inputter{
    public ArrayList<ArrayList<Integer>> getInput();
}

class ConsoleInput implements Inputter {
    public ArrayList<ArrayList<Integer>> getInput(){
        Scanner scanner = new Scanner(System.in);
        ArrayList<ArrayList<Integer>> inputs = new ArrayList<>();
        ArrayList<Integer> in = new ArrayList<>();
        in.add(scanner.nextInt());
        in.add(scanner.nextInt());
        if (in.get(0) == 0 || in.get(1) == 0) {
            System.out.println("0");
            System.exit(0);
        }
        inputs.add(in);
        for (int i = 0; i < inputs.get(0).get(1); i++) {
            in = new ArrayList<>();
            in.add(scanner.nextInt());
            in.add(scanner.nextInt());
            inputs.add(in);
        }
        return inputs;
    }
}

interface Outputter{
    public void output(String output);
}

class ConsoleOutput implements Outputter{

    @Override
    public void output(String outPut) {
        System.out.println(outPut);
    }
}