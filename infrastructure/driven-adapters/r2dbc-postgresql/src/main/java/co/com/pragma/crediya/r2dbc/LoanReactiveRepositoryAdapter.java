package co.com.pragma.crediya.r2dbc;

import co.com.pragma.crediya.model.loanapplication.LoanApplication;
import co.com.pragma.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.crediya.r2dbc.entity.LoanApplicatonEntity;
import co.com.pragma.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class LoanReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanApplication,
        LoanApplicatonEntity,
        UUID,
        LoanReactiveRepository
        > implements LoanApplicationRepository {
    public LoanReactiveRepositoryAdapter(LoanReactiveRepository repository,
                                         ObjectMapper mapper,
                                         TransactionalOperator transactionalOperator) {
        super(repository, mapper, d -> mapper.map(d, LoanApplication.class));

        this.transactionalOperator = transactionalOperator;
    }

    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<LoanApplication> saveLoanApplication(LoanApplication loanApplication) {

        Mono<LoanApplication> flow = super.save(loanApplication);

        return transactionalOperator.transactional(flow);
    }
}


