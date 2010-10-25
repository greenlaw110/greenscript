package com.greenscriptool;

/**
 * <code>CircularDependenceException</code> is thrown out when a circular dependence 
 * relationship is found by {@link IDependenceManager}.
 * 
 * As an example of circular dependence relationship, suppose A depend on B, which in
 * turn depend on C and D, while D depends on A directly or indirectly.
 * 
 * @author greenlaw110@gmail.com
 * @version 1.0, 2010-10-13
 * @since 1.0
 */
public class CircularDependenceException extends RuntimeException {

    private static final long serialVersionUID = -5547761752401762149L;

    public CircularDependenceException(String element1, String element2) {
        super(String.format("Circular dependence relationship found between %1$s and %1$s", element1, element2));
    }
}
