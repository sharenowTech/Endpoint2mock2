package com.car2go.endpoint2mock2;

/**
 * Function which takes no arguments and returns boolean.
 */
public interface BooleanFunction {

    /**
     * @return result of the function.
     */
    boolean call();

    /**
     * Always returns {@code true}.
     */
    BooleanFunction TRUE = new BooleanFunction() {
        @Override
        public boolean call() {
            return true;
        }
    };

    /**
     * Always returns {@code false}.
     */
    BooleanFunction FALSE = new BooleanFunction() {
        @Override
        public boolean call() {
            return false;
        }
    };

}