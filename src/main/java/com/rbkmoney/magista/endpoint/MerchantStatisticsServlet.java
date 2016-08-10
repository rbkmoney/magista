package com.rbkmoney.magista.endpoint;

import com.rbkmoney.damsel.merch_stat.MerchantStatisticsSrv;
import com.rbkmoney.woody.api.event.CompositeServiceEventListener;
import com.rbkmoney.woody.thrift.impl.http.THServiceBuilder;
import com.rbkmoney.woody.thrift.impl.http.event.HttpServiceEventLogListener;
import com.rbkmoney.woody.thrift.impl.http.event.ServiceEventLogListener;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;

/**
 * Created by vpankrashkin on 30.06.16.
 */

@WebServlet("/stat")
public class MerchantStatisticsServlet extends GenericServlet {

    private Servlet thriftServlet;

    @Autowired
    private MerchantStatisticsSrv.Iface requestHandler;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        thriftServlet = new THServiceBuilder()
                .withEventListener(
                        new CompositeServiceEventListener(
                                new ServiceEventLogListener(),
                                new HttpServiceEventLogListener()))
                .build(MerchantStatisticsSrv.Iface.class, requestHandler);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        thriftServlet.service(req, res);
    }
}
