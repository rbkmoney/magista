package com.rbkmoney.magista.dsl.instance;

/**
 * Created by vpankrashkin on 17.05.17.
 */
public class ConstStringValueInstance extends StringValueInstance {

    public ConstStringValueInstance(String value) {
        super(value);
    }

    @Override
    public void setValue(String value) {
        throw new UnsupportedOperationException();
    }
}
