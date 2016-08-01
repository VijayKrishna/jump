package org.spideruci.analysis.statik.controlflow;

import static org.junit.Assert.*;

import java.util.function.BiConsumer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DomTreeTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();
  
  @Test
  public void shouldThrowExceptionWithNullFlowGraph() {
    // given
    Graph<Integer> flowGraph = null;
    
    // then
    thrown.expect(IllegalArgumentException.class);
    
    //when
    Algorithms.getDominatorTree(flowGraph);
  }
  
  @Test
  public void shouldReturnThrowupWhenFlowGraphIsEmpty() {
    // given
    Graph<Integer> flowGraph = Graph.createEmptyGraph();
    
    // then
    thrown.expect(IllegalArgumentException.class);
    
    //when
    Algorithms.getDominatorTree(flowGraph);
  }
  
  @Test
  public void shouldReturnthrowUpWithNullStartNode() {
    // given
    Graph<Integer> flowGraph = Graph.create();
    
    // then
    thrown.expect(IllegalArgumentException.class);
    
    //when
    Algorithms.getDominatorTree(flowGraph);
  }
  
  @Test
  public void endShouldPointToStartInGraphWithNoOtherNodes() {
    // given
    Graph<Integer> flowGraph = Graph.create();
    flowGraph.startNode().pointsTo(flowGraph.endNode());
    
    System.out.println(flowGraph);
    
    //when
    Graph<Integer> domTree = Algorithms.getDominatorTree(flowGraph);
    
    System.out.println(domTree);
    
    //then
    assertTrue(domTree.node(Graph.START).pointsTo().contains(domTree.node(Graph.END)));
  }
  
  @Test
  public void testDomTreeForSimpleIfStructure() {
    // given
    Graph<Integer> flowGraph = Graph.create();
    flowGraph.startNode().pointsTo(flowGraph.endNode());
    
    Node<Integer> branch = Node.create("branch", flowGraph);
    Node<Integer> then = Node.create("then", flowGraph);
    Node<Integer> join = Node.create("join", flowGraph);
    Node<Integer> next = Node.create("next", flowGraph);
    flowGraph.nowHas(branch.and(then).and(join).and(next));
    
    flowGraph.startNode().pointsTo(branch);
    branch.pointsTo(then.and(join));
    then.pointsTo(join);
    join.pointsTo(next);
    next.pointsTo(flowGraph.endNode());
    
    System.out.println(flowGraph);
    
    //when
    Graph<Integer> domTree = Algorithms.getDominatorTree(flowGraph);
    
    System.out.println(domTree);
    
    //then
    assert1pointsTo2(Graph.START, Graph.END, domTree);
    assert1pointsTo2(Graph.START, "branch", domTree);
    assert1pointsTo2("branch", "then", domTree);
    assert1pointsTo2("branch", "join", domTree);
    assert1pointsTo2("join", "next", domTree);
    assert1doesNotPointTo2("next", Graph.END, domTree);
    assert1doesNotPointTo2(Graph.START, "join", domTree);
    assert1doesNotPointTo2(Graph.START, "next", domTree);
  }
  
  @Test
  public void testDomTreeForSimpleIfElseStructure() {
    // given
    Graph<Integer> flowGraph = Graph.create();
    flowGraph.startNode().pointsTo(flowGraph.endNode());
    
    Node<Integer> branch = Node.create("branch", flowGraph);
    Node<Integer> then = Node.create("then", flowGraph);
    Node<Integer> elze = Node.create("else", flowGraph);
    Node<Integer> join = Node.create("join", flowGraph);
    Node<Integer> next = Node.create("next", flowGraph);
    flowGraph.nowHas(branch.and(then).and(elze).and(join).and(next));
    
    flowGraph.startNode().pointsTo(branch);
    branch.pointsTo(then.and(elze));
    then.pointsTo(join);
    elze.pointsTo(join);
    join.pointsTo(next);
    next.pointsTo(flowGraph.endNode());
    
    System.out.println(flowGraph);
    
    //when
    Graph<Integer> domTree = Algorithms.getDominatorTree(flowGraph);
    
    System.out.println(domTree);
    
    //then
    assert1pointsTo2(Graph.START, Graph.END, domTree);
    assert1pointsTo2(Graph.START, "branch", domTree);
    assert1pointsTo2("branch", "then", domTree);
    assert1pointsTo2("branch", "else", domTree);
    assert1pointsTo2("branch", "join", domTree);
    assert1pointsTo2("join", "next", domTree);
    assert1doesNotPointTo2("next", Graph.END, domTree);
    assert1doesNotPointTo2(Graph.START, "join", domTree);
    assert1doesNotPointTo2(Graph.START, "next", domTree);
  }
  
  @Test
  public void testForSimpleLoopStructure() {
    // given
    Graph<Integer> flowGraph = Graph.create();
    flowGraph.startNode().pointsTo(flowGraph.endNode());
    
    Node<Integer> head = Node.create("head", flowGraph);
    Node<Integer> body = Node.create("body", flowGraph);
    Node<Integer> tail = Node.create("tail", flowGraph);
    flowGraph.nowHas(head.and(body).and(tail));
    
    flowGraph.startNode().pointsTo(head);
    head.pointsTo(body.and(tail));
    body.pointsTo(head);
    tail.pointsTo(flowGraph.endNode());
    
    System.out.println(flowGraph);
    
    //when
    Graph<Integer> domTree = Algorithms.getDominatorTree(flowGraph);
    
    System.out.println(domTree);
    
    //then
    assert1pointsTo2(Graph.START, Graph.END, domTree);
    assert1pointsTo2(Graph.START, "head", domTree);
    assert1pointsTo2("head", "body", domTree);
    assert1pointsTo2("head", "tail", domTree);
    assert1doesNotPointTo2("head", Graph.END, domTree);
    assert1doesNotPointTo2(Graph.START, "tail", domTree);
  }
  
  /**
   * <a href="http://pages.cs.wisc.edu/~fischer/cs701.f08/lectures/Lecture19.4up.pdf">http://pages.cs.wisc.edu/~fischer/cs701.f08/lectures/Lecture19.4up.pdf
   */
  @Test
  public void testDomTreeFischerLectureExample() {
 // given
    Graph<Integer> flowGraph = Graph.create("fischer");
    
    Node<Integer> a = Node.create("a", flowGraph);
    Node<Integer> b = Node.create("b", flowGraph);
    Node<Integer> c = Node.create("c", flowGraph);
    Node<Integer> d = Node.create("d", flowGraph);
    Node<Integer> e = Node.create("e", flowGraph);
    Node<Integer> f = Node.create("f", flowGraph);
    flowGraph.nowHas(a.and(b).and(c).and(d).and(e).and(f));
    
    flowGraph.startNode().pointsTo(a);
    a.pointsTo(b.and(c));
    b.pointsTo(d);
    c.pointsTo(d);
    d.pointsTo(e);
    e.pointsTo(f);
    f.pointsTo(flowGraph.endNode().and(e));
    
    System.out.println(flowGraph);
    
    //when
    Graph<Integer> domTree = Algorithms.getDominatorTree(flowGraph);
    
    //then
    System.out.println(domTree);
    
    assert1pointsTo2(Graph.START, "a", domTree);
    assert1pointsTo2("a", "b", domTree);
    assert1pointsTo2("a", "c", domTree);
    assert1pointsTo2("a", "d", domTree);
    assert1pointsTo2("d", "e", domTree);
    assert1pointsTo2("e", "f", domTree);
    assert1pointsTo2("f", Graph.END, domTree);
  }
  
  @Test
  public void testPostDomTreeFischerLectureExample() {
    // given
    Graph<Integer> flowGraph = Graph.create("fischer");

    Node<Integer> a = Node.create("a", flowGraph);
    Node<Integer> b = Node.create("b", flowGraph);
    Node<Integer> c = Node.create("c", flowGraph);
    Node<Integer> d = Node.create("d", flowGraph);
    Node<Integer> e = Node.create("e", flowGraph);
    Node<Integer> f = Node.create("f", flowGraph);
    flowGraph.nowHas(a.and(b).and(c).and(d).and(e).and(f));

    flowGraph.startNode().pointsTo(a);
    a.pointsTo(b.and(c));
    b.pointsTo(d);
    c.pointsTo(d);
    d.pointsTo(e);
    e.pointsTo(f);
    f.pointsTo(flowGraph.endNode().and(e));

    Graph<Integer> revFlowGraph = flowGraph.reverseEdges();
    
    System.out.println(revFlowGraph);

    //when
    Graph<Integer> domTree = Algorithms.getDominatorTree(revFlowGraph);

    //then
    System.out.println(domTree);

    assert1pointsTo2("a", Graph.START, domTree);
    assert1pointsTo2("d", "a", domTree);
    assert1pointsTo2("d", "b", domTree);
    assert1pointsTo2("d", "c", domTree);
    assert1pointsTo2("e", "d", domTree);
    assert1pointsTo2("f", "e", domTree);
    assert1pointsTo2(Graph.END, "f", domTree);
  }
  
  private <T> void assert1pointsTo2(String one, String two, Graph<T> domTree) {
    assertTrue("Expceted: " + one +  "--->" + two, 
        domTree.node(one).pointsTo().contains(domTree.node(two)));
  };
  
  private <T> void assert1doesNotPointTo2(String one, String two, Graph<T> domTree) {
    assertFalse("Expceted: " + one +  "-/->" + two,
        domTree.node(one).pointsTo().contains(domTree.node(two)));
  };

}
