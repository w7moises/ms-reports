package co.com.bancolombia.dynamodb.entity;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.math.BigDecimal;

@DynamoDbBean
@Setter
public class LoanPetitionInformationEntity {

    private Long id;
    private String documentNumber;
    private BigDecimal amount;
    private Integer term;
    private String email;
    private String state;
    private BigDecimal interestRate;
    private BigDecimal salary;
    private Boolean automaticValidation;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("id")
    public Long getId() {
        return id;
    }

    @DynamoDbAttribute("documentNumber")
    public String getDocumentNumber() {
        return documentNumber;
    }

    @DynamoDbAttribute("amount")
    public BigDecimal getAmount() {
        return amount;
    }

    @DynamoDbAttribute("term")
    public Integer getTerm() {
        return term;
    }

    @DynamoDbAttribute("email")
    public String getEmail() {
        return email;
    }

    @DynamoDbAttribute("state")
    public String getState() {
        return state;
    }

    @DynamoDbAttribute("interestRate")
    public BigDecimal getInterestRate() {
        return interestRate;
    }

    @DynamoDbAttribute("salary")
    public BigDecimal getSalary() {
        return salary;
    }

    @DynamoDbAttribute("automaticValidation")
    public Boolean getAutomaticValidation() {
        return automaticValidation;
    }

}
