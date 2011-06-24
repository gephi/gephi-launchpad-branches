package org.gephi.layout.plugin.circularlayout.nodecomparator;


import java.lang.reflect.Field;
import java.util.Comparator;
import java.lang.reflect.Method;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeData;
/**
 *
 * @author Matt
 */
public class NodeComparator implements Comparator<Object> {

    private Node[] collection;
    private String methodName;
    private String attribute;    
    private boolean sortAsc;
    private Graph graph;
    private CompareType enumcompare;

    public static final int EQUAL = 0;
    public static final int LESS_THAN = -1;
    public static final int GREATER_THAN = 1;

    public static enum CompareType {
        NODEID,
        METHOD,
        ATTRIBUTE,
        LAYOUTDATA
    }
    
    public NodeComparator(Graph graph, Node[] collection, CompareType enumcompare, String field, boolean sortAsc) {
            super();
            this.graph = graph;
            this.collection = collection;
            this.enumcompare = enumcompare;
            switch (enumcompare) {
                case NODEID:
                    break;
                case LAYOUTDATA:
                case ATTRIBUTE:
                    this.attribute = field;            
                    break;
                case METHOD:
                    this.methodName = buildMethod(field);
                    break;
            }
            this.sortAsc = sortAsc;
    }    
    
    @Override
    @SuppressWarnings("CallToThreadDumpStack")
    public int compare(Object o1, Object o2) {
            int rv = 0;
            Method method;
            NodeData NodeData1 = null;
            NodeData NodeData2 = null;
            Object result1 = null;
            Object result2 = null;
            Class<?> c = null;
            try {
                switch (this.enumcompare) {
			case NODEID:
                            method = o1.getClass().getMethod("getNodeData");                            
                            NodeData1 = (NodeData) method.invoke(o1);
                            NodeData2 = (NodeData) method.invoke(o2);
                            result1 = NodeData1.getId();
                            result2 = NodeData2.getId();
                            break;
                            
			case ATTRIBUTE:
                            method = o1.getClass().getMethod("getNodeData");                            
                            NodeData1 = (NodeData) method.invoke(o1);
                            NodeData2 = (NodeData) method.invoke(o2);
                            result1 = NodeData1.getAttributes().getValue(this.attribute);
                            result2 = NodeData2.getAttributes().getValue(this.attribute);
                            break;
                            
			case LAYOUTDATA:
                            method = o1.getClass().getMethod("getNodeData");                            
                            NodeData1 = (NodeData) method.invoke(o1);
                            Object NodeLayoutData1 = NodeData1.getLayoutData();
                            NodeData2 = (NodeData) method.invoke(o2);
                            Object NodeLayoutData2 = NodeData2.getLayoutData();
                            Field field1 = NodeLayoutData1.getClass().getField(this.attribute);
                            result1 = field1.get(NodeLayoutData1);
                            Field field2 = NodeLayoutData2.getClass().getField(this.attribute);
                            result2 = field2.get(NodeLayoutData2);
                            break;
                            
			case METHOD: 
                            method = graph.getClass().getMethod(this.methodName, Node.class);
                            c = method.getReturnType();
                            result1 = method.invoke(graph, o1);
                            result2 = method.invoke(graph, o2);
                            break;
                }
                
                
                if (result1 == null && result2 == null) return 0; 
                if (result1 != null && result2 == null) return -1; 
                if (result1 == null && result2 != null) return 1; 
                if (this.enumcompare != CompareType.METHOD) {
                    c = result1.getClass();
                }
                if (c.isAssignableFrom(Class.forName("java.util.Comparator"))) {
                    java.util.Comparator c1 = (java.util.Comparator) result1;
                    java.util.Comparator c2 = (java.util.Comparator) result2;
                    rv = c1.compare(c1, c2);
                } else if (Class.forName("java.lang.Comparable").isAssignableFrom(c)) {
                    java.lang.Comparable c1 = (java.lang.Comparable) result1;
                    java.lang.Comparable c2 = (java.lang.Comparable) result2;
                    rv = c1.compareTo(c2);
                } else if (c.isPrimitive()) {
                    long f1 = ((Number) result1).longValue();
                    long f2 = ((Number) result2).longValue();
                    if (f1 == f2) {
                        rv = EQUAL;
                    } else if (f1 < f2) {
                        rv = LESS_THAN;
                    } else if (f1 > f2) {
                        rv = GREATER_THAN;
                    }
                } else {
                    throw new RuntimeException("NodeComparator does not currently support ''" + c.getName() + "''!");
                }
            } catch (Exception nsme) {
                System.out.println("Error " + nsme.toString());
            }
            return rv * getSortOrder();
        }

       
    private int getSortOrder() {
        return sortAsc ? 1 : -1;
    }

    private String buildMethod(String field) {
        StringBuilder fieldName = new StringBuilder("get");
        fieldName.append(field.substring(0));
        return fieldName.toString();
    }    
}
