package com.refinement_types.types;

import java.util.HashMap;
import java.util.Map;

import com.refinement_types.bdlc.Type;

public class MLTypeVariable {
    String id;

    public MLTypeVariable(String id, Map<String, Type> fields) {
        this.id = id;
        RefinementTypeDecl.typingContext.put(id, new Type.UserDefined(id, fields));
    }

    public MLTypeVariable(String id) {
        this.id = id;
        RefinementTypeDecl.typingContext.put(id, new Type.UserDefined(id, new HashMap<String, Type>()));
    }

}
