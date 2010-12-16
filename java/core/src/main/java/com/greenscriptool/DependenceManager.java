package com.greenscriptool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A tree node based implementation of {@link IDependenceManager}.
 * 
 * The dependence relationships is built up during construction of the
 * instance. The input is defined in a {@link Properties}. 
 * 
 * @author greenlaw110@gmail.com
 * @version 1.0, 2010-10-14
 * @since 1.0
 */
public class DependenceManager implements IDependenceManager {
    
    private Map<String, Node> dependencies_ = new HashMap<String, Node>();
    
    /**
     * Create a dependency manager with a properties which
     * contains a set of dependence relationships. The format
     * of the properties shall look like:
     * 
     * a=b,c,d
     * b=x,y
     * 
     * which means item a depends on b, c and d; b depends on
     * x and y
     * 
     * @param dependencies
     */
    public DependenceManager(Properties dependencies) {
        for (String s: dependencies.stringPropertyNames()) {
            String v = dependencies.getProperty(s, "");
            List<String> l = Arrays.asList(v.replaceAll("\\s+", "").split(SEPARATOR));
            addDependency(s, l);
        }
        for (Node n: dependencies_.values()) {
            n.rectify();
        }
    }
    
    @Override
    public List<String> comprehend(Collection<String> resourceNames) {
        return comprehend(resourceNames, false);
    }

    @Override
    public List<String> comprehend(Collection<String> resourceNames, boolean withDefault) {
        if (resourceNames.size() == 0 && !withDefault) return Collections.emptyList();
        List<String> retList = new ArrayList<String>();
        Map<String, Node> nodes = new HashMap<String, Node>();
        List<String> undefs = new ArrayList<String>();
        for (String name: resourceNames) {
            if (null == name) continue;
            name = name.trim();
            if ("".equals(name)) continue;
            Node n = dependencies_.get(name);
            if (n != null) nodes.put(name, n);
            else {
                if (!undefs.contains(name)) undefs.add(name);
            }
        }

        // DEFAULT nodes go first
        SortedSet<Node> defs = new TreeSet<Node>();
        if (withDefault || nodes.containsKey(DEFAULT)) {
            Node def = dependencies_.get(DEFAULT);
            if (null != def) {
                // remove DEFAULT from nodes as it is process right now
                nodes.remove(DEFAULT);
                defs.addAll(def.allDependOns());
                for(Node n: defs) {
                    retList.add(n.name_);
                }
            }
        }
        
        SortedSet<Node> all = new TreeSet<Node>();
        for (Node n: nodes.values()) {
            all.addAll(n.allDependOns());
        }        
        all.removeAll(defs);
        for (Node n: all) {
            retList.add(n.name_);
        }
        
        retList.addAll(undefs);
        
        return retList;
    }
    
    @Override
    public List<String> comprehend(String resourceNames) {
        return comprehend(resourceNames, false);
    }

    @Override
    public List<String> comprehend(String resourceNames, boolean withDefault) {
        if (null == resourceNames) return comprehend(new ArrayList<String>(), withDefault);
        List<String> l = Arrays.asList(resourceNames.split(SEPARATOR));
        return comprehend(l, withDefault);
    }

    @Override
    public List<String> comprehend() {
        return comprehend(DEFAULT, false);
    }

    @Override
    public void addDependency(String dependent, Collection<String> dependsOn) {
        createNode_(dependent, dependsOn);
    }
    
    /**
     * Create a node denoted by <code>dependent</code>. The node might
     * not necessarily be "created" if a node corresponding to the
     * given <code>dependent</code> has been created already.
     * 
     * A list of depend on resource names can be passed to build the
     * immediate dependence relationship.
     * 
     * The node created will be stored in the dependence relationship
     * container of this {@link IDependenceManager}
     * 
     * @param dependent
     * @param dependsOn
     * @return
     */
    private Node createNode_(String dependent, Collection<String> dependsOn) {
        Node n = dependencies_.get(dependent);
        if (null == n) {
            n = new Node(dependent);
            dependencies_.put(dependent, n);
        }
        
        List<String> e = Collections.emptyList();
        for (String s: dependsOn) {
            Node n0 = createNode_(s, e);
            n.addDependOn(n0);
        }
        
        return n;
    }
    
    static class NodeComparator implements Comparator<Node> {

    	private final List<String> nameSequence;

		public NodeComparator(List<String> nameSequence) {
			this.nameSequence = nameSequence;
			// Sort for to be prepared by binary search
			Collections.sort(nameSequence);
		}
    	
