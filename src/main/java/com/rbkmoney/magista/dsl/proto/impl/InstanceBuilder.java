package com.rbkmoney.magista.dsl.proto.impl;

import com.rbkmoney.magista.dsl.DSLBuildException;
import com.rbkmoney.magista.dsl.def.*;
import com.rbkmoney.magista.dsl.instance.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by vpankrashkin on 21.04.17.
 */
public abstract class InstanceBuilder<T extends DSLInstance, S, SS> implements DSLInstanceBuilder<T, S, SS> {
    private Map<DSLDef, DSLInstanceBuilder> builders = new HashMap<>();

    public InstanceBuilder(Map<DSLDef, DSLInstanceBuilder> builders) {
        this.builders.putAll(builders);
    }

    @Override
    public T build(S src, List<PathPoint<SS>> path, DSLInstance parentInstance) throws DSLBuildException {
        if (src == null) {
            return null;
        }
        DSLInstanceBuilder builder = builders.get(peekPoint(path));
        if (builder != null) {
            return (T) builder.build(src, path, parentInstance);
        } else {
            return (T) fillInstance(src, path, buildBase(src, path, parentInstance));
        }
    }

    private DSLInstance fillInstance(S src, List<PathPoint<SS>> path, DSLInstance instance) {
        DSLDef def = instance.getDef();
        PathPoint point = peekPoint(path);
        if (point.getDef() != def) {//if tree contains child defs, consider they're already processed
            do {
                pollPoint(path).getDef();
                point = peekPoint(path);
            } while (point != null && point.getDef() != def);
            return instance;
        }

        PathPoint<SS> childPoint;
        while ((childPoint = getNexChildPoint(src, path)) != null) {
            DSLInstance value = build(src, putPoint(path, childPoint), instance);
            instance.setChild(childPoint.getDef(), value);
            point.addMatchedPoint(childPoint);
            pollPoint(path);
        }
        return instance;

    }


    protected PathPoint<SS> getNexChildPoint(S src, List<PathPoint<SS>> path) throws DSLBuildException {
        PathPoint<SS> point = peekPoint(path);
        DSLDef def = point.getDef();
        PathPoint<SS> childPoint = null;
        if (def instanceof ArrayDef) {
            int nextIdx = point.getMatchedPoints().size();
            childPoint = new PathPoint<>(((ArrayDef) point.getDef()).getItemsDef(), null, new IndexSelection(nextIdx));

        } else if (def instanceof EnumDef) {
            if (point.getMatchedPoints().size() > 0) {
                return null;
            } else {
                int processedDefs = point.getVisitedDefs().size();
                if (def.getChildDefs().size() <= processedDefs) {
                    return null;
                }
                DSLDef childDef;
                for (int i = processedDefs; i < def.getChildDefs().size(); ++i) {
                    childDef = def.getChildDefs().get(i);
                    //if (childDef instanceof NamedDSLDef)//todo value enums can be accepted
                }
                //childPoint = new PathPoint<>(def.getChildDefs().get(processedDefs), point.getData(), );
            }
        } else if (def.getChildDefs().size() == 1 && !(def.getChildDefs().get(0) instanceof NamedDSLDef)) {
            childPoint = new PathPoint<>(def.getChildDefs().get(0), point.getData(), new LinkSelection(point));
        } else {
            DSLDef parentDef = point.getDef();
            Set<DSLDef> processedDefs = point.getMatchedPoints().stream().map(p -> p.getDef()).collect(Collectors.toSet());
            for (DSLDef childDef : parentDef.getChildDefs()) {
                if (childDef instanceof NamedDSLDef && !processedDefs.contains(childDef)) {
                    childPoint = new PathPoint<>(childDef, null, new NameSelection(((NamedDSLDef) childDef).getName()));
                    break;
                }
            }
        }

        if (childPoint == null) {
            return null;
        }
        SS elemSrc = walkPath(src, path, childPoint);
        point.addVisitedDef(childPoint.getDef());
        if (elemSrc == null) {
            return null;
        } else {
            childPoint.setData(elemSrc);
            return childPoint;
        }
    }

    /**
     * @param src parent, root or any other object, depends on implementation. By default def tree navigation is relied on builder implementation.
     *            For navigating in array, all sequentially read defs're added as tree nodes, so same defs count to nearest array def up in tree is number or reading element (1 based)
     * @return src object representation of last defPath node or any other object, allowing to get this value
     */
    abstract protected SS walkPath(S src, List<PathPoint<SS>> parentPath, PathPoint<SS> child) throws DSLBuildException;

    private DSLInstance buildBase(S src, List<PathPoint<SS>> path, DSLInstance parentInstance) throws DSLBuildException {
        if (src == null) {
            return null;
        }

        DSLDef def = peekPoint(path).getDef();

        if (def instanceof FunctionDef) {
            return buildBaseFunc(src, path, parentInstance);
        } else if (def instanceof ArrayDef) {
            return buildBaseArray(src, path, parentInstance);
        } else if (def instanceof EnumDef) {
            return buildBaseEnum(src, path, parentInstance);
        } else if (def instanceof ParameterDef) {
            return buildBaseParam(src, path, parentInstance);
        } else if (def instanceof ValueDef) {
            return buildBaseVal(src, path, parentInstance);
        } else {
            return buildDefault(src, path, parentInstance);
        }
    }

    protected FunctionInstance buildBaseFunc(S src, List<PathPoint<SS>> path, DSLInstance parentInstance) throws DSLBuildException {
        return (FunctionInstance) peekPoint(path).getDef().createInstance();
    }

