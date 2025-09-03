package co.com.pragma.crediya.r2dbc;

import co.com.pragma.crediya.r2dbc.entity.LoanApplicatonEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface LoanReactiveRepository extends ReactiveCrudRepository<LoanApplicatonEntity, UUID>,
        ReactiveQueryByExampleExecutor<LoanApplicatonEntity> {

}
