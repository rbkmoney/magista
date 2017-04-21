package com.rbkmoney.magista.dsl.def;

import java.util.Map;

/**
 * Created by vpankrashkin on 06.04.17.
 */
public interface FunctionDSLDef extends NamedDSLDef {
    Map<String, ParameterDSLDef> getInputParams();//ValueDef inputParameters - used for validating condition params, no need in visibility control for nested defs
    Map<String, ParameterDSLDef> getOutputParams();//output parameters - used for sort and partial extraction
}
