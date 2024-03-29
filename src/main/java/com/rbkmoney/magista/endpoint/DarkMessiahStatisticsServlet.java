package com.rbkmoney.magista.endpoint;

import com.rbkmoney.magista.dark.messiah.DarkMessiahMerchantStatisticsServiceSrv;
import com.rbkmoney.woody.thrift.impl.http.THServiceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/v3/dark/stat")
public class DarkMessiahStatisticsServlet extends GenericServlet {

    private Servlet thriftServlet;

    @Autowired
    private DarkMessiahMerchantStatisticsServiceSrv.Iface requestHandler;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        thriftServlet = new THServiceBuilder()
                .build(DarkMessiahMerchantStatisticsServiceSrv.Iface.class, requestHandler);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        thriftServlet.service(req, res);
    }
}