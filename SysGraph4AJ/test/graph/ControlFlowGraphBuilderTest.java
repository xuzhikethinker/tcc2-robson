package graph;

import graph.util.ControlFlowGraphClassForTestUtils;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.apache.bcel.generic.Instruction;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Classe de teste do construtor de grafo de fluxo de controle {@link ControlFlowGraphBuilder}
 * 
 * @author robson
 *
 */
public class ControlFlowGraphBuilderTest {
	
	private static final ControlFlowGraphBuilder CONTROL_FLOW_GRAPH_BUILDER = new ControlFlowGraphBuilder();

	@Test
	public void ifElseTest() throws NoSuchMethodException, IOException {
		
		ControlFlowGraphBlockNode tree = CONTROL_FLOW_GRAPH_BUILDER.build(ControlFlowGraphClassForTestUtils.class, "ifElseMethod", Integer.TYPE);
		
		Assert.assertEquals(5, tree.getInstructions().size());
		
		List<ControlFlowGraphBlockNode> childrenBlocks = tree.getChildNodes();
		Assert.assertEquals(2, childrenBlocks.size());
		
		
	}
	
	@Test
	public void forTest() throws NoSuchMethodException, IOException {
		
		ControlFlowGraphBlockNode node0 = CONTROL_FLOW_GRAPH_BUILDER.build(ControlFlowGraphClassForTestUtils.class, "forMethod", String.class);
		
		List<Instruction> instructionsFromRoot = node0.getInstructions();
		Assert.assertEquals(7, instructionsFromRoot.size());
		
		List<ControlFlowGraphBlockNode> childrenBlocks = node0.getChildNodes();
		Assert.assertEquals(2, childrenBlocks.size());
		
		ControlFlowGraphBlockNode node1 = childrenBlocks.get(0);
		Assert.assertEquals(2, node1.getChildNodes().size());
		
		ControlFlowGraphBlockNode node2 = childrenBlocks.get(1);
		Assert.assertEquals(0, node2.getChildNodes().size());
		
		ControlFlowGraphBlockNode node3 = node1.getChildNodes().get(0);
		Assert.assertEquals(1, node3.getChildNodes().size());
		
		ControlFlowGraphBlockNode node4 = node1.getChildNodes().get(1);
		Assert.assertEquals(1, node4.getChildNodes().size());
	}

	@Test
	public void switchTest() {
		ControlFlowGraphBlockNode node0 = CONTROL_FLOW_GRAPH_BUILDER.build(ControlFlowGraphClassForTestUtils.class, "switchMethod");
		
		Assert.assertNotNull(node0);
		
		ControlFlowGraphBlockNode node1 = node0.getChildNodes().get(0);
		ControlFlowGraphBlockNode node2 = node0.getChildNodes().get(1);
		
		Assert.assertEquals(8, node1.getChildNodes().size());
		
		for(ControlFlowGraphBlockNode childNodeFromNode1 : node1.getChildNodes()) {
			Assert.assertEquals(0, childNodeFromNode1.getChildNodes().size());
		}
		
		Assert.assertEquals(1, node2.getChildNodes().size());		
	}

	@Test
	@Ignore
	public void aspectTest() {		
		ControlFlowGraphBlockNode node0 = CONTROL_FLOW_GRAPH_BUILDER.build(ControlFlowGraphClassForTestUtils.class, "aspectMethod");
		
		Assert.assertNotNull(node0);
		
		List<ControlFlowGraphBlockNode> childrenBlocks = node0.getChildNodes();
		Assert.assertEquals(1, node0.getAspectInstructions().size());
		Assert.assertEquals(2, childrenBlocks.size());
		
		ControlFlowGraphBlockNode node1 = childrenBlocks.get(0);
		Assert.assertEquals(0, node1.getChildNodes().size());
		
		ControlFlowGraphBlockNode node2 = childrenBlocks.get(1);
		Assert.assertEquals(1, node2.getChildNodes().size());
		Assert.assertEquals(1, node2.getAspectInstructions().size());
	}
	
	@Test
	public void tryCatchTest() {
		ControlFlowGraphBlockNode node0 = CONTROL_FLOW_GRAPH_BUILDER.build(ControlFlowGraphClassForTestUtils.class, "tryCatchMethod");
		
		Assert.assertTrue(node0.isTryStatement());
		
		List<ControlFlowGraphBlockNode> childrenBlocks = node0.getChildNodes();
		
		ControlFlowGraphBlockNode node1 = childrenBlocks.get(0);
		Assert.assertTrue(node1.isTryStatement());
		//try/finally do try de cima
		Assert.assertEquals(2, node1.getChildNodes().size());
		ControlFlowGraphBlockNode node3 = node1.getChildNodes().get(0);
		Assert.assertFalse(node3.isTryStatement());
		//escopo try apontando para um catch
		Assert.assertEquals(1, node3.getChildNodes().size());
		
		ControlFlowGraphBlockNode node2 = childrenBlocks.get(1);
		
		Assert.assertFalse(node2.isTryStatement());
	}
}
