package com.rbkmoney.magista.query;

import com.rbkmoney.magista.dao.AbstractDaoTest;
import com.rbkmoney.magista.dao.ReportDao;
import com.rbkmoney.magista.dao.SearchDao;
import com.rbkmoney.magista.dao.StatisticsDao;
import com.rbkmoney.magista.dao.impl.ReportDaoImpl;
import com.rbkmoney.magista.dao.impl.SearchDaoImpl;
import com.rbkmoney.magista.dao.impl.StatisticsDaoImpl;
import com.rbkmoney.magista.query.impl.QueryContextFactoryImpl;
import com.rbkmoney.magista.query.impl.QueryProcessorImpl;
import com.rbkmoney.magista.query.impl.builder.QueryBuilderImpl;
import com.rbkmoney.magista.query.impl.parser.JsonQueryParser;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Created by jeckep on 08.02.17.
 */

@ContextConfiguration(classes = {AbstractQueryTest.TestConfiguration.class, StatisticsDaoImpl.class, SearchDaoImpl.class, ReportDaoImpl.class})
public abstract class AbstractQueryTest extends AbstractDaoTest {

    protected QueryProcessorImpl queryProcessor;

    @Autowired
    private StatisticsDao statisticsDao;

    @Autowired
    private SearchDao searchDao;

    @Autowired
    private ReportDao reportDao;

    @Before
    public void before() {
        QueryContextFactoryImpl contextFactory = new QueryContextFactoryImpl(statisticsDao, searchDao, reportDao);
        queryProcessor = new QueryProcessorImpl(JsonQueryParser.newWeakJsonQueryParser(), new QueryBuilderImpl(), contextFactory);
    }

    @Configuration
    static class TestConfiguration {

        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

    }
}
