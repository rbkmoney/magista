package com.rbkmoney.magista.dsl.instance;

import com.rbkmoney.magista.dsl.def.DSLDef;

import java.util.ArrayList;

public class DefTree {
        private ArrayList<DSLDef> defs = new ArrayList<>();

        public DefTree(DSLDef def) {
            addNode(def);
        }

        public DefTree() {}

        public DefTree addNode(DSLDef def) {
            defs.add(def);
            return this;
        }

        public DefTree remNode() {
            defs.remove(defs.size() - 1);
            return this;
        }

        public DSLDef getNode() {
            return defs.isEmpty() ? null : defs.get(defs.size() - 1);
        }
    }