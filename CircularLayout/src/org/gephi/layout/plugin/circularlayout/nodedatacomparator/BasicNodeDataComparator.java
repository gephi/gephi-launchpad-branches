/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gephi.layout.plugin.circularlayout.nodedatacomparator;


import java.util.Comparator;
import java.lang.reflect.Method;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeData;
/**
 *
 * @author Matt
 */
    public class BasicNodeDataComparator implements Comparator<Object> {

        private Node[] collection;
        private String attribute;
        private boolean sortAsc;

        public static final int EQUAL = 0;
        public static final int LESS_THAN = -1;
        public static final int GREATER_THAN = 1;

        public BasicNodeDataComparator(Node[] collection, String attribute, boolean sortAsc) {
            super();
            this.collection = collection;
            this.attribute = attribute;
            this.sortAsc = sortAsc;
        }

    @Override
        public int compare(Object o1, Object o2) {
            int rv = 0;
            try {
                Method method = o1.getClass().getMethod("getNodeData");
 
                NodeData NodeData1 = (NodeData) method.invoke(o1);
                NodeData NodeData2 = (NodeData) method.invoke(o2);
                Object result1 = NodeData1.getId();
                Object result2 = NodeData2.getId();
                Class<?> c = result1.getClass();
                if (!"NodeID".equals(attribute)) {
                    result1 = NodeData1.getAttributes().getValue(attribute);
                    result2 = NodeData2.getAttributes().getValue(attribute);
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
                    throw new RuntimeException("DynamicComparator does not currently support ''" + c.getName() + "''!");
                }
            } catch (Exception nsme) {
                System.out.println("Error " + nsme);
            }
            return rv * getSortOrder();
        }

        private int getSortOrder() {
            return sortAsc ? 1 : -1;
        }
        
    }
