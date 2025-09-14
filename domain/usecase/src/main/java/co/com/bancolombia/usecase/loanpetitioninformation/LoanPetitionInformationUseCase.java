package co.com.bancolombia.usecase.loanpetitioninformation;

import co.com.bancolombia.model.loanpetitioninformation.LoanPetitionInformation;
import co.com.bancolombia.model.loanpetitioninformation.gateways.LoanPetitionInformationRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class LoanPetitionInformationUseCase {

    private final LoanPetitionInformationRepository loanPetitionInformationRepository;

    public Mono<LoanPetitionInformation> save(LoanPetitionInformation loanPetitionInformation) {
        return loanPetitionInformationRepository.save(loanPetitionInformation);
    }

    public Flux<LoanPetitionInformation> findAll() {
        return loanPetitionInformationRepository.findAll();
    }

    public Mono<Integer> countPetitionsApproved() {
        return loanPetitionInformationRepository.countPetitionsApproved();
    }

    public Mono<BigDecimal> totalAmountLoanPetitionsApproved() {
        return loanPetitionInformationRepository.totalAmountLoanPetitionsApproved();
    }
}
