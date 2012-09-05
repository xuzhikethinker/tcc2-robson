package visualization;

import java.util.Iterator;

import analysis.MethodAnalysis;

import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.visualization.VisualizationViewer;

import model.Element;
import model.SysAdvice;
import model.SysMethod;
import model.SysRoot;

/**the class that constructs a call chain graph*/
public class CallChainM2G {
	
	EspecialEdgesTable<Element, Float> table = new EspecialEdgesTable<Element, Float>();
	private int deltaX = 100;
	private int deltaY = 80;

	/**constructs an AggregateLayout with the given SysMethod*/
	public AggregateLayout<Element, Float> doAggregateLayout( SysRoot root, SysMethod m) {
		MethodAnalysis.analyseMethod(m, root);
		DelegateTree<Element, Float> dt = new DelegateTree<Element, Float>();
		dt.addVertex(m);
		dt = addChildToGraph(dt,m);
		Forest<Element, Float> df = ModelToGraph.tree_to_forest(dt);
		AggregateLayout<Element, Float> al = new AggregateLayout<Element, Float>(new TreeLayout<Element, Float>(df, deltaX, deltaY));
		df=addEspecialEdges(df, table);
		return al;
	}
	
	
	/**constructs a VisualizationViewer from an AggregateLayout*/
	public VisualizationViewer<Element, Float> makeVV(AggregateLayout<Element, Float> al){
		VisualizationViewer<Element, Float> vv =new VisualizationViewer<Element, Float>(al);		
		return vv;
	}
	
	/**add the vertex that may broke a tree structure*/
	private static Forest<Element, Float> addEspecialEdges(Forest<Element, Float> df, EspecialEdgesTable<Element, Float> table) {
		Iterator<Element> from = table.getFrom().iterator();
		Iterator<Element> to = table.getTo().iterator();
		Iterator<Float> edge = table.getEdge().iterator();
		while(from.hasNext() && to.hasNext() && edge.hasNext()){
			//df.addEdge(df.getEdgeCount()+1.5f, from.next(), to.next());
			df.addEdge(edge.next()+0.5f, from.next(), to.next());
		}
		return df;
	}

	/**add the caller child to the graph*/
	public DelegateTree<Element, Float> addChildToGraph(DelegateTree<Element, Float> g, SysMethod caller){
		for(SysMethod m : caller.getCalls()){
			if(g.containsVertex(m)){
				this.table.add(g.getEdgeCount()*1.0f+1.0f+this.table.size(), caller, m);
			} else {
				//g.addChild(g.getVertexCount()*1.0f+1.5f, caller, m);
				g.addChild(g.getEdgeCount()*1.0f+1.5f+this.table.size(), caller, m);
				g = addChildToGraph(g,m);//recursive call to complete the graph and add the needed edges to the table
			}
		}
		for(SysAdvice ad : caller.getAffecters()){
			if(g.containsVertex(ad)){
				this.table.add(g.getEdgeCount()*1.0f+1.0f+this.table.size(), caller, ad);
			} else {
				//g.addChild(g.getVertexCount()*1.0f+1.5f, caller, ad);
				g.addChild(g.getEdgeCount()*1.0f+1.5f + this.table.size(), caller, ad);
				g = addChildToGraph(g,ad);//recursive call to complete the graph and add the needed edges to the table
			}
		}
		return g;
		
	}
	
	



}