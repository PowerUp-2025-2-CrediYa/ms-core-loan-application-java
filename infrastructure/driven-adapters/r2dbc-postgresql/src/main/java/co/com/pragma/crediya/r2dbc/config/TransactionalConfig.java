package co.com.pragma.crediya.r2dbc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

@Configuration
public class TransactionalConfig {

    @Bean
    ReactiveTransactionManager r2dbcTransactionalManager(io.r2dbc.spi.ConnectionFactory cf) {
        return new org.springframework.r2dbc.connection.R2dbcTransactionManager(cf);
    }

    @Bean
    TransactionalOperator transactionalOperator(ReactiveTransactionManager rtm) {
        return TransactionalOperator.create(rtm);
    }
}
