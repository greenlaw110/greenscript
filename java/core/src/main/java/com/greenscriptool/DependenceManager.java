package com.greenscriptool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A tree node based implementation of {@link IDependenceManager}.
 * 
 * The dependence relationships is built up during construction of the instance.
 * The input is defined in a {@link Properties}.
 * 
 * @author greenlaw110@gmail.com
 * @version 1.0.2, 2010-07-08, bug fix: refresh play plugin cause Node weight
 *          overflow;
 * @version 1.0.1, 2010-01-21, add debugString()
 * @version 1.0, 2010-10-14
 * @since 1.0
 */
public class DependenceManager implements IDependenceManager {

    private Map<String, Node> dependencies_;// = new HashMap();

    public String debugString() {
        StringBuilder sb = new StringBuilder();
        sb.append(
                "===============================================================")
                .append("\n DependencyManager debug information ");
        for (Node n : dependencies_.values()) {
            sb.append(String.format("\n\n node info: %1$s\n", n.name_)).append(
                    n.debugString());
        }

        return sb.toString();
    }

    // public DependenceManager(){}

    /**
     * <p>
     * Create a dependency manager with a properties which contains a set of
     * dependence relationships. The format of the properties shall look like:
     * </p>
     * 
     * <code>
     * a=b,c,d
     * b=x,y
     * </code>
     * 
     * <p>
     * which means item a depends on b, c and d; b depends on x and y
     * </p>
     * 
     * <p>
     * New in 1.2d:
     * </p>
     * 
     * <code>
     * a<b<c
     * x>y>z
     * ab=xy<z,o
     * </code>
     * 
     * <p>
     * which means item a depends on b and in turn depends on c; z depends on y
     * which in turn depends on y;ab depends on both xy, z and o, while xy
     * depends on z
     * </p>
     * 
     * @param dependencies
     */
    public DependenceManager(Properties dependencies) {
        dependencies_ = new HashMap<String, Node>();
        for (String s : dependencies.stringPropertyNames()) {
            String v = dependencies.getProperty(s, "");
            if (null != v && !v.trim().equals("")) {
                processInlineDependency(v);
                List<String> l = Arrays.asList(v.replaceAll("\\s+", "").split(
                        SEPARATOR));
                createNode_(s, l);
            } else {
                processInlineDependency(s);
            }
        }
        for (Node n : dependencies_.values()) {
            n.rectify();
        }
    }

    public List<String> comprehend(Collection<String> resourceNames) {
        return comprehend(resourceNames, false);
    }

    public List<String> comprehend(Collection<String> resourceNames,
            boolean withDefault) {
        if (resourceNames.isEmpty() && !withDefault)
            return Collections.emptyList();
        List<String> retList = new ArrayList<String>();
        Map<String, Node> nodes = new HashMap<String, Node>();
        List<String> undefs = new ArrayList<String>();
        for (String name : resourceNames) {
            if (null == name)
                continue;
            name = name.trim();
            if ("".equals(name))
                continue;
            Node n = dependencies_.get(name);
            if (n != null)
                nodes.put(name, n);
            else {
                if (!undefs.contains(name))
                    undefs.add(name);
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
                for (Node n : defs) {
                    retList.add(n.name_);
                }
            }
        }

        SortedSet<Node> all = new TreeSet<Node>();
        for (Node n : nodes.values()) {
            all.addAll(n.allDependOns());
        }
        all.removeAll(defs);
        for (Node n : all) {
            retList.add(n.name_);
        }

        retList.addAll(undefs);

        return retList;
    }

    public List<String> comprehend(String resourceNames) {
        return comprehend(resourceNames, false);
    }

    public List<String> comprehend(String resourceNames, boolean withDefault) {
        if (null == resourceNames)
            return comprehend(new ArrayList<String>(), withDefault);
        processInlineDependency(resourceNames);
        List<String> l = Arrays.asList(resourceNames.split(SEPARATOR));
        return comprehend(l, withDefault);
    }

    public List<String> comprehend() {
        return comprehend(DEFAULT, false);
    }

    public final void addDependency(String dependent,
            Collection<String> dependsOn) {
        createNode_(dependent, dependsOn);
        for (Node n : dependencies_.values()) {
            n.rectify();
        }
    }

