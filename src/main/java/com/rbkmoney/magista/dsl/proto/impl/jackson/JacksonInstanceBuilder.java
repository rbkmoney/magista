package com.rbkmoney.magista.dsl.proto.impl.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.rbkmoney.magista.dsl.DSLBuildException;
import com.rbkmoney.magista.dsl.def.*;
import com.rbkmoney.magista.dsl.instance.*;
import com.rbkmoney.magista.dsl.proto.impl.InstanceBuilder;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by vpankrashkin on 13.04.17.
 */
public class JacksonInstanceBuilder<T extends DSLInstance> extends InstanceBuilder<T, JsonNode, JsonNode> {

    public JacksonInstanceBuilder() {
        this(Collections.EMPTY_MAP);
    }

    public JacksonInstanceBuilder(Map<DSLDef, DSLInstanceBuilder> builders) {
        super(builders);
    }

    @Override
    protected JsonNode walkPath(JsonNode src, List<PathPoint<JsonNode>> path, PathPoint<JsonNode> child) throws DSLBuildException {
        PathPoint<JsonNode> parent = peekPoint(path);
        if (child.getSelection() instanceof LinkSelection) {
            return (JsonNode) ((LinkSelection) child.getSelection()).getLinked().getData();
        }
        if (child.getSelection() instanceof IndexSelection) {
            int idx = (Integer) child.getSelection().getKey();
            JsonNode node;
            if (parent.getData().isArray()) {
                node = parent.getData().get(idx);
            } else if (idx == 0) {
                node = parent.getData();
            } else {
                node = null;
            }

            /*DSLDef itemsDef = ((ArrayDef)parent.getDef()).getItemsDef();
            if (node != null && (itemsDef instanceof ParameterDef)) {
                PathPoint<JsonNode> subChildPoint = new PathPoint<>(itemsDef, null, new NameSelection())
                node = walkPath(src, putPoint(path, child), )
            }*/
            return node;
        } else if (child.getDef() instanceof VarNamedDSLDef) {
            Set<String> usedNames = Stream.concat(
                    parent.getDef().getChildDefs().stream()
                            .filter(def -> def instanceof NamedDSLDef && !(def instanceof VarNamedDSLDef))
                            .map(def -> ((NamedDSLDef)def).getName()),
                    parent.getMatchedPoints().stream()
                            .map(p -> (String)p.getSelection().getKey()))
                    .collect(Collectors.toSet()
                    );
            Optional<String> varName = StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(parent.getData().fieldNames(), Spliterator.ORDERED),
                    false).filter(name -> !usedNames.contains(name)).findFirst();
            if (varName.isPresent()) {
                child.setSelection(new NameSelection(varName.get()));//todo change return type to pathpoint
                return parent.getData().get(varName.get());
            } else {
                return null;
            }
        } else if (child.getDef() instanceof NamedDSLDef) {
            return parent.getData().get(((NamedDSLDef) child.getDef()).getName());
        } else {
            throw new DSLBuildException("Can't handle def type: "+parent.getDef());
        }
    }

    @Override
    protected VarParameterInstance buildBaseVarParam(JsonNode src, List<PathPoint<JsonNode>> path, DSLInstance parentInstance) throws DSLBuildException {
        PathPoint<JsonNode> point = peekPoint(path);
        JsonNode pointNode = point.getData();
        VarParameterDef def = (VarParameterDef) point.getDef();
        VarParameterInstance instance = (VarParameterInstance) def.createInstance();
        PathPoint.Selection selection = point.getSelection();
        String varName = null;
        JsonNode varNode;
        if (!(selection instanceof NameSelection)) {
            if (pointNode.isObject()) {
                for (Iterator<String> it = pointNode.fieldNames(); it.hasNext();) {
                    if (varName != null) {
                        throw new DSLBuildException("Can't map var def to multi key object: "+ point);
                    }
                    varName = it.next();
                    varNode = pointNode.get(varName);
                }
            } else {
                varName = pointNode.asText();
            }
        }
        instance.setVarName((String) point.getSelection().getKey());
        PathPoint<SS> childPoint = new PathPoint<>(def.getValueDef(), point.getData(), new LinkSelection(point));
        DSLInstance value = build(src, putPoint(path, childPoint), instance);
        instance.setValue(value);
        pollPoint(path);
        return instance;
    }

    @Override
    protected ParameterInstance buildDefaultParam(JsonNode src, List<PathPoint<JsonNode>> path, DSLInstance parentInstance) throws DSLBuildException {
        PathPoint<SS> point = peekPoint(path);
        ParameterDef def = (ParameterDef) point.getDef();
        ParameterInstance instance = (ParameterInstance) def.createInstance();
        PathPoint<SS> childPoint = new PathPoint<>(def.getValueDef(), point.getData(), new LinkSelection(point));
        DSLInstance value = build(src, putPoint(path, childPoint), instance);
        instance.setValue(value);
        pollPoint(path);
        return instance;
    }

    @Override
    protected LongValueInstance buildBaseLongVal(JsonNode src, List<PathPoint<JsonNode>> path, DSLInstance parentInstance) throws DSLBuildException {
        PathPoint<JsonNode> point = peekPoint(path);
        LongValueInstance instance = ((LongValueInstance)point.getDef().createInstance());
        instance.setValue(point.getData().asLong());//todo add node type validation
        return instance;
    }

    @Override
    protected DoubleValueInstance buildBaseDoubleVal(JsonNode src, List<PathPoint<JsonNode>> path, DSLInstance parentInstance) throws DSLBuildException {
        PathPoint<JsonNode> point = peekPoint(path);
        DoubleValueInstance instance = ((DoubleValueInstance)point.getDef().createInstance());
        instance.setValue(point.getData().asDouble());//todo add node type validation
        return instance;
    }

    @Override
    protected BooleanValueInstance buildBaseBoolVal(JsonNode src, List<PathPoint<JsonNode>> path, DSLInstance parentInstance) throws DSLBuildException {
        PathPoint<JsonNode> point = peekPoint(path);
        BooleanValueInstance instance = ((BooleanValueInstance)point.getDef().createInstance());
        instance.setValue(point.getData().asBoolean());//todo add node type validation
        return instance;
    }

    @Override
    protected StringValueInstance buildBaseStrVal(JsonNode src, List<PathPoint<JsonNode>> path, DSLInstance parentInstance) throws DSLBuildException {
        PathPoint<JsonNode> point = peekPoint(path);
        StringValueInstance instance = ((StringValueInstance)point.getDef().createInstance());
        instance.setValue(point.getData().asText());//todo add node type validation
        return instance;
    }

}
