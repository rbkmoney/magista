package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.domain.Contract;
import com.rbkmoney.damsel.domain.Party;
import com.rbkmoney.damsel.domain.Shop;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.PartyException;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by tolkonepiu on 29/05/2017.
 */
@Service
public class PartyService {

    private final UserInfo userInfo = new UserInfo("admin", UserType.internal_user(new InternalUser()));

    @Autowired
    PartyManagementSrv.Iface partyManagementSrv;

    public Party getParty(String partyId) {
        try {
            return partyManagementSrv.get(userInfo, partyId);
        } catch (PartyNotFound ex) {
            throw new NotFoundException(String.format("Party not found, partyId='%s'", partyId), ex);
        } catch (TException ex) {
            throw new PartyException("Exception with get party from hg", ex);
        }
    }


    public Shop getShopById(String partyId, int shopId) {
        try {
            return partyManagementSrv.getShop(userInfo, partyId, shopId);
        } catch (ShopNotFound ex) {
            throw new NotFoundException(String.format("Shop not found, partyId='%s', shopId='%d'", partyId, shopId), ex);
        } catch (PartyNotFound ex) {
            throw new NotFoundException(String.format("Party not found, partyId='%s', shopId='%d'", partyId, shopId), ex);
        } catch (TException ex) {
            throw new PartyException("Exception with get shop from hg", ex);
        }
    }

    public Contract getContract(String partyId, int shopId) {
        try {
            return partyManagementSrv.getContract(userInfo, partyId, shopId);
        } catch (PartyNotFound ex) {
            throw new NotFoundException(String.format("Party not found, partyId='%s', shopId='%d'", partyId, shopId), ex);
        } catch (ContractNotFound ex) {
            throw new NotFoundException(String.format("Contract not found, partyId='%s', shopId='%d'", partyId, shopId), ex);
        } catch (TException ex) {
            throw new PartyException("Exception with get contract from hg", ex);
        }
    }

}
