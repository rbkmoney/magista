package com.rbkmoney.magista.dsl.proto;

import com.rbkmoney.magista.dsl.DSLInvalidException;
import com.rbkmoney.magista.dsl.def.*;
import com.rbkmoney.magista.dsl.instance.DSLInstanceValidator;

import java.util.Arrays;

/**
 * Created by vpankrashkin on 19.04.17.
 */
public class SortDef extends NamedDef implements DSLInstanceValidator<LimitInst> {
    public static final SortDef INSTANCE = new SortDef();

    public SortDef() {
        super(Arrays.asList(Ordering.INSTANCE ), "sort");
    }

    @Override
    public void validate(LimitInst instance) throws DSLInvalidException {

    }

    public static class Ordering extends ArrayDef {
        public static final Ordering INSTANCE = new Ordering();

        public Ordering() {
            super(new VarParameterDef(Order.INSTANCE, ""));
        }

        public static class Order extends EnumDef {
            public static Order INSTANCE = new Order();

            public Order() {
                super(Arrays.asList(Asc.INSTANCE, Desc.INSTANCE));
            }

            public static class Asc extends ParameterDef {
                public static final Asc INSTANCE = new Asc();
                public Asc() {
                    super(null, "asc");
                }
            }
            public static class Desc extends ParameterDef {
                public static final Desc INSTANCE = new Desc();
                public Desc() {
                    super(null, "desc");
                }

            }
        }
    }
}
