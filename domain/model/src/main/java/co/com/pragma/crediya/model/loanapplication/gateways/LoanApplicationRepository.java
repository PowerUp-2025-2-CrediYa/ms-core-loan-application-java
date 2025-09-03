package co.com.pragma.crediya.model.loanapplication.gateways;

import co.com.pragma.crediya.model.loanapplication.LoanApplication;
import reactor.core.publisher.Mono;

public interface LoanApplicationRepository {

    Mono<LoanApplication> saveLoanApplication(LoanApplication loanApplication);
}
