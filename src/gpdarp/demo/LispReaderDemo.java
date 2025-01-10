package gpdarp.demo;

import ec.gp.GPTree;
import gpdarp.gp.UCARPPrimitiveSet;
import gputils.LispUtils;

/**
 * A demo for lisp reader.
 * Given a string lisp expression, one can first simplify the expression,
 * then parse the string into a GPTree class, and print it in a Graphviz format.
 *
 * @author gphhucarp, William Huang
 */
public class LispReaderDemo {
    public static void main(String[] args) {
        String expression =
                "(* (+ (min (max TVPU CRD) (/ COST DUR)) (- DEM SLACK)) (+ (/ RQ (- RT VSLACK)) (+ TVC OBV)))";

        expression = LispUtils.simplifyExpression(expression);

        GPTree gpTree = LispUtils.parseExpression(expression, UCARPPrimitiveSet.primitiveSet());
        System.out.println(gpTree.child.makeGraphvizTree());
    }
}
