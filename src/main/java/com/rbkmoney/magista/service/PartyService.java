package com.rbkmoney.magista.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.rbkmoney.damsel.domain.Party;
import com.rbkmoney.damsel.domain.Shop;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.PartyException;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.AbstractMap;
import java.util.Map;

/**
 * Created by tolkonepiu on 29/05/2017.
 */
@Service
public class PartyService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final UserInfo userInfo = new UserInfo("admin", UserType.internal_user(new InternalUser()));

    private final PartyManagementSrv.Iface partyManagementSrv;

    private final RetryTemplate retryTemplate;

    private final Cache<Map.Entry<String, PartyRevisionParam>, Party> partyCache;

    @Autowired
    public PartyService(
            PartyManagementSrv.Iface partyManagementSrv,
            RetryTemplate retryTemplate,
            @Value("${cache.maxSize}") long cacheMaximumSize
    ) {
        this.partyManagementSrv = partyManagementSrv;
        this.retryTemplate = retryTemplate;
        this.partyCache = Caffeine.newBuilder()
                .maximumSize(cacheMaximumSize)
                .build();
    }

    public Shop getShop(String partyId, String shopId) throws NotFoundException {
        return getShop(partyId, shopId, Instant.now());
    }

    public Shop getShop(String partyId, String shopId, long partyRevision) throws NotFoundException {
        return getShop(partyId, shopId, PartyRevisionParam.revision(partyRevision));
    }

    public Shop getShop(String partyId, String shopId, Instant timestamp) throws NotFoundException {
        return getShop(partyId, shopId, PartyRevisionParam.timestamp(TypeUtil.temporalToString(timestamp)));
    }

    public Shop getShop(String partyId, String shopId, PartyRevisionParam partyRevisionParam) throws NotFoundException {
        log.info("Trying to get shop, partyId='{}', shopId='{}', partyRevisionParam='{}'", partyId, shopId, partyRevisionParam);
        Party party = getParty(partyId, partyRevisionParam);

        Shop shop = party.getShops().get(shopId);
        if (shop == null) {
            throw new NotFoundException(String.format("Shop not found, partyId='%s', shopId='%s', partyRevisionParam='%s'", partyId, shopId, partyRevisionParam));
        }
        log.info("Shop has been found, partyId='{}', shopId='{}', partyRevisionParam='{}'", partyId, shopId, partyRevisionParam);
        return shop;
    }

    public Party getParty(String partyId) throws NotFoundException, PartyException {
        return getParty(partyId, Instant.now());
    }

    public Party getParty(String partyId, long partyRevision) throws NotFoundException, PartyException {
        return getParty(partyId, PartyRevisionParam.revision(partyRevision));
    }

    public Party getParty(String partyId, Instant timestamp) throws NotFoundException, PartyException {
        return getParty(partyId, PartyRevisionParam.timestamp(TypeUtil.temporalToString(timestamp)));
    }

    public Party getParty(String partyId, PartyRevisionParam partyRevisionParam) throws NotFoundException, PartyException {
        log.info("Trying to get party, partyId='{}', partyRevisionParam='{}'", partyId, partyRevisionParam);
        Party party = retryTemplate.execute(context -> {
            if (context.getLastThrowable() != null) {
                log.warn("Failed to get party (partyId='{}', partyRevisionParam='{}'), retrying ({})...", partyId, partyRevisionParam, context.getRetryCount(), context.getLastThrowable());
            }

            return partyCache.get(
                    new AbstractMap.SimpleEntry<>(partyId, partyRevisionParam),
                    key -> {
                        try {
                            return partyManagementSrv.checkout(userInfo, key.getKey(), key.getValue());
                        } catch (PartyNotFound ex) {
                            throw new NotFoundException(String.format("Party not found, partyId='%s'", partyId), ex);
                        } catch (InvalidPartyRevision ex) {
                            throw new NotFoundException(String.format("Invalid party revision, partyId='%s', partyRevisionParam='%s'", partyId, partyRevisionParam), ex);
                        } catch (TException ex) {
                            throw new PartyException("Exception with get party from hg", ex);
                        }
                    }
            );


        });
        log.info("Party has been found, partyId='{}', partyRevisionParam='{}'", partyId, partyRevisionParam);
        return party;
    }

}
