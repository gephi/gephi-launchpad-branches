/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gephi.layout.plugin.circularlayout.nodecomparator;


import java.util.Comparator;
import java.lang.reflect.Method;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
/**
 *
 * @author Matt
 */
    public class BasicNodeComparator implements Comparator<Object> {

        private Node[] collection;
        private String methodName;
        private boolean sortAsc;
        private Graph graph;

        public static final int EQUAL = 0;
        public static final int LESS_THAN = -1;
        public static final int GREATER_THAN = 1;

        public BasicNodeComparator(Graph graph,Node[] collection, String field, boolean sortAsc) {
            super();
            this.graph = graph;
            this.collection = collection;
            this.methodName = buildMethod(field);
            this.sortAsc = sortAsc;
        }

        @Override
        public int compare(Object o1, Object o2) {
            int rv = 0;
            try {
                Method method = graph.getClass().getMethod(methodName, Node.class);
                Class<?> c = method.getReturnType();

                Object result1 = method.invoke(graph, o1);
                Object result2 = method.invoke(graph, o2);

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

        private String buildMethod(String field) {
            StringBuilder fieldName = new StringBuilder("get");
            fieldName.append(field.substring(0));
            return fieldName.toString();
        }
    }
