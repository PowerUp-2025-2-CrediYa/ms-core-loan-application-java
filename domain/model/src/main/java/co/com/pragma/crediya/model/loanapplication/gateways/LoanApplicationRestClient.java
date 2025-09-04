package co.com.pragma.crediya.model.loanapplication.gateways;

import co.com.pragma.crediya.model.loanapplication.User;
import reactor.core.publisher.Mono;

public interface LoanApplicationRestClient {

    Mono<User> findUserByDocumentId(String documentId);
}
