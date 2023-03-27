package com.refinement_types.types;

import java.util.ArrayList;
import java.util.List;

import com.refinement_types.bdlc.Type;
import com.refinement_types.bdlc.TypingContext;

public class RefinementTypeDecl {
    public static TypingContext typingContext = new TypingContext();

    public static interface Recursivity {
        void addParent(String name);

        public class Union implements Recursivity {
            List<Recursivity> recursivities;

            public Union(Recursivity... recursivities) {
                this.recursivities = new ArrayList<>();
                for (Recursivity r : recursivities)
                    this.recursivities.add(r);
            }

            @Override
            public void addParent(String name) {
                for (Recursivity r : recursivities)
                    r.addParent(name);
            }
        }

        public class Arrow implements Recursivity {
            Type mlType;
            Recursivity recursivity;

            @Override
            public void addParent(String name) {
                recursivity.addParent(name);
            }
        }

        public class Constructor implements Recursivity {
            List<Recursivity> recursivityList;

            public Constructor(Recursivity... recursivities) {
                this.recursivityList = new ArrayList<>();
                for (Recursivity r : recursivities)
                    this.recursivityList.add(r);
            }

            @Override
            public void addParent(String name) {
                for (Recursivity r : recursivityList)
                    r.addParent(name);
            }
        }

        public class MLTypeVariableRecursivity implements Recursivity {
            MLTypeVariable mlTypeVariable;
            // String refinementTypeName; // forgone in this demo

            public MLTypeVariableRecursivity(MLTypeVariable mlTypeVariable) {
                this.mlTypeVariable = mlTypeVariable;
            }

            @Override
            public void addParent(String name) {
                ((Type.UserDefined) typingContext.get(mlTypeVariable.id)).getParents()
                        .add(new Type.UserDefined(name, null));
            }
        }
    }

    // MLTypeVariable mlTypeVariable; // Ignored in this demo
    String refinementTypeName;
    Recursivity recursivity;
    RefinementTypeDecl refinementTypeDecl;

    public RefinementTypeDecl(/* MLTypeVariable mlTypeVariable, */String refinementTypeName, Recursivity recursivity,
            RefinementTypeDecl refinementTypeDecl) {
        // this.mlTypeVariable = mlTypeVariable;
        this.refinementTypeName = refinementTypeName;
        this.recursivity = recursivity;
        this.refinementTypeDecl = refinementTypeDecl;

        typingContext.put(refinementTypeName, new Type.UserDefined(refinementTypeName, null));
        recursivity.addParent(refinementTypeName);
    }

}
