package co.com.bancolombia.dynamodb;

import co.com.bancolombia.dynamodb.entity.LoanPetitionInformationEntity;
import co.com.bancolombia.dynamodb.helper.TemplateAdapterOperations;
import co.com.bancolombia.model.loanpetitioninformation.LoanPetitionInformation;
import co.com.bancolombia.model.loanpetitioninformation.gateways.LoanPetitionInformationRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.math.BigDecimal;
import java.util.List;


@Repository
public class LoanPetitionDynamoTemplateAdapter extends TemplateAdapterOperations<
        LoanPetitionInformation,
        String,
        LoanPetitionInformationEntity>
        implements LoanPetitionInformationRepository {

    public LoanPetitionDynamoTemplateAdapter(DynamoDbEnhancedAsyncClient connectionFactory, ObjectMapper mapper) {
        super(connectionFactory, mapper, d -> mapper.map(d, LoanPetitionInformation.class),
                "reports");
    }

    private QueryEnhancedRequest generateQueryExpression(String partitionKey, String sortKey) {
        return QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue(partitionKey).build()))
                .queryConditional(QueryConditional.sortGreaterThanOrEqualTo(Key.builder().sortValue(sortKey).build()))
                .build();
    }

    @Override
    public Mono<LoanPetitionInformation> save(LoanPetitionInformation loanPetitionInformation) {
        return super.save(loanPetitionInformation);
    }

    @Override
    public Flux<LoanPetitionInformation> findAll() {
        return super.scan()
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<Integer> countPetitionsApproved() {
        return super.scan().map(List::size);
    }

    @Override
    public Mono<BigDecimal> totalAmountLoanPetitionsApproved() {
        return super.scan()
                .map(data -> data.stream()
                        .map(LoanPetitionInformation::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                );
    }
}
