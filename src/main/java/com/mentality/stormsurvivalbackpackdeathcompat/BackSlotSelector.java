package com.mentality.stormsurvivalbackpackdeathcompat;

import java.util.function.IntFunction;
import java.util.function.Predicate;

final class BackSlotSelector {
    private BackSlotSelector() {
    }

    static <T> int firstMatch(int size, IntFunction<T> valueAt, Predicate<T> isTarget) {
        for (int index = 0; index < size; index++) {
            if (isTarget.test(valueAt.apply(index))) {
                return index;
            }
        }
        return -1;
    }
}
