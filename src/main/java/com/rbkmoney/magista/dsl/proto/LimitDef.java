package com.rbkmoney.magista.dsl.proto;

import com.rbkmoney.magista.dsl.DSLInvalidException;
import com.rbkmoney.magista.dsl.def.NamedDef;
import com.rbkmoney.magista.dsl.def.LongValueDef;
import com.rbkmoney.magista.dsl.def.ParameterDef;
import com.rbkmoney.magista.dsl.instance.DSLInstanceValidator;

import java.util.Arrays;

import static com.rbkmoney.magista.dsl.instance.ValidationUtils.*;

/**
 * Created by vpankrashkin on 12.04.17.
 */
public class LimitDef extends NamedDef implements DSLInstanceValidator<LimitInst> {
    public static final String NAME = "limit";
    public static final LimitDef INSTANCE = new LimitDef();

    public LimitDef() {
        super(Arrays.asList(SizeDef.INSTANCE, FromDef.INSTANCE), NAME);
    }

    @Override
    public void validate(LimitInst instance) throws DSLInvalidException {
        if (instance.getSize() == null && instance.getFrom() == null) {
            throw genValidationError(this, String.format("At least one of %s or %s must be defined", SizeDef.NAME, FromDef.NAME));
        }
    }

    public static class SizeDef extends ParameterDef implements DSLInstanceValidator<LimitInst.Size> {
        public static final String NAME = "size";
        public static final SizeDef INSTANCE = new SizeDef();

        public SizeDef() {
            super(new LongValueDef(), NAME);
        }

        @Override
        public void validate(LimitInst.Size instance) throws DSLInvalidException {
            validateParamValueNotNull(this, instance);
            checkTrue(instance.getValue().getValue() > 0, this, "Only positive value allowed here");
        }
    }

    public static class FromDef extends ParameterDef implements DSLInstanceValidator<LimitInst.From> {
        public static final String NAME = "from";
        public static final FromDef INSTANCE = new FromDef();

        public FromDef() {
            super(new LongValueDef(), NAME);
        }

        @Override
        public void validate(LimitInst.From instance) throws DSLInvalidException {
            validateParamValueNotNull(this, instance);
            checkTrue(instance.getValue().getValue() >= 0, this, "Only positive or zero value allowed here");
        }
    }
}
