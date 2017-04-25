package com.rbkmoney.magista.dsl.proto;

import com.rbkmoney.magista.dsl.instance.*;

/**
 * Created by vpankrashkin on 13.04.17.
 */
public class SortInst extends KeyedInstance {
    private static final String ORDERING_NAME = "ordering";

    public SortInst() {
        super(LimitDef.INSTANCE);
    }

    public Ordering getOrdering() {
        return (Ordering) getChild(SortDef.Ordering.INSTANCE);
    }

    public void setOrdering(Ordering ordering) {
        setChild(SortDef.Ordering.INSTANCE, ordering);
    }

    public static class Ordering extends ArrayInstance<Ordering.Order> {
        public Ordering() {
            super(SortDef.Ordering.INSTANCE);
        }

        public static class Order extends EnumInstance<SortDef.Ordering.Order> {
            public Order() {
                super(SortDef.Ordering.Order.INSTANCE);
            }

            public static class Asc extends ParameterInstance<SortDef.Ordering.Order.Asc, DSLInstance> {
                public Asc() {
                    super(SortDef.Ordering.Order.Asc.INSTANCE);
                }
            }

            public static class Desc extends ParameterInstance<SortDef.Ordering.Order.Desc, DSLInstance> {
                public Desc() {
                    super(SortDef.Ordering.Order.Desc.INSTANCE);
                }
            }
        }
    }
}
