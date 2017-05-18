package com.rbkmoney.magista.dsl.instance;

import com.rbkmoney.magista.dsl.def.DSLDef;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vpankrashkin on 16.05.17.
 */
public class PathPoint<T> {
    private DSLDef def;
    private T data;
    private Selection selection;
    private List<PathPoint<T>> matchedPoints = new ArrayList<>();
    private List<DSLDef> visitedDefs = new ArrayList<>();

    public PathPoint() {
    }

    public PathPoint(DSLDef def, T data, Selection selection) {
        this.def = def;
        this.data = data;
        this.selection = selection;
    }

    public DSLDef getDef() {
        return def;
    }

    public void setDef(DSLDef def) {
        this.def = def;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Selection getSelection() {
        return selection;
    }

    public void setSelection(Selection selection) {
        this.selection = selection;
    }

    public List<PathPoint<T>> getMatchedPoints() {
        return matchedPoints;
    }

    public void setMatchedPoints(List<PathPoint<T>> matchedPoints) {
        this.matchedPoints = matchedPoints;
    }

    public void addMatchedPoint(PathPoint<T> point) {
        matchedPoints.add(point);
    }

    public List<DSLDef> getVisitedDefs() {
        return visitedDefs;
    }

    public void setVisitedDefs(List<DSLDef> visitedDefs) {
        this.visitedDefs = visitedDefs;
    }

    public void addVisitedDef(DSLDef def) {
        visitedDefs.add(def);
    }

    @Override
    public String toString() {
        return "PathPoint{" +
                "def=" + def +
                ", data=" + data +
                ", selection=" + selection +
                ", matchedPoints=" + matchedPoints +
                '}';
    }

    public static class Selection<K> {
        private final K key;

        public Selection(K key) {
            this.key = key;
        }

        public K getKey() {
            return key;
        }

        @Override
        public String toString() {
            return "Selection{" +
                    "key=" + key +
                    '}';
        }
    }
}