    private Set<String> inlineDepDeclarations = new HashSet<String>();
    public void processInlineDependency(String dependency) {
        if (inlineDepDeclarations.contains(dependency)) return; // already processed
        inlineDepDeclarations.add(dependency);
        dependency = " " + dependency; // in order to match the regexp
        final Pattern p = Pattern
                .compile("(?=[\\s,;]+|(?<![\\w\\/\\-\\.:])([\\w\\/\\-\\.:]+\\s*[<>]\\s*[\\w\\/\\-\\.:]+))");
        Matcher m = p.matcher(dependency);
        boolean found = false;
        while (m.find()) {
            String g = m.group(1);
            if (null == g)
                continue;
            found = true;
            String[] relation = g.split("[<>]");
            String a = relation[0].trim();
            String b = relation[1].trim();
            if (g.indexOf('<') > -1) {
                createNode_(a, Arrays.asList(new String[] { b }));
            } else {
                createNode_(b, Arrays.asList(new String[] { a }));
            }
        }
        if (found) {
            for (Node n : dependencies_.values()) {
                n.rectify();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String s = "http://ahost.com/something.js > http://zbc-1.com.au/some/path/to/x19-v1.0.js < y < z < a > b > c > d";
        String regex = "(?=[\\s,;]+|(?<![\\w\\/\\-\\.:])([\\w\\/\\-\\.:]+\\s*[<>]\\s*[\\w\\/\\-\\.:]+))";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(s);
        while (m.find()) {
            String d = m.group(1);
            if (d != null) {
                System.out.println(d);
            }
        }
    }

    /**
     * Create a node denoted by <code>dependent</code>. The node might not
     * necessarily be "created" if a node corresponding to the given
     * <code>dependent</code> has been created already.
     * 
     * A list of depend on resource names can be passed to build the immediate
     * dependence relationship.
     * 
     * The node created will be stored in the dependence relationship container
     * of this {@link IDependenceManager}
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
        for (String s : dependsOn) {
            Node n0 = createNode_(s, e);
            n.addDependOn(n0);
        }

        return n;
    }

    /**
     * Node class abstract a dependent resource and the dependent relationship
     * between the resource and all it's depend on resources
     * 
     * @author greenlaw110@gmail.com
     */
    private static class Node implements Comparable<Node> {
        /**
         * name of the node
         */
        private final String name_;
        /**
         * a map contains all immediate depend on resource of the resource
         * denoted by this node
         * 
         * key - resource name val - resource presented by an <code>Node</code>
         */
        private final Map<String, Node> dependOns_;
        /**
         * Weight is used to help sort nodes
         * 
         * The weight of the node shall always be smaller than the weight of any
         * one of it's depend on nodes
         */
        private long weight_ = 1;

        /**
         * the smallest gap between weight of nodes
         */
        private static final int STEP_ = 10;

        /**
         * keep track whether the dependencies of this node has been updated and
         * needs rectify
         */
        private boolean dirty_ = true;

        /**
         * Construct a <code>Node</code> instance
         * 
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

        public String debugString() {
            String openTag = String.format("<node name='%1$s' weight='%2$s'>",
                    name_, weight_);
            String closeTag = "\n</node>";
            StringBuilder sb = new StringBuilder();
            sb.append(openTag);
            for (Node n : dependOns_.values()) {
                sb.append("\n\t").append(n.debugString());
            }
            sb.append(closeTag);
            return sb.toString();
        }

        public int compareTo(Node o) {
            if (null == o)
                return -1;
            if (equals(o))
                return 0;
            if (this.weight_ == o.weight_)
                return o.name_.compareTo(this.name_);
            else {
                long l = o.weight_ - this.weight_;
                return (l > 0) ? 1 : ((l < 0) ? -1 : 0);
            }
        }

        /**
         * Add a dependOn node
         * 
         * @param dependOn
         */
        void addDependOn(Node dependOn) {
            // check for circular reference
            if (dependOn.dependOn_(this))
                throw new CircularDependenceException(name_, dependOn.name_);
            dependOns_.put(dependOn.name_, dependOn);
            dirty_ = true;
        }

        /**
         * Return all depend on nodes of this node, including indirectly depend
         * on nodes, i.e. the nodes depended on by the depend on node(s) of this
         * node
         * 
         * the return set also include this node itself as this node depend on
         * it self.
         * 
         * @return
         */
        Set<Node> allDependOns() {
            Set<Node> all = new HashSet<Node>();
            if (dirty_) {
                if (!dependOns_.isEmpty()) {
                    for (Node n0 : dependOns_.values()) {
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
         * Flatten the dependence relationship and then recalculate weight of
         * depend on nodes
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
            for (Node n0 : dependOns_.values()) {
                n0.flatten_();
            }
            for (Node n0 : new HashSet<Node>(dependOns_.values())) {
                dependOns_.putAll(n0.dependOns_);
            }
        }

        /**
         * update weights of depend on nodes based on the weight of this node
         */
        void updateDependOnWeights_() {
            incWeightOn_(null);
        }

        private void incWeightOn_(Node node) {
            if (null != node && this.weight_ <= node.weight_) {
                this.weight_ = node.weight_ + STEP_;
            }
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
            if (dependOns_.containsKey(node.name_))
                return true;
            if (dirty_) {
                for (Node n0 : dependOns_.values()) {
                    if (n0.dependOn_(node))
                        return true;
                }
            }
            return false;
        }

    }
}
