package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.service.PartyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;

/**
 * Created by tolkonepiu on 29/05/2017.
 */
public class InvoicePartyMapper implements Mapper<InvoiceEventContext> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PartyService partyService;

    public InvoicePartyMapper(PartyService partyService) {
        this.partyService = partyService;
    }

    @Override
    public InvoiceEventContext fill(InvoiceEventContext value) {
        InvoiceEventStat invoiceEventStat = value.getInvoiceEventStat();

        log.debug("Start invoice party enrichment, invoiceId='{}', partyId='{}', eventId='{}'",
                invoiceEventStat.getInvoiceId(), invoiceEventStat.getPartyId(), invoiceEventStat.getEventId());

        try {
            Party party;
            if (invoiceEventStat.getInvoicePartyRevision() != null) {
                party = partyService.getParty(
                        invoiceEventStat.getPartyId(),
                        invoiceEventStat.getInvoicePartyRevision()
                );
            } else {
                party = partyService.getParty(
                        invoiceEventStat.getPartyId(),
                        invoiceEventStat.getInvoiceCreatedAt().toInstant(ZoneOffset.UTC)
                );
            }
            invoiceEventStat.setPartyEmail(party.getContactInfo().getEmail());

            Shop shop = party.getShops().get(invoiceEventStat.getPartyShopId());

            if (shop == null) {
                throw new NotFoundException(
                        String.format("Shop not found, partyId='%s', shopId='%s'",
                                invoiceEventStat.getPartyId(), invoiceEventStat.getPartyShopId())
                );
            }

            invoiceEventStat.setPartyShopCategoryId(shop.getCategory().getId());
            invoiceEventStat.setPartyShopPayoutToolId(shop.getPayoutToolId());
            if (shop.getLocation().isSetUrl()) {
                invoiceEventStat.setPartyShopUrl(shop.getLocation().getUrl());
            }

            ShopDetails shopDetails = shop.getDetails();
            invoiceEventStat.setPartyShopName(shopDetails.getName());
            invoiceEventStat.setPartyShopDescription(shopDetails.getDescription());

            Contract contract = party.getContracts().get(shop.getContractId());

            if (contract == null) {
                throw new NotFoundException(
                        String.format("Contract not found, contractId='%s', partyId='%s', shopId='%s'",
                                shop.getContractId(), invoiceEventStat.getPartyId(), shop.getId())
                );
            }

            invoiceEventStat.setPartyContractId(contract.getId());
            if (contract.isSetPaymentInstitution()) {
                invoiceEventStat.setPaymentInstitutionId(contract.getPaymentInstitution().getId());
            }

            if (contract.isSetContractor()
                    && contract.getContractor().isSetLegalEntity()
                    && contract.getContractor().getLegalEntity().isSetRussianLegalEntity()) {
                RussianLegalEntity entity = contract.getContractor()
                        .getLegalEntity()
                        .getRussianLegalEntity();
                invoiceEventStat.setPartyContractInn(entity.getInn());
                invoiceEventStat.setPartyContractRegisteredNumber(entity.getRegisteredNumber());
            }
        } finally {
            log.debug("End invoice party enrichment, invoiceId='{}', partyId='{}', eventId='{}'",
                    invoiceEventStat.getInvoiceId(), invoiceEventStat.getPartyId(), invoiceEventStat.getEventId());
        }


        return value.setInvoiceEventStat(invoiceEventStat);
    }
}
