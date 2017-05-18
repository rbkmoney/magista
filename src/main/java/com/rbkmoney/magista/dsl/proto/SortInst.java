package com.rbkmoney.magista.dsl.proto;

import com.rbkmoney.magista.dsl.instance.ArrayInstance;
import com.rbkmoney.magista.dsl.instance.ConstStringValueInstance;
import com.rbkmoney.magista.dsl.instance.EnumInstance;
import com.rbkmoney.magista.dsl.instance.KeyedInstance;

/**
 * Created by vpankrashkin on 13.04.17.
 */
public class SortInst extends KeyedInstance {
    private static final String ORDERING_NAME = "ordering";

    public SortInst() {
        super(SortDef.INSTANCE);
    }

    public Ordering getOrdering() {
        return (Ordering) getChild(SortDef.OrderingDef.INSTANCE);
    }

    public void setOrdering(Ordering ordering) {
        setChild(SortDef.OrderingDef.INSTANCE, ordering);
    }

    public static class Ordering extends ArrayInstance<Ordering.Order> {
        public Ordering() {
            super(SortDef.OrderingDef.INSTANCE);
        }

        public static class Order extends EnumInstance<SortDef.OrderingDef.OrderDef> {
            public Order() {
                super(SortDef.OrderingDef.OrderDef.INSTANCE);
            }

            public static class Asc extends ConstStringValueInstance {
                public Asc() {
                    super(SortDef.OrderingDef.OrderDef.AscDef.INSTANCE.getValue());
                }
            }

            public static class Desc extends ConstStringValueInstance {
                public Desc() {
                    super(SortDef.OrderingDef.OrderDef.AscDef.INSTANCE.getValue());
                }
            }
        }
    }
}
