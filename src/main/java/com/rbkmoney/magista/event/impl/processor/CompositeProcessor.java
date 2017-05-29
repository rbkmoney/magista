package com.rbkmoney.magista.event.impl.processor;

import com.rbkmoney.magista.event.Processor;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tolkonepiu on 29/05/2017.
 */
public class CompositeProcessor implements Processor {

    private final List<Processor> processors;

    public CompositeProcessor(Processor... processors) {
        this.processors = Arrays.asList(processors);
    }

    @Override
    public void execute() {
        processors.stream().forEach(t -> t.execute());
    }
}
