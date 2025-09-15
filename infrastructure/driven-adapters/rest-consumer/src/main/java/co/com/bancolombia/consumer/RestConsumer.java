package co.com.bancolombia.consumer;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RestConsumer implements UserRepository {
    private final WebClient client;

    @CircuitBreaker(name = "findUserByDocumentNumber" /*, fallbackMethod = "testGetOk"*/)
    @Override
    public Mono<User> findUserByDocumentNumber(String documentNumber) {
        return client
                .get()
                .uri("/api/v1/users/document/{documentNumber}", documentNumber)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            String errorMessage = extractErrorMessage(errorBody);
                            return Mono.error(new RuntimeException(errorMessage));
                        }))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new RuntimeException("Server error fetching user: " + documentNumber))
                ).bodyToMono(User.class);
    }

    private String extractErrorMessage(String errorBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(errorBody);
            return rootNode.path("message").asText();
        } catch (Exception e) {
            return "Error fetching user: " + errorBody;
        }
    }
}
