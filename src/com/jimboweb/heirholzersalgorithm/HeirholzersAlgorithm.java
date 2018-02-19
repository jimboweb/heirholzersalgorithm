//package com.jimboweb.heirholzersalgorithm;


import java.io.IOException;
import java.util.*;


// FIXME: 2/18/18 stack overflow on input: 4 5
//1 3
//2 3
//3 4
//4 2
//3 1
// currentVertex isn't going to null after full path is found
// seems oddVertices isn't correctly being set

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
            Node newNode = new Node(i,new ArrayList<>(), new ArrayList<>(), g);
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
        for(Node node:g.getNodes()){
            node.updateOdd();
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
        Integer[] oddVertices = graph.getOddVertices();
        if (graph.isSemiEulerian()){
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
    private  Integer firstVertexIfSemiEulerian(Path path, Integer[] endPoints) {
        Integer currentVertex = null;
        Integer firstEndpoint = endPoints[0];
        Integer secondEndpoint = endPoints[1];
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
        Path newPath = new Path(graph.size());
        while(currentVertex!=null){
            Node currentNode = addCurrentVertexAndSelfLoops(graph, currentVertex, newPath);
            boolean isFirstLoop = true;
            if(currentNode.hasAdjacent()){
                currentVertex = getNextVertex(graph, currentNode, isFirstLoop);
                isFirstLoop = false;
            } else {
                currentVertex = null;
            }
        }
        return newPath;

    }

    /**
     * Gets the current Node and adds it to the path. Also adds all the
     * self-loops.
     * @param graph the graph
     * @param currentVertex the current vertex number
     * @param newPath the new path we're creating
     * @return
     */
    private Node addCurrentVertexAndSelfLoops(Graph graph, Integer currentVertex, Path newPath) {
        Integer currentVertexNum = currentVertex;
        newPath.add(currentVertexNum);
        Node currentNode = graph.getNode(currentVertexNum);
        while(currentNode.hasSelfLoops()){
            newPath.add(currentVertexNum);
            graph.removeSelfLoop(currentVertexNum);
        }
        return currentNode;
    }

    /**
     * gets the next vertex, removes the incoming and outgoing vertices from the
     * nodes, and updates the isOdd properties
     * @param graph the graph
     * @param currentNode the current node in the loop
     * @param isFirstLoop boolean used by updateFirstAndLastOddNode method
     * @return
     */
    private Integer getNextVertex(Graph graph, Node currentNode, boolean isFirstLoop) {
        Integer currentVertex;
        Integer nextVertex = currentNode.getFirstAdjacent();
        currentNode.removeFirstAdjacent();
        updateFirstAndLastOddNode(currentNode, isFirstLoop);
        graph.getNode(nextVertex).removeIncomingVertex();
        updateFirstAndLastOddNode(graph.getNode(nextVertex), isFirstLoop);
        currentVertex=nextVertex;
        return currentVertex;
    }

    /**
     * this will updated the isOdd property of the first and last nodes of a path
     * if it's semi-Eulerian and also check again if it's currently odd
     * @param currentNode
     * @param isFirstLoop
     */
    private void updateFirstAndLastOddNode(Node currentNode, boolean isFirstLoop) {
        if(isFirstLoop || !currentNode.hasAdjacent() || currentNode.isOdd()){
            currentNode.updateOdd();
        }
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
class Graph{
    private ArrayList<Node> nodes;
    Set<Integer> oddVertices;
    public Graph(){
        oddVertices = new HashSet<>();
        nodes = new ArrayList<>();
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    /**
     * get Node of index as optional
     * @param n index to get
     * @return Node or Optional.empty if it's not there
     */


    public int size(){
        return nodes.size();
    }
    public void addOddVertex(int n){
        oddVertices.add(n);
    }

    public void removeOddVertex(int n){
        oddVertices.remove(n);
    }

    public boolean isEulerian(){
        return oddVertices.size()==0;
    }

    public boolean isSemiEulerian(){
        return oddVertices.size()==2;
    }

    public Integer[] getOddVertices(){
        return oddVertices.toArray(new Integer[oddVertices.size()]);
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


    public boolean isGraphEven(int graphSize){
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
    private int incomingVertices;
    private int selfLoops = 0;
    private boolean isOdd;
    private Graph gr;
    /**
     *
     * @param vertex the number of the vertex
     * @param vertices adjacent vertices
     * @param incomingVertices incoming vertices
     */
    public Node(Integer vertex, List<Integer> vertices, List<Integer> incomingVertices, Graph gr) {
        this.vertex = vertex;
        this.adjacentVertices = vertices;
        this.incomingVertices = incomingVertices.size();
        this.gr = gr;
    }

    public boolean isOdd(){
        return isOdd;
    }

    public void setOdd(){
        isOdd = true;
    }

    public void clearOdd(){
        isOdd = false;
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

    public void removeFirstAdjacent(){
        if(!adjacentVertices.isEmpty()){
            adjacentVertices.remove(0);
        }
    }

    public void removeIncomingVertex(){
        incomingVertices--;
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
        incomingVertices++;
    }

    public void updateOdd(){
        if (checkisEven()) {
            isOdd = false;
            gr.removeOddVertex(vertex);
        } else {
            isOdd = true;
            gr.addOddVertex(vertex);
        }
    }

    /**
     *
     * @return true if in==out, false if not
     */
    private boolean checkisEven(){
         return incomingVertices==adjacentVertices.size();
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