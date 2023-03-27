package com.refinement_types.bdlc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Types 𝐴, 𝐵,𝐶 ::= unit | 𝐴 → 𝐴
 */
public abstract class Type {
    public boolean isSubtype(Type t) {
        return this.equals(t);
    }

    public static class Unit extends Type {
        @Override
        public boolean equals(Object t) {
            return (t instanceof Unit);
        }

        @Override
        public String toString() {
            return "()";
        }
    }

    public static class Function extends Type {
        Type left;
        Type right;

        public Function(Type left, Type right) {
            this.left = left;
            this.right = right;
        }

        public Type getLeft() {
            return left;
        }

        public Type getRight() {
            return right;
        }

        @Override
        public boolean equals(Object t) {
            return (t instanceof Function)
                    && Objects.equals(((Function) t).left, left)
                    && Objects.equals(((Function) t).right, right);
        }

        @Override
        public boolean isSubtype(Type t) {
            if (!(t instanceof Function))
                return false;
            Function f = (Function) t;
            return this.left.isSubtype(f.left) && f.right.isSubtype(this.right);
        }

        @Override
        public String toString() {
            return "(" + this.left.toString() + ")->(" + this.right.toString() + ")";
        }
    }

    public static class Intersection extends Type {
        List<Type> types;

        public Intersection(List<Type> types) {
            this.types = types;
        }

        public Intersection(Type... types) {
            this.types = new ArrayList<>();
            for (Type t : types) {
                this.types.add(t);
            }
        }

        @Override
        public String toString() {
            String result = "";
            String spacer = "";
            for (Type t : types) {
                result += spacer + t.toString();
                spacer = "/\\";
            }
            return result;
        }

        @Override
        public boolean equals(Object t) {
            return t instanceof Intersection && this.types.equals(((Intersection) t).types);
        }

        @Override
        public boolean isSubtype(Type t) {
            for (Type s : types)
                if (s.isSubtype(t))
                    return true;
            return false;
        }
    }

    public static class Int extends Type {

        @Override
        public boolean equals(Object t) {
            return t instanceof Int;
        }
    }

    public static class Bool extends Type {

        @Override
        public boolean equals(Object t) {
            return t instanceof Bool;
        }

        @Override
        public int hashCode() {
            return "bool".hashCode();
        }
    }

    public static class UserDefined extends Type {
        String id;
        Set<UserDefined> parents;
        Map<String, Type> fields;

        public UserDefined(String id, Map<String, Type> fields) {
            this.id = id;
            this.parents = new HashSet<>();
            this.fields = fields;
        }

        public Set<UserDefined> getParents() {
            return parents;
        }

        @Override
        public boolean equals(Object t) {
            return t instanceof UserDefined && Objects.equals(((UserDefined) t).id, this.id);
        }

        @Override
        public int hashCode() {
            return this.id.hashCode();
        }

        @Override
        public boolean isSubtype(Type t) {
            return equals(t) || this.parents.contains(t);
        }
    }
}
