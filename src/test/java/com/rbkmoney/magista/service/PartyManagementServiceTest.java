package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.payment_processing.PartyManagementSrv;
import com.rbkmoney.damsel.payment_processing.PartyRevisionParam;
import com.rbkmoney.magista.config.PostgresqlSpringBootITest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@PostgresqlSpringBootITest
public class PartyManagementServiceTest {

    private static final String SHOP_ID_1 = "shop_id_1";
    private static final String SHOP_ID_2 = "shop_id_2";
    private static final String CONTRACT_ID_1 = "contract_id_";
    private static final String CONTRACT_ID_2 = "contract_id_2";
    private static final String PAYOUT_TOOL_ID_1 = "payout_tool_id_1";
    private static final String PAYOUT_TOOL_ID_2 = "payout_tool_id_2";
    private static final PayoutToolInfo EXPECTED_PAYOUT_TOOL_INFO_1 =
            PayoutToolInfo.wallet_info(new WalletInfo().setWalletId("wallet_id_1"));
    private static final PayoutToolInfo EXPECTED_PAYOUT_TOOL_INFO_2 =
            PayoutToolInfo.wallet_info(new WalletInfo().setWalletId("wallet_id_2"));

    @Autowired
    private PartyManagementService partyManagementService;

    @MockBean
    private PartyManagementSrv.Iface partyManagementClient;

    @SneakyThrows
    @BeforeEach
    public void setup() {

        given(partyManagementClient.getShop(any(), any(), eq(SHOP_ID_1)))
                .willReturn(new Shop().setContractId(CONTRACT_ID_1));
        given(partyManagementClient.getShop(any(), any(), eq(SHOP_ID_2)))
                .willReturn(new Shop().setContractId(CONTRACT_ID_2));
        given(partyManagementClient.getContract(any(), any(), eq(CONTRACT_ID_1)))
                .willReturn(new Contract().setPayoutTools(List.of(
                        new PayoutTool()
                                .setId(PAYOUT_TOOL_ID_1)
                                .setPayoutToolInfo(EXPECTED_PAYOUT_TOOL_INFO_1))));
        given(partyManagementClient.getContract(any(), any(), eq(CONTRACT_ID_2)))
                .willReturn(new Contract().setPayoutTools(List.of()));

        given(partyManagementClient.getRevision(any(), any())).willReturn(3L);
        given(partyManagementClient.checkout(any(), any(), eq(PartyRevisionParam.revision(2))))
                .willReturn(new Party()
                        .setShops(Map.of(SHOP_ID_2, new Shop().setContractId("kek_contract_id")))
                        .setContracts(Map.of("kek_contract_id",
                                new Contract().setPayoutTools(List.of(new PayoutTool().setId("kek_id"))))));
        given(partyManagementClient.checkout(any(), any(), eq(PartyRevisionParam.revision(1))))
                .willReturn(new Party()
                        .setShops(Map.of(SHOP_ID_2, new Shop().setContractId(CONTRACT_ID_2)))
                        .setContracts(Map.of(CONTRACT_ID_2,
                                new Contract().setPayoutTools(List.of(
                                        new PayoutTool().setId(PAYOUT_TOOL_ID_2)
                                                .setPayoutToolInfo(EXPECTED_PAYOUT_TOOL_INFO_2))))));

    }

    @SneakyThrows
    @Test
    public void getPayoutToolInfoTest() {
        PayoutToolInfo actualPayoutToolInfo = partyManagementService.getPayoutToolInfo(
                "party_id_1", SHOP_ID_1, PAYOUT_TOOL_ID_1);
        assertEquals(EXPECTED_PAYOUT_TOOL_INFO_1, actualPayoutToolInfo);
    }

    @SneakyThrows
    @Test
    public void getDeepPayoutToolInfoTest() {
        PayoutToolInfo actualPayoutToolInfo = partyManagementService.getPayoutToolInfo(
                "party_id_1", SHOP_ID_2, PAYOUT_TOOL_ID_2);
        assertEquals(EXPECTED_PAYOUT_TOOL_INFO_2, actualPayoutToolInfo);
    }

}