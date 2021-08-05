package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.magista.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartyManagementService {

    private final PartyManagementSrv.Iface partyManagementClient;
    private final UserInfo userInfo = new UserInfo("admin", UserType.internal_user(new InternalUser()));

    @SneakyThrows
    public PayoutToolInfo getPayoutToolInfo(String partyId, String shopId, String payoutToolId) {
        Shop shop = partyManagementClient.getShop(userInfo, partyId, shopId);
        Contract contract = partyManagementClient.getContract(userInfo, partyId, shop.getContractId());
        return extractPayoutToolInfo(payoutToolId, contract)
                .orElseGet(() -> getDeepPayoutToolInfo(partyId, shopId, payoutToolId));
    }

    private Optional<PayoutToolInfo> extractPayoutToolInfo(String payoutToolId, Contract contract) {
        return contract.getPayoutTools().stream()
                .filter(pt -> payoutToolId.equals(pt.getId()))
                .findAny()
                .map(PayoutTool::getPayoutToolInfo);
    }

    @SneakyThrows
    private PayoutToolInfo getDeepPayoutToolInfo(String partyId, String shopId, String payoutToolId) {
        long currentRevision = partyManagementClient.getRevision(userInfo, partyId);
        for (long revision = currentRevision - 1; revision >= 0; --revision) {
            Party party = partyManagementClient.checkout(userInfo, partyId, PartyRevisionParam.revision(revision));
            Shop shop = party.getShops().get(shopId);
            Contract contract = party.getContracts().get(shop.getContractId());
            Optional<PayoutToolInfo> payoutToolInfo = extractPayoutToolInfo(payoutToolId, contract);
            if (payoutToolInfo.isPresent()) {
                return payoutToolInfo.get();
            }
        }
        throw new NotFoundException(String.format("PayoutToolInfo not found for partyId=%s, shopId=%s, payoutToolId=%s",
                partyId, shopId, payoutToolId));
    }
}
