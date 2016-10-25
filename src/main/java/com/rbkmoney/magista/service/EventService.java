package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.magista.dao.EventDao;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.StorageException;
import com.rbkmoney.magista.handler.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by tolkonepiu on 24.08.16.
 */
@Service
public class EventService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    EventDao eventDao;

    @Autowired
    List<Handler> handlers;

    public Long getLastEventId() {
        Long lastEventId;
        try {
            log.trace("Get last event id");
            lastEventId = eventDao.getLastEventId();
        } catch (DaoException ex) {
            throw new StorageException("Failed to get last event id");
        }
        return lastEventId;
    }

    public void processEvent(StockEvent stockEvent) {
        for (Handler handler : handlers) {
            if (handler.accept(stockEvent)) {
                handler.handle(stockEvent);
                break;
            }
        }
    }

}