		@Override
		public int compare(Node o1, Node o2) {
			if (o1 == null && o2 == null) {
				return 0;
			}
			
			if (o1 != null && o2 == null) {
				return -1;
			}
			
			if (o1 == null && o2 != null) {
				return 1;
			}
			
			int result = o2.weight_ - o1.weight_;
			if (result != 0) {
				return result;
			}
			
			int seq1 = Collections.binarySearch(nameSequence, o1.name_);
			int seq2 = Collections.binarySearch(nameSequence, o2.name_);
			
			if(seq1 >= 0 && seq2 >= 0) {
				// Both are found in name sequen, compare the sequence
				return seq1  - seq2;
			}
			
			return o2.name_.compareTo(o1.name_);
		}
    }

    /**
     * Node class abstract a dependent resource and the dependent relationship between
     * the resource and all it's depend on resources 
     * 
     * @author greenlaw110@gmail.com
     */
    private static class Node implements Comparable<Node> {
        /**
         * name of the node
         */
        private final String name_;
        /**
         * a map contains all immediate depend on resource
         * of the resource denoted by this node
         * 
         * key - resource name
         * val - resource presented by an <code>Node</code> 
         */
        private final Map<String, Node> dependOns_;
        /**
         * Weight is used to help sort nodes
         * 
         * The weight of the node shall always be smaller than the 
         * weight of any one of it's depend on nodes  
         */
        private int weight_ = 1;
        
        /**
         * the smallest gap between weight of nodes
         */
        private static final int STEP_ = 100;
        
        /**
         * keep track whether the dependencies of this node has 
         * been updated and needs rectify
         */
        private boolean dirty_ = true;

        /**
         * Construct a <code>Node</code> instance
         * @param name
         */
        private Node(String name) {
            name_ = name;
            dependOns_ = new HashMap<String, Node>();
        }

        @Override
        public boolean equals(Object that) {
            if (that == null)
                return false;
            if (that == this)
                return true;
            if (!(that instanceof Node))
                return false;
            return name_.equals(((Node) that).name_);
        }

        @Override
        public int hashCode() {
            return name_.hashCode();
        }

        @Override
        public String toString() {
            return name_;
        }

        @Override
        public int compareTo(Node o) {
            if (null == o) return -1;
            if (equals(o)) return 0;
            if (this.weight_ == o.weight_)
                return o.name_.compareTo(this.name_);
            else
                return (o.weight_ - this.weight_);
        }

        /**
         * Add a dependOn node
         * @param dependOn
         */
        void addDependOn(Node dependOn) {
            // check for circular reference
            if (dependOn.dependOn_(this)) throw new CircularDependenceException(name_, dependOn.name_);
            dependOns_.put(dependOn.name_, dependOn);
            dirty_ = true;
        }
        
        /**
         * Return all depend on nodes of this node, including indirectly depend on 
         * nodes, i.e. the nodes depended on by the depend on node(s) of this node
         * 
         * the return set also include this node itself as this node depend on it self.
         * 
         * @return
         */
        Set<Node> allDependOns() {
            Set<Node> all = new HashSet<Node>();
            if (dirty_) {
                if (dependOns_.size() != 0) {
                    for (Node n0: dependOns_.values()) {
                        all.addAll(n0.allDependOns());
                        all.add(n0);
                    }                
                }
            } else {
                all.addAll(dependOns_.values());
            }
            all.add(this);
            return all;
        }
        
        /**
         * Flatten the dependence relationship and then
         * recalculate weight of depend on nodes
         */
        void rectify() {
            flatten_();
            updateDependOnWeights_();
            dirty_ = false;
        }

        /**
         * Turn indirect dependencies into direct dependencies
         */
        private void flatten_() {
            for (Node n0: dependOns_.values()) {
                n0.flatten_();
            }
            for (Node n0: new HashSet<Node>(dependOns_.values())) {
                dependOns_.putAll(n0.dependOns_);
            }
        }

        /**
         * update weights of depend on nodes based on the
         * weight of this node
         */
        void updateDependOnWeights_() {
            incWeightOn_(null);
        }

        private void incWeightOn_(Node node) {
            if (null != node)
                this.weight_ += node.weight_ + STEP_;
            for (Node dependOn : dependOns_.values()) {
                dependOn.incWeightOn_(this);
            }
        }
        
        /**
         * Test whether a given node is depend on node of this node
         * 
         * @param node
         * @return
         */
        private boolean dependOn_(Node node) {
            if (dependOns_.containsKey(node.name_)) return true;
            if (dirty_) {
                for (Node n0: dependOns_.values()) {
                    if (n0.dependOn_(node)) return true;
                }                
            } 
            return false;
        }

    }
}
