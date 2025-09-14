package co.com.bancolombia.dynamodb.dto;

public record EmailRequest(
        String to,
        String subject,
        String text
) {
}
