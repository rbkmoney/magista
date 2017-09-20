package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.domain.Party;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.PartyException;
import com.rbkmoney.woody.api.flow.error.WRuntimeException;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;

/**
 * Created by tolkonepiu on 29/05/2017.
 */
@Service
public class PartyService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final UserInfo userInfo = new UserInfo("admin", UserType.internal_user(new InternalUser()));

    private final PartyManagementSrv.Iface partyManagementSrv;

    private final RetryTemplate retryTemplate;

    @Autowired
    public PartyService(PartyManagementSrv.Iface partyManagementSrv) {
        this.partyManagementSrv = partyManagementSrv;

        retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(
                new SimpleRetryPolicy(3, Collections.singletonMap(WRuntimeException.class, true))
        );
    }

    public Party getParty(String partyId, Instant timestamp) throws NotFoundException, PartyException {
        return retryTemplate.execute(context -> {
                    if (context.getLastThrowable() != null) {
                        log.error("Failed to get party (partyId='{}', timestamp='{}'), retrying ({})...", partyId, timestamp, context.getRetryCount(), context.getLastThrowable());
                    }

                    try {
                        return partyManagementSrv.checkout(userInfo, partyId, TypeUtil.temporalToString(timestamp));
                    } catch (PartyNotFound ex) {
                        throw new NotFoundException(String.format("Party not found, partyId='%s'", partyId), ex);
                    } catch (PartyNotExistsYet ex) {
                        throw new NotFoundException(String.format("Party not exists at this time, partyId='%s', timestamp='%s'", partyId, timestamp), ex);
                    } catch (TException ex) {
                        throw new PartyException("Exception with get party from hg", ex);
                    }
                }
        );
    }

}
