package com.jimboweb.heirholzersalgorithm;


import java.io.IOException;
import java.util.*;


public class HeirholzersAlgorithm {
    boolean countLoops = false;
    private int operations = 0;//debug

    public int getOperations() {//debug
        return operations;
    }

    public static void main(String[] args) {
        try {
            new HeirholzersAlgorithm().run();
        } catch (IOException ex) {
            System.out.println(ex);
        }
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
            Queue<Integer> pathQueue = path.getQueue();
            while(pathQueue.size()>1){
                int outputNode = pathQueue.poll();
                outputNode++;
                output += outputNode + " ";
                if(countLoops) operations++; //debug
            }
            outputter.output(output);
        } else {
            outputter.output("0");
        }
        if(countLoops) operations +=graph.getOperations();
        if(countLoops) graph.clearLoopCount();
    }

    /**
     * creates basic graph from inputs
     * @param inputs ArrayList of Integers from input
     * @return
     */
    private Graph buildGraph(ArrayList<ArrayList<Integer>> inputs){
        int n = inputs.get(0).get(0);
        int m = inputs.get(0).get(0);
        Graph g = new Graph(n);
        for(int i=0;i<n;i++){
            Node newNode = new Node(i);
            g.addNode(newNode);
            if(countLoops) operations++; //debug

        }
        for(int i=1;i<inputs.size();i++){
            int from = inputs.get(i).get(0)-1;
            int to = inputs.get(i).get(1)-1;
            if(from==to){
                g.addSelfLoop(from);
            }else {
                g.addEdge(from, to);
            }
            if(countLoops) operations++; //debug

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
        if(countLoops) operations++; //debug

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
        if(countLoops) operations++; //debug

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
            if(countLoops) operations++; //debug

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
        if(countLoops) operations++; //debug

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
            Node currentNode = graph.getNode(currentVertexNum);
            if(countLoops) operations++; //debug

            while(currentNode.hasSelfLoops()){
                newPath.add(currentVertexNum);
                graph.removeSelfLoop(currentVertexNum);
                if(countLoops) operations++; //debug

            }
            if(currentNode.hasAdjacent()){
                Integer nextVertex = currentNode.popFirstAdjacent();
                graph.getNode(nextVertex).removeIncomingVertex();
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
        for(Integer vertex:path.getQueue()){
            adjustedPath.add(vertex);
            if(newPathNotAdded && vertex.equals(start)){
                for(int newVertex:newPath.getQueue()){
                    adjustedPath.add(newVertex);
                }
                newPathNotAdded = false;
            }
            if(countLoops) operations++; //debug

        }
        path = adjustedPath;
        return path;
    }


}

/**
 * List of Nodes
  */
class Graph  {
    private int operations;//debug
    boolean countLoops = false;

    public int getOperations() {//debug
        return operations;
    }
    public void clearLoopCount(){
        operations =0;
    }

    ArrayList<Node> nodes;
    public Graph(int size){
        nodes = new ArrayList<>(size);
    }
    public int size(){
        return nodes.size();
    }

    public void addSelfLoop(int n){
        getNode(n).addSelfLoop();
    }

    public void removeSelfLoop(int n){
        getNode(n).removeSelfLoop();
    }

    public void addNode(Node n){
        nodes.add(n);
    }

    public Node getNode(int i){
        return nodes.get(i);
    }

    public Iterator<Node> iterator() {
        return nodes.iterator();
    }

    public  ArrayList<Integer> oddVertices(int graphSize){
        ArrayList<Integer> rtrn = new ArrayList<>(graphSize);
        Iterator<Node> graphIterator = (Iterator<Node>)iterator();
        while (graphIterator.hasNext()){
            Node n = graphIterator.next();
            if(!n.isEven()){
                rtrn.add((Integer)n.getVertex());
            }
            if(countLoops) operations++;
        }
        return rtrn;
    }

    public boolean isGraphEven(int graphSize){
        ArrayList<Integer> oddVertices = oddVertices(graphSize);
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
    private Deque<Integer> adjacentVertices;
    private int incomingVertices;
    private int selfLoops = 0;
    /**
     *
     * @param vertex the number of the vertex
     * @param vertices adjacent vertices
     * @param incomingVertices incoming vertices
     */
    public Node(Integer vertex, Deque<Integer> vertices, int incomingVertices) {
        this.vertex = vertex;
        this.adjacentVertices = vertices;
        this.incomingVertices = incomingVertices;
    }

    public Node(Integer vertex){
        this.vertex = vertex;
        this.adjacentVertices = new ArrayDeque<>();
        this.incomingVertices = 0;
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

    public void removeIncomingVertex(){
        incomingVertices--;
    }

    /**
     *
     * @return first adjacent vertex
     */
    public Integer popFirstAdjacent() {
        if(hasAdjacent()) {
            return adjacentVertices.pop();
        } else {
            return null;
        }
    }

    /**
     *
     * @param i index of adjacent vertex
     * @return vertex number of adjacent vertex
     */

    public void addAdjacentVertex(Integer i){
        adjacentVertices.push(i);
    }

    public void addIncomingVertex(Integer i){
        incomingVertices++;
    }

    /**
     *
     * @return true if in==out, false if not
     */
    public boolean isEven(){
        return incomingVertices==adjacentVertices.size();
    }
}

/**
 * ArrayList of vertex numbers, will be final return of findPath method
 */
class Path {
    private Queue<Integer> queue;
    private boolean[] doesContain;
    private Integer val;


    public int size(){
        return queue.size();
    }

    public boolean isEmpty(){
        return queue.isEmpty();
    }

    public Queue<Integer> getQueue() {
        return queue;
    }

    public Path(int size) {
        doesContain = new boolean[size];
        queue = new LinkedList<>();
    }

    public boolean add(Integer val){
        boolean rtrn = queue.add(val);
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


    /**
     *
     * @return first vertex or Optional.empty if path is empty
     */
    public Integer getStart(){
        if(queue.isEmpty()){
            return null;
        } else {
            return queue.poll();
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