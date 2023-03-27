package com.refinement_types.types;

import java.util.List;

public interface RefinementType {
    public static class Intersect implements RefinementType {
        List<RefinementType> refinementTypes;
    }

    public static class Union implements RefinementType {
        List<RefinementType> refinementTypes;
    }

    public static class Constructor implements RefinementType {
        RefinementType in;
        RefinementType out;
    }

    public static class Empty implements RefinementType {
        RefinementType type;
        String name;
    }

    public static class TypeVariable implements RefinementType {
        RefinementType type;
        String name;
    }

    public static class TypeName implements RefinementType {
    }
}
