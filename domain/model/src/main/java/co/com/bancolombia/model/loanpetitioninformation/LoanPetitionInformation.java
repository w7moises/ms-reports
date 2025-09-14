package co.com.bancolombia.model.loanpetitioninformation;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanPetitionInformation {
    private Long id;
    private BigDecimal amount;
    private Integer term;
    private String email;
    private String documentNumber;
    private String state;
    private BigDecimal interestRate;
    private BigDecimal salary;
    private boolean automaticValidation;
}
