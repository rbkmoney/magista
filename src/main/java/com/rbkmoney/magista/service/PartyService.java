package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.domain.Party;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.PartyException;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Created by tolkonepiu on 29/05/2017.
 */
@Service
public class PartyService {

    private final UserInfo userInfo = new UserInfo("admin", UserType.internal_user(new InternalUser()));

    @Autowired
    PartyManagementSrv.Iface partyManagementSrv;

    public Party getParty(String partyId, Instant timestamp) throws NotFoundException, PartyException {
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

}
