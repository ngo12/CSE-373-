import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;

/**
 * 
 */

/**
 * @author Ryan Linden and Brandon Ngo .
 * Extra Credit Options Implemented, if any:  (mention them here.)
 * 
 * Solution to Assignment 5 in CSE 373, Autumn 2016
 * University of Washington.
 * 
 * (Based on starter code v1.3. By Steve Tanimoto.)
 *
 * Java version 8 or higher is recommended.
 *
 */

// Here is the main application class:
public class ExploredGraph {
	Set<Vertex> Ve; // collection of explored vertices
	Set<Edge> Ee;   // collection of explored edges
	Vertex startVe; // starting point, TODO do we need it?
	
	public ExploredGraph() {
		Ve = new LinkedHashSet<Vertex>();
		Ee = new LinkedHashSet<Edge>();
	}

	public void initialize(v) {
		// TODO Check for valid # of pegs?
		startVe = v;
		Ve.add(v);
	}
	
	public int nvertices() {
		return Ve.size(); // Will return the size of the Ve LinkedHashSet
	} 
	
	public int nedges() {
		return Ee.size(); // Will return the size of the Ee LinkedHashSet
	}

	public void idfs(Vertex vi, Vertex vj) {
		// use stack for open set
		Stack openVe = new Stack();
		Stack closedVe = new Stack();
		// Add Start vertex to open set
		openVe.push(vi);
		// While open set not empty:
		while !openVe.empty() {
			// pop node from open set
			currentVe = openVe.pop;
			// for each child of v:
				// check if child visited already
				// if not, add to open set
				// add v to closed
		}
	} // Implement this. (Iterative Depth-First Search)
	
	public void bfs(Vertex vi, Vertex vj) {} // Implement this. (Breadth-First Search)
	public ArrayList<Vertex> retrievePath(Vertex vi) {return null;} // Implement this.
	public ArrayList<Vertex> shortestPath(Vertex vi, Vertex vj) {return null;} // Implement this.
	public Set<Vertex> getVertices() {return Ve;} 
	public Set<Edge> getEdges() {return Ee;} 
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ExploredGraph eg = new ExploredGraph();
		// Test the vertex constructor: 
		Vertex v0 = eg.new Vertex("[[4,3,2,1],[],[]]");
		Vertex v1 = eg.new Vertex("[[],[],[4,3,2,1]]");
		System.out.println(v0);
		// Add your own tests here.
		// The autograder code will be used to test your basic functionality later.
		eg.Ve.add(v0); // Adds v0 to the Set of Vertexes in the ExploredGraph 
		eg.Ve.add(v1); // Adds 1 to the Set of Vertexes in the ExploredGraph 
		System.out.println(eg.nvertices()); // Size should be equal to 2 

	}
	
	class Vertex {
		ArrayList<Stack<Integer>> pegs; // Each vertex will hold a Towers-of-Hanoi state.
		// There will be 3 pegs in the standard version, but more if you do extra credit option A5E1.
		
		// Constructor that takes a string such as "[[4,3,2,1],[],[]]":
		public Vertex(String vString) {
			String[] parts = vString.split("\\],\\[");
			pegs = new ArrayList<Stack<Integer>>(3);
			for (int i=0; i<3;i++) {
				pegs.add(new Stack<Integer>());
				try {
					parts[i]=parts[i].replaceAll("\\[","");
					parts[i]=parts[i].replaceAll("\\]","");
					List<String> al = new ArrayList<String>(Arrays.asList(parts[i].split(",")));
					System.out.println("ArrayList al is: "+al);
					Iterator<String> it = al.iterator();
					while (it.hasNext()) {
						String item = it.next();
                        if (!item.equals("")) {
                                System.out.println("item is: "+item);
                                pegs.get(i).push(Integer.parseInt(item));
                        }
					}
				}
				catch(NumberFormatException nfe) { nfe.printStackTrace(); }
			}		
		}
		public String toString() {
			String ans = "[";
			for (int i=0; i<3; i++) {
			    ans += pegs.get(i).toString().replace(" ", "");
				if (i<2) { ans += ","; }
			}
			ans += "]";
			return ans;
		}
	}
	
	// Edges contain two vertices to show that it is the edge between them
	class Edge {
		private Vertex vi, vj;
		
		// Construct an edge using two vertices
		public Edge(Vertex vi, Vertex vj) {
			this.vi = vi;
			this.vj = vj;
		}
		
		// Return a string representation of an edge
		public String toString() {
			String ans = "Edge from ";
			ans += vi.toString();
			ans += " to ";
			ans += vj.toString();
			return ans;
		}
		
		// Return the first vertex end point
		public Vertex getEndPoint1(){
			return vi;
		}
		
		// Return the second vertex end point
		public Vertex getEndPoint2(){
			return vj;
		}
	}
	
	class Operator {
		private int i, j;

		public Operator(int i, int j) { // Constructor for operators.
			this.i = i;
			this.j = j;
		}

		// Will determine whether or not this 
		// operator is applicable to this vertex.
		// Only one disk may be moved at a time. Only a disk that is topmost in its stack of disks may be moved. 
		// It must then be moved either to a peg containing no disks or to a peg 
		// where the topmost disk is of a larger diameter than its own
		public boolean precondition(Vertex v) {
			if(v.pegs.get(i).empty()){ // Checks if peg i is empty 
				return false; 
			} else if (v.pegs.get(j).empty()){ // Checks if peg j is empty 
				return true;
			} else if (v.pegs.get(i).peek() > v.pegs.get(j).peek()){ // Checks if top disk at peg i is larger than top disk at peg j 
				return false;
			} else { // Top disk at peg is smaller than the top disk at peg j 
				return true; 
			}

		}

		public Vertex transition(Vertex v) {
			if(precondition(v)){
				Vertex newState = new Vertex(v.toString());
				newState.pegs.get(j).push(newState.pegs.get(i).pop());
				return newState;
			} else {
			return v; // Should return null or original input Vertex v since insertion into set of same vertex will not do anything?
			}
		}
		
		
		//Code to return a string good enough
		// to distinguish different operators
		public String toString() {
			return "Move a disk from peg " + i + " to peg " + j;
			
		}
	}

}