    protected EnumInstance buildBaseEnum(S src, List<PathPoint<SS>> path, DSLInstance parentInstance) throws DSLBuildException {
        return (EnumInstance) peekPoint(path).getDef().createInstance();
    }

    protected ArrayInstance buildBaseArray(S src, List<PathPoint<SS>> path, DSLInstance parentInstance) throws DSLBuildException {
        return ((ArrayInstance) peekPoint(path).getDef().createInstance());
    }

    protected ValueInstance buildBaseVal(S src, List<PathPoint<SS>> path, DSLInstance parentInstance) throws DSLBuildException {
        DSLDef def = peekPoint(path).getDef();
        if (def instanceof NumberValueDef) {
            return buildBaseNumVal(src, path, parentInstance);
        } else if (def instanceof StringValueDef) {
            return buildBaseStrVal(src, path, parentInstance);
        } else if (def instanceof BooleanValueDef) {
            return buildBaseBoolVal(src, path, parentInstance);
        } else {
            return buildDefaultVal(src, path, parentInstance);
        }
    }

    protected NumberValueInstance buildBaseNumVal(S src, List<PathPoint<SS>> path, DSLInstance parentInstance) throws DSLBuildException {
        DSLDef def = peekPoint(path).getDef();
        if (def instanceof LongValueDef) {
            return buildBaseLongVal(src, path, parentInstance);
        } else if (def instanceof DoubleValueDef) {
            return buildBaseDoubleVal(src, path, parentInstance);
        } else {
            return buildDefaultNumVal(src, path, parentInstance);
        }
    }


    abstract protected LongValueInstance buildBaseLongVal(S src, List<PathPoint<SS>> path, DSLInstance parentInstance) throws DSLBuildException;

    abstract protected DoubleValueInstance buildBaseDoubleVal(S src, List<PathPoint<SS>> path, DSLInstance parentInstance) throws DSLBuildException;

    abstract protected BooleanValueInstance buildBaseBoolVal(S src, List<PathPoint<SS>> path, DSLInstance parentInstance) throws DSLBuildException;

    abstract protected StringValueInstance buildBaseStrVal(S src, List<PathPoint<SS>> path, DSLInstance parentInstance) throws DSLBuildException;

    protected NumberValueInstance buildDefaultNumVal(S src, List<PathPoint<SS>> path, DSLInstance parentInstance) throws DSLBuildException {
        throw new DSLBuildException("Unknown number def: " + peekPoint(path).getDef());
    }

    protected ValueInstance buildDefaultVal(S src, List<PathPoint<SS>> path, DSLInstance parentInstance) throws DSLBuildException {
        throw new DSLBuildException("Unknown value def: " + peekPoint(path).getDef());
    }


    protected ParameterInstance buildBaseParam(S src, List<PathPoint<SS>> path, DSLInstance parentInstance) throws DSLBuildException {
        DSLDef def = peekPoint(path).getDef();
        if (def instanceof VarParameterDef) {
            return buildBaseVarParam(src, path, parentInstance);
        } else {
            return buildDefaultParam(src, path, parentInstance);
        }
    }

    protected VarParameterInstance buildBaseVarParam(S src, List<PathPoint<SS>> path, DSLInstance parentInstance) throws DSLBuildException {
        PathPoint<SS> point = peekPoint(path);
        VarParameterDef def = (VarParameterDef) point.getDef();
        VarParameterInstance instance = (VarParameterInstance) def.createInstance();
        instance.setVarName((String) point.getSelection().getKey());
        PathPoint<SS> childPoint = new PathPoint<>(def.getValueDef(), point.getData(), new LinkSelection(point));
        DSLInstance value = build(src, putPoint(path, childPoint), instance);
        instance.setValue(value);
        pollPoint(path);
        return instance;
    }

    protected ParameterInstance buildDefaultParam(S src, List<PathPoint<SS>> path, DSLInstance parentInstance) throws DSLBuildException {
        PathPoint<SS> point = peekPoint(path);
        ParameterDef def = (ParameterDef) point.getDef();
        ParameterInstance instance = (ParameterInstance) def.createInstance();
        PathPoint<SS> childPoint = new PathPoint<>(def.getValueDef(), point.getData(), new LinkSelection(point));
        DSLInstance value = build(src, putPoint(path, childPoint), instance);
        instance.setValue(value);
        pollPoint(path);
        return instance;
    }

    protected DSLInstance buildDefault(S src, List<PathPoint<SS>> path, DSLInstance parentInstance) throws DSLBuildException {
        return peekPoint(path).getDef().createInstance();
    }

    public static <SS> PathPoint<SS> peekPoint(List<PathPoint<SS>> path) {
        return path.get(path.size() - 1);
    }

    public static <SS> PathPoint<SS> pollPoint(List<PathPoint<SS>> path) {
        PathPoint<SS> point = peekPoint(path);
        path.remove(path.size() - 1);
        return point;
    }

    public static <SS> List<PathPoint<SS>> putPoint(List<PathPoint<SS>> path, PathPoint<SS> point) {
        path.add(point);
        return path;
    }

    public static class IndexSelection extends PathPoint.Selection<Integer> {

        public IndexSelection(Integer key) {
            super(key);
        }
    }

    public static class NameSelection extends PathPoint.Selection<String> {

        public NameSelection(String key) {
            super(key);
        }
    }

    public static class LinkSelection extends PathPoint.Selection {
        private final PathPoint linked;

        public LinkSelection(PathPoint pathPoint) {
            super(pathPoint.getSelection().getKey());
            this.linked = pathPoint;
        }

        public PathPoint getLinked() {
            return linked;
        }

        @Override
        public String toString() {
            return "LinkSelection{" +
                    "linked=" + linked +
                    "} " + super.toString();
        }
    }

}
