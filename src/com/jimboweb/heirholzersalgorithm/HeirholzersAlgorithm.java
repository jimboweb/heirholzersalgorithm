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
        Path path = new Path(graph.size());
        if(graph.isGraphEven(graph.size())){
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
    private Graph buildGraph(ArrayList<ArrayList<Integer>> inputs){
        int n = inputs.get(0).get(0);
        int m = inputs.get(0).get(0);
        Graph g = new Graph();
        for(int i=0;i<n;i++){
            Node newNode = new Node(i,new ArrayList<>(), new ArrayList<>());
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
     * @return the path of Integer vertices
     */
    public Path findPath(Graph graph, Path path){
        Integer currentVertex = findFirstVertex(graph, path);
        if(currentVertex==null){
            return path;
        }
        Path newPath = makeNewPath(graph,path,currentVertex);
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
     * @return endpoints of semi-Eulerian graph or open node of Eulerian graph
     */
    public Integer findFirstVertex(Graph graph, Path path){
        Integer currentVertex;
        ArrayList<Integer> oddVertices = graph.oddVertices(graph.size());
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
     * @return
     */
    private  Integer firstVertexIfEulerian(Graph graph, Path path){
        Integer currentVertex = null;
        Iterator<Node> graphIterator = graph.iterator();
        while (graphIterator.hasNext()){
            Node n = graphIterator.next();
            if(n.hasAdjacent()){
                if(path.isEmpty()||path.doesContain((Integer)n.getVertex())){
                    currentVertex = n.getVertex();
                }
            }
        }
        return currentVertex;
    }

    /**
     * finds the first vertex from endpoints of semiEulerianGrapph
     * @param path previous path
     * @param endPoints end points of semi Eulerian graph
     * @return
     */
    private  Integer firstVertexIfSemiEulerian(Path path, ArrayList<Integer> endPoints) {
        Integer currentVertex = null;
        Integer firstEndpoint = endPoints.get(0);
        Integer secondEndpoint = endPoints.get(1);
        if(path.isEmpty() || path.doesContain(firstEndpoint)){
            currentVertex=firstEndpoint;
        } else if(path.doesContain(secondEndpoint)){
            currentVertex = secondEndpoint;
        }
        return currentVertex;
    }

    /**
     * finds the next new path
     * @param graph what's left of the graph
     * @param path  the previous path
     * @param currentVertex the vertex to start on
     * @return the new path from a vertex of the old one
     */
    public  Path makeNewPath(Graph graph, Path path, Integer currentVertex){
        // TODO: 2/16/18
        Path newPath = new Path(graph.size());
        while(currentVertex!=null){
            Integer currentVertexNum = currentVertex;
            newPath.add(currentVertexNum);
            Node currentNode = graph.get(currentVertexNum);
            while(currentNode.hasSelfLoops()){
                newPath.add(currentVertexNum);
                graph.removeSelfLoop(currentVertexNum);
            }
            if(currentNode.hasAdjacent()){
                Integer nextVertex = currentNode.getFirstAdjacent();
                // TODO: 2/16/18 for first and last vertices check for oddVertices
                // probably first vertex can be something like an isFirstVertex boolean
                // and the last vertex can be found by checking if nextVertex has adjacent
                currentNode.removeFirstAdjacent();
                graph.getNode(nextVertex).removeIncomingVertexByVertexNumber(currentNode.getVertex());
                currentVertex=nextVertex;
            } else {
                currentVertex = null;
            }
        }
        return newPath;

    }

    /**
     * connects the previous path to the new one
     * @param path previous path
     * @param newPath path to add
     * @return the previous path joined to new path
     */
    private Path addNewPath(Path path, Path newPath) {
        Path adjustedPath = new Path(path.getGraphSize());
        boolean newPathNotAdded = true;
        Integer start = newPath.getStart();
        for(Integer vertex:path){
            adjustedPath.add(vertex);
            if(newPathNotAdded && vertex.equals(start)){
                for(int j=1;j<newPath.size();j++){
                    Integer newVertex = newPath.get(j);
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
  */
class Graph  extends ArrayList<Node>{

    public Graph(){

    }
    /**
     * get Node of index as optional
     * @param n index to get
     * @return Node or Optional.empty if it's not there
     */

    public void addSelfLoop(int n){
        getNode(n).addSelfLoop();
    }

    public void removeSelfLoop(int n){
        getNode(n).removeSelfLoop();
    }

    public void addNode(Node n){
        this.add(n);
    }

    public Node getNode(int i){
        return get(i);
    }

    @Override
    public Iterator<Node> iterator() {
        return super.iterator();
    }

    // TODO: 2/16/18 We can get rid of this loop. It's worsening the efficiency by a factor of O(n)
    public  Path oddVertices(int graphSize){
        Path rtrn = new Path(graphSize);
        Iterator<Node> graphIterator = (Iterator<Node>)iterator();
        while (graphIterator.hasNext()){
            Node n = graphIterator.next();
            if(!n.isEven()){
                rtrn.add((Integer)n.getVertex());
            }
        }
        return rtrn;
    }

    public boolean isGraphEven(int graphSize){
        Path oddVertices = oddVertices(graphSize); //graph size doesn't matter here
        return oddVertices.isEmpty();
    }

    public void addEdge(int from, int to){
        getNode(from).addAdjacentVertex(to);
        getNode(to).addIncomingVertex(from);

    }

}

/**
 * A directed node
 */
class Node {
    private final Integer vertex;
    private List<Integer> adjacentVertices;
    private List<Integer> incomingVertices;
    private int selfLoops = 0;
    /**
     *
     * @param vertex the number of the vertex
     * @param vertices adjacent vertices
     * @param incomingVertices incoming vertices
     */
    public Node(Integer vertex, List<Integer> vertices, List<Integer> incomingVertices) {
        this.vertex = vertex;
        this.adjacentVertices = vertices;
        this.incomingVertices = incomingVertices;
    }

    /**
     * constructor with just the vertex
     * @param vertex the vertex number
     */
    public Node(Integer vertex){
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
    public Integer getVertex() {
        return vertex;
    }

    /**
     *
     * @return true if adjecent vertices, false if not
     */
    public boolean hasAdjacent() {
        return !adjacentVertices.isEmpty();
    }

    // TODO: 2/16/18 change these to to have a updateOddVertices boolean to change the oddVertices data structures
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
    public Integer getFirstAdjacent() {
        if(hasAdjacent()) {
            return adjacentVertices.get(0);
        } else {
            return null;
        }
    }

    /**
     *
     * @param i index of adjacent vertex
     * @return vertex number of adjacent vertex
     */
    public Integer getAdjacentVertex (int i){
        if(adjacentVertices.size()>=i) {
            return null;
        }
        return adjacentVertices.get(i);
    }

    /**
     * removes vertex of index
     * @param i index to remove
     */
    public void removeAdjacentVertex(int i) {
        adjacentVertices.remove(i);
    }

    public void addAdjacentVertex(Integer i){
        adjacentVertices.add(i);
    }

    public void addIncomingVertex(Integer i){
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
 */
class Path extends ArrayList<Integer>{
    private boolean[] doesContain;
    private Integer val;

    @Override
    public boolean add(Integer val){
        boolean rtrn = super.add(val);
        int valInt = (int)val;
        if(valInt>doesContain.length){
            throw new IndexOutOfBoundsException("contains array is not big enough for " + val);
        }
        if(rtrn){
            doesContain[valInt] = true;
        }
        return rtrn;
    }


    public boolean doesContain(Integer val){
        int valInt = (int)val;
        return doesContain[valInt];
    }

    public Path(Integer size) {
        doesContain = new boolean[size];
    }

    public Path(int size) {
        doesContain = new boolean[size];
    }

    /**
     *
     * @return first vertex or Optional.empty if path is empty
     */
    public Integer getStart(){
        if(isEmpty()){
            return null;
        } else {
            return super.get(0);
        }
    }

    public int getGraphSize(){
        return doesContain.length;
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