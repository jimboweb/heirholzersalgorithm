package com.jimboweb.heirholzersalgorithm;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;

import static org.junit.Assert.*;

public class HeirholzersAlgorithmTest {
    Random rnd = new Random();

    @org.junit.Test
    public void hierholzersAlgorithm() {
        int graphSize = rnd.nextInt(10);
        InputGraph g = makeBalancedInputGraph(graphSize);
    }

    @org.junit.Test
    public void findPath() {
    }

    @org.junit.Test
    public void findFirstVertex() {
    }

    @org.junit.Test
    public void makeNewPath() {
    }

    private String createInput(InputGraph g){
        // TODO: 2/4/18 turn graph into input
        return "";
    }

    class TestInput implements Inputter{

        @Override
        public ArrayList<ArrayList<Integer>> getInput() {
            // TODO: 2/4/18 generate input
            return null;
        }
    }

    class TestOutput implements Outputter {

        private String outputText;

        @Override
        public void output(String output) {
            outputText=output;
        }

        public String getOutputText() {
            return outputText;
        }
    }



//    private String getFailOutput(Cycle c, InputGraph g, EulerianCycle instance){
//        String s = "Input graph was " + g.getInputAsString();
//        s+="\n";
//        s+=c.toString() + "\n";
//        //s+="buildCycleOps: " + instance.buildCycleOps;
//        return s;
//    }

    class InputNode{
        private ArrayList<Integer> edgesOut;
        private ArrayList<Integer> edgesIn;
        private final int index;
        //positive balance = more out than in
        //neg. balance = more in than out
        //0 balance = even
        private int balance;

        public InputNode(int index) {
            this.edgesOut = new ArrayList<>();
            this.edgesIn = new ArrayList<>();
            this.index = index;
            this.balance = 0;
        }

        public void addEdgeOut(int e){
            edgesOut.add(e);
            balance--;
        }
        public void addEdgeIn(int e){
            edgesIn.add(e);
            balance++;
        }

        public int getBalance() {
            return balance;
        }

        public boolean balanceIsPositive(){
            return balance>0;
        }

        public boolean balanceIsNegative(){
            return balance<0;
        }
        public boolean isBalanced(){
            return balance==0;
        }

        public int getIndex() {
            return index;
        }
    }

    class InputEdge{
        private final int index;
        private final int fromNode;
        private final int toNode;

        public InputEdge(int index, int fromNode, int toNode) {
            this.index = index;
            this.fromNode = fromNode;
            this.toNode = toNode;
        }

        public int getIndex() {
            return index;
        }

        public int getFromNode() {
            return fromNode;
        }

        public int getToNode() {
            return toNode;
        }

        @Override
        public String toString() {
            int f = fromNode+1;
            int t = toNode +1;
            return f + " " + t;
        }
    }

    class InputGraph {
        Random rnd = new Random();
        private final ArrayList<InputNode> nodes;
        ArrayList<InputEdge> edges;
        BitSet balancedNodes;
        boolean graphIsBalanced;
        public InputGraph(int size) {
            ArrayList<InputNode> nodes = new ArrayList<>();
            for(int i=0;i<size;i++){
                nodes.add(new InputNode(i));
            }
            this.nodes=nodes;
            this.edges = new ArrayList<>();
            balancedNodes = new BitSet(nodes.size());
        }

        public ArrayList<InputNode> getNodes() {
            return nodes;
        }

        /**
         * <ol>
         *     <li>adds an edge from node to node</li>
         *     <li>adds edge to edgeOut of from and edgeIn of to</li>
         *     <li>sets the balance of node which sets graphIsBalanced</li>
         * </ol>
         *
         * @param from from node
         * @param to to node
         */
        public void addEdge(int from, int to){
            if(!(nodes.size()>=(from))){
                throw new IllegalArgumentException("node " + from + " out of bounds");
            }
            if(!(nodes.size()>=to)){
                throw new IllegalArgumentException("node " + to + "out of bounds");
            }
            int edgeInd = edges.size();
            edges.add(new InputEdge(edgeInd,from,to));
            InputNode fromNode = nodes.get(from);
            InputNode toNode = nodes.get(to);
            fromNode.addEdgeOut(edgeInd);
            setOrClearBalancedNode(fromNode);
            toNode.addEdgeIn(edgeInd);
            setOrClearBalancedNode(toNode);
        }

        /**
         * checks if node is balanced and sets or clears the boolean in balancedNodes
         * @param n node to check
         */
        private void setOrClearBalancedNode(InputNode n){
            int ind = n.index;
            if(n.isBalanced()){
                setBalancedNode(ind);
            } else {
                clearBalancedNode(ind);
            }
        }

        /**
         * sets node in balancedNodes and sets graphIsBalanced
         * @param ind index of node
         */
        private void setBalancedNode(int ind){
            balancedNodes.set(ind);
            setGraphBalance();
        }

        /**
         * clears node in balancedNodes and sets graphIsBalanced
         * @param ind index of node
         */
        private void clearBalancedNode(int ind){
            balancedNodes.clear(ind);
            setGraphBalance();
        }

        /**
         * sets the balance of the graph if it's balanced
         */
        private void setGraphBalance(){
            graphIsBalanced = balancedNodes.cardinality()==nodes.size();
        }

        public InputNode getNode(int ind){
            return nodes.get(ind);
        }
        public InputEdge getEdge(int ind){
            return edges.get(ind);
        }
        public String getInputAsString(){
            String rtrn = nodes.size() + " " + edges.size()+"\n";
            for(InputEdge e:edges){
                rtrn+=e.toString() + "\n";
            }
            return rtrn;
        }
        public ArrayList<int[]> getInputAsArray(){
            ArrayList<int[]> input = new ArrayList<>();
            int[] firstLine = {nodes.size(),edges.size()};
            input.add(firstLine);
            for(InputEdge e:edges){
                int[] nextLine = {e.fromNode+1,e.toNode+1};
                input.add(nextLine);
            }
            return input;
        }

        /**
         * return a random node with optional sign; 0 = sign doesn't matter
         * @param sign 0 for any node, positive for positive node, negative for negative node
         * @return any node if sign 0, otherwise node of balanced sign
         */
        public InputNode getRandomNode(int sign){
            if(sign==0){
                int nodeNum = rnd.nextInt(nodes.size());
                InputNode n = nodes.get(nodeNum);
                return n;
            }
            boolean positive = sign>0;
            BitSet nodeWasTried = new BitSet(nodes.size());
            // TODO: 1/14/18 failing to get positive or negative node and returning new node with index -1
            while(nodeWasTried.cardinality()<nodes.size()){
                int rtrnInd = rnd.nextInt(nodes.size());
                if(nodeWasTried.get(rtrnInd)){
                    continue;
                }
                InputNode possibleRtrn = nodes.get(rtrnInd);
                if(positive) {
                    if (possibleRtrn.balanceIsPositive()) {
                        return possibleRtrn;
                    }
                } else {
                    if (possibleRtrn.balanceIsNegative()){
                        return possibleRtrn;
                    }
                }
                nodeWasTried.set(rtrnInd);
            }
            return new InputNode(-1);
        }

        public boolean graphIsBalanced() {
            return graphIsBalanced;
        }
    }

    /**
     * @return a balanced input graph
     */
    private InputGraph makeBalancedInputGraph(int graphSize){
        Random rnd = new Random();
        int n = rnd.nextInt(graphSize) + 2;
        boolean balanced = rnd.nextInt(10)<1;
        int m = 0;
        InputGraph gr = new InputGraph(n);
        for(InputNode fromNode:gr.getNodes()){
            int fromNodeInd = fromNode.getIndex();
            InputNode toNode;
            do{
                toNode = gr.getRandomNode(0);
            } while(toNode.index==fromNode.index);
            int toNodeInd = toNode.getIndex();
            gr.addEdge(fromNodeInd,toNodeInd);
        }

        while(!gr.graphIsBalanced()){
            InputNode nodeToBalance;
            do{
                nodeToBalance = gr.getRandomNode(0);
            } while(nodeToBalance.isBalanced());
            InputNode otherNode;
            if(nodeToBalance.balanceIsPositive()){
                otherNode = gr.getRandomNode(-1);
                gr.addEdge(nodeToBalance.getIndex(),otherNode.getIndex());
            } else {
                otherNode = gr.getRandomNode(1);
                gr.addEdge(otherNode.getIndex(),nodeToBalance.getIndex());
            }
        }
        boolean areNodesPointingToSelf = rnd.nextBoolean();
        int nodesPointingToSelf = areNodesPointingToSelf? rnd.nextInt(n):0;
        for(int i=0;i<nodesPointingToSelf;i++){
            int nodePointingToSelf = rnd.nextInt(n);
            gr.addEdge(nodePointingToSelf,nodePointingToSelf);
        }
        return gr;
    }

    // TODO: 2/4/18 modify this to work with output string
    /**
     * <o>Checks to make sure that the cycle meets following criteria:</p>
     * <ol>
     *     <li>every edge connects to the next edge</li>
     *     <li>every edge is used</li>
     *     <li>no edge used more than once</li>
     *     <li>last edge connects to first edge</li>
     * </ol>
     * @param g the original input graph
     * @return true if cycle is Eulerian in InputGraph g
     */
    private boolean testEulerianCycle(String output, InputGraph g){
        String[] nodeListAsString = output.split(" ");
        ArrayList<Integer> nodeList = new ArrayList<>();
        for (String s:nodeListAsString){
            try{
                nodeList.add(Integer.parseInt(s));
            } catch (NumberFormatException ex) {
                System.out.println(ex);
            }
        }
        boolean[] edgeIsUsed = new boolean[g.edges.size()];

        //TODO: make sure each node's out goes to next node's to
        Integer resultFrom = nodeList.get(0);
        Integer resultTo;
        for(int i=1;i<nodeList.size();i++){
            resultFrom = nodeList.get(i);

            resultTo = nodeList.get(i%nodeList.size());
        }
        //TODO: make sure that the cycle has all the edges

        //TODO: make sure all edges were used

        return true;
    }

    private static int triangular(int n){
        int tri = 0;
        for(int i=1; i<n; i++){
            tri = tri + i;
        }
        return tri;
    }

}