package com.refinement_types;

import java.util.HashMap;
import java.util.Map;

import com.refinement_types.bdlc.BDLCExpression;
import com.refinement_types.bdlc.BidirectionalLambdaCalculus;
import com.refinement_types.bdlc.Type;
import com.refinement_types.types.MLTypeVariable;
import com.refinement_types.types.RefinementTypeDecl;
import com.refinement_types.types.RefinementTypeDecl.Recursivity;

public class Main {
    public static class Pair<K, V> {
        K k;
        V v;

        public Pair(K k, V v) {
            this.k = k;
            this.v = v;
        }
    }

    public static <K, V> Map<K, V> mapOf(Pair<K, V>... pairs) {
        Map<K, V> m = new HashMap<>();
        for (Pair<K, V> p : pairs)
            m.put(p.k, p.v);
        return m;
    }

    public static Type getType(String id) {
        return RefinementTypeDecl.typingContext.get(id);
    }

    public static void main(String[] args) {
        BidirectionalLambdaCalculus bdlc = new BidirectionalLambdaCalculus(RefinementTypeDecl.typingContext);
        /*
         * Overall plan:
         * -----------------------------------------
         * rectype std = e | stdpos
         * and stdpos = o(e) | z(stdpos) | o(stdpos)
         */

        // e | stdpos
        MLTypeVariable eVar = new MLTypeVariable("e");
        MLTypeVariable stdposVar = new MLTypeVariable("stdpos");
        Recursivity eVarRecursivity = new RefinementTypeDecl.Recursivity.MLTypeVariableRecursivity(eVar);
        Recursivity stdposVarRecursivity = new RefinementTypeDecl.Recursivity.MLTypeVariableRecursivity(stdposVar);
        Recursivity e_stdpos_Union = new Recursivity.Union(eVarRecursivity, stdposVarRecursivity);

        // o(e) | z(stdpos) | o(stdpos)
        Map<String, Type> fields = mapOf(new Pair<String, Type>("inner", getType("stdpos")));
        MLTypeVariable oVar = new MLTypeVariable("o", fields);
        MLTypeVariable zVar = new MLTypeVariable("z", fields);
        Recursivity oVarRecursivity = new RefinementTypeDecl.Recursivity.MLTypeVariableRecursivity(oVar);
        Recursivity zVarRecursivity = new RefinementTypeDecl.Recursivity.MLTypeVariableRecursivity(zVar);
        Recursivity oeConstructor = new Recursivity.Constructor(oVarRecursivity, zVarRecursivity);
        Recursivity oConstructor = new Recursivity.Constructor(oVarRecursivity, stdposVarRecursivity);
        Recursivity zConstructor = new Recursivity.Constructor(zVarRecursivity, stdposVarRecursivity);
        Recursivity oe_o_z_Union = new Recursivity.Union(oeConstructor, oConstructor, zConstructor);

        // stdpos = ...
        RefinementTypeDecl stdposDecl = new RefinementTypeDecl("stdpos", oe_o_z_Union, null);

        // std = ... and ...
        RefinementTypeDecl rtd = new RefinementTypeDecl("std", e_stdpos_Union, stdposDecl);

        // Build expressions with std and stdpos
        Type stdposFun = new Type.Function(getType("stdpos"), new Type.Int());
        Type stdFun = new Type.Function(getType("std"), new Type.Int());
        RefinementTypeDecl.typingContext.put("stdposFun", stdposFun);
        RefinementTypeDecl.typingContext.put("stdFun", stdFun);
        BDLCExpression stdposFunVar = new BDLCExpression.Variable("stdposFun");
        BDLCExpression stdFunVar = new BDLCExpression.Variable("stdFun");
        BDLCExpression stdposApp = new BDLCExpression.Application(stdposFunVar, new BDLCExpression.Variable("stdpos"));
        BDLCExpression stdApp = new BDLCExpression.Application(stdFunVar, new BDLCExpression.Variable("std"));
        System.out.println(bdlc.typeCheck(stdposApp, new Type.Int()));
        System.out.println(bdlc.typeCheck(stdApp, new Type.Int()));

        // prove stdpos <: std && !(std <: stdpos)
        BDLCExpression passApp = new BDLCExpression.Application(stdposFunVar, new BDLCExpression.Variable("std"));
        BDLCExpression failApp = new BDLCExpression.Application(stdApp, new BDLCExpression.Variable("stdpos"));
        System.out.println(bdlc.typeCheck(passApp, new Type.Int()));
        System.out.println(bdlc.typeCheck(failApp, new Type.Int()));

        // Show s.inner types if s : stdpos, but not if s : std
        BDLCExpression oExprVar = new BDLCExpression.Variable("o");
        BDLCExpression eExprVar = new BDLCExpression.Variable("e");
        BDLCExpression stdposField = new BDLCExpression.Access(oExprVar, "inner");
        BDLCExpression stdField = new BDLCExpression.Access(eExprVar, "inner");
        System.out.println(bdlc.typeCheck(stdposField, getType("stdpos")));
        System.out.println(bdlc.typeCheck(stdField, getType("stdpos")));
    }
}
