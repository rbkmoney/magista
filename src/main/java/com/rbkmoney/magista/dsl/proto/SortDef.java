package com.rbkmoney.magista.dsl.proto;

import com.rbkmoney.magista.dsl.DSLInvalidException;
import com.rbkmoney.magista.dsl.def.*;
import com.rbkmoney.magista.dsl.instance.DSLInstance;
import com.rbkmoney.magista.dsl.instance.DSLInstanceValidator;

import java.util.Arrays;

/**
 * Created by vpankrashkin on 19.04.17.
 */
public class SortDef extends NamedDef implements DSLInstanceValidator<LimitInst> {
    public static final SortDef INSTANCE = new SortDef();

    public SortDef() {
        super(Arrays.asList(OrderingDef.INSTANCE ), "sort");
    }

    @Override
    public void validate(LimitInst instance) throws DSLInvalidException {

    }

    @Override
    public DSLInstance createInstance() {
        return new SortInst();
    }

    public static class OrderingDef extends ArrayDef {
        public static final OrderingDef INSTANCE = new OrderingDef();

        public OrderingDef() {
            super(new VarParameterDef(OrderDef.INSTANCE, ""));
        }

        @Override
        public DSLInstance createInstance() {
            return new SortInst.Ordering();
        }

        public static class OrderDef extends EnumDef {
            public static OrderDef INSTANCE = new OrderDef();

            public OrderDef() {
                super(Arrays.asList(AscDef.INSTANCE, DescDef.INSTANCE));
            }

            @Override
            public DSLInstance createInstance() {
                return new SortInst.Ordering.Order();
            }

            public static class AscDef extends ParameterDef {
                public static final AscDef INSTANCE = new AscDef();
                public AscDef() {
                    super(null, "asc");
                }

                @Override
                public DSLInstance createInstance() {
                    return new SortInst.Ordering.Order.Asc();
                }
            }
            public static class DescDef extends ParameterDef {
                public static final DescDef INSTANCE = new DescDef();
                public DescDef() {
                    super(null, "desc");
                }

                @Override
                public DSLInstance createInstance() {
                    return new SortInst.Ordering.Order.Desc();
                }

            }
        }
    }
}
