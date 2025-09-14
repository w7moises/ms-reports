package co.com.bancolombia.api;

import co.com.bancolombia.model.loanpetitioninformation.LoanPetitionInformation;
import co.com.bancolombia.usecase.loanpetitioninformation.LoanPetitionInformationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final LoanPetitionInformationUseCase loanPetitionInformationUseCase;

    public Mono<ServerResponse> saveLoanPetitionInformation(ServerRequest request) {
        return request.bodyToMono(LoanPetitionInformation.class)
                .flatMap(loanPetitionInformationUseCase::save)
                .flatMap(data -> ServerResponse.ok()
                        .body(loanPetitionInformationUseCase.save(data), LoanPetitionInformation.class));
    }

    public Mono<ServerResponse> getLoanPetitionInformation(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(loanPetitionInformationUseCase.findAll(), LoanPetitionInformation.class);
    }

    public Mono<ServerResponse> countLoanApproved(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(loanPetitionInformationUseCase.countPetitionsApproved(), Integer.class);
    }

    public Mono<ServerResponse> totalAmountApproved(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(loanPetitionInformationUseCase.totalAmountLoanPetitionsApproved(), Integer.class);
    }
}
