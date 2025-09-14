package co.com.bancolombia.model.loanpetitioninformation.gateways;

import co.com.bancolombia.model.loanpetitioninformation.LoanPetitionInformation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface LoanPetitionInformationRepository {
    Mono<LoanPetitionInformation> save(LoanPetitionInformation loanPetitionInformation);

    Flux<LoanPetitionInformation> findAll();

    Mono<Integer> countPetitionsApproved();

    Mono<BigDecimal> totalAmountLoanPetitionsApproved();
}
