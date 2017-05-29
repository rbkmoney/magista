package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.service.PartyService;

/**
 * Created by tolkonepiu on 29/05/2017.
 */
public class InvoicePartyMapper implements Mapper<InvoiceEventContext> {

    private final PartyService partyService;

    public InvoicePartyMapper(PartyService partyService) {
        this.partyService = partyService;
    }

    @Override
    public InvoiceEventContext fill(InvoiceEventContext value) {
        InvoiceEventStat invoiceEventStat = value.getInvoiceEventStat();

        Party party = partyService.getParty(invoiceEventStat.getPartyId());
        invoiceEventStat.setPartyEmail(party.getContactInfo().getEmail());

        Shop shop = partyService.getShopById(invoiceEventStat.getPartyId(), invoiceEventStat.getPartyShopId());
        invoiceEventStat.setPartyShopCategoryId(shop.getCategory().getId());
        invoiceEventStat.setPartyShopPayoutToolId(shop.getPayoutToolId());

        ShopDetails shopDetails = shop.getDetails();
        invoiceEventStat.setPartyShopName(shopDetails.getName());
        invoiceEventStat.setPartyShopDescription(shopDetails.getDescription());
        if (shopDetails.isSetLocation() && shopDetails.getLocation().isSetUrl()) {
            invoiceEventStat.setPartyShopUrl(shopDetails.getLocation().getUrl());
        }

        Contract contract = partyService.getContract(invoiceEventStat.getPartyId(), invoiceEventStat.getPartyShopId());
        invoiceEventStat.setPartyContractId(contract.getId());
        if (contract.isSetContractor() && contract.getContractor().getEntity().isSetRussianLegalEntity()) {
            RussianLegalEntity entity = contract.getContractor().getEntity().getRussianLegalEntity();
            invoiceEventStat.setPartyContractInn(entity.getInn());
            invoiceEventStat.setPartyContractRegisteredNumber(entity.getRegisteredNumber());
        }

        return value.setInvoiceEventStat(invoiceEventStat);
    }
}
