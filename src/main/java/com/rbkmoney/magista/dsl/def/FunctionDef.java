package com.rbkmoney.magista.dsl.def;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by vpankrashkin on 11.04.17.
 */
public abstract class FunctionDef extends NamedDef implements FunctionDSLDef {
    private final Map<String, ParameterDSLDef> inputParameters;
    private final Map<String, ParameterDSLDef> outputParameters;

    public FunctionDef(List<? extends DSLDef> nestedDSLList, List<? extends ParameterDSLDef> inputParameters, List<? extends ParameterDSLDef> outputParameters, String name) {
        super(Stream.concat(Stream.concat(nestedDSLList.stream(), inputParameters.stream()), outputParameters.stream()).collect(Collectors.toList()), name);
        this.inputParameters = Collections.unmodifiableMap(inputParameters.stream().collect(Collectors.toMap(def -> def.getName(), def -> def)));
        this.outputParameters = Collections.unmodifiableMap(outputParameters.stream().collect(Collectors.toMap(def -> def.getName(), def -> def)));
    }

    @Override
    public Map<String, ParameterDSLDef> getInputParams() {
        return inputParameters;
    }

    @Override
    public Map<String, ParameterDSLDef> getOutputParams() {
        return outputParameters;
    }
}
