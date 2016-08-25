package com.rbkmoney.magista.service;

import com.rbkmoney.magista.dao.EventDao;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by tolkonepiu on 24.08.16.
 */
@Service
public class EventService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    EventDao eventDao;

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

}
