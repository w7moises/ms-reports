package co.com.bancolombia.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return RouterFunctions
                .route()
                .path("/api/v1/reports", builder -> builder
                        .GET("/approved", handler::countLoanApproved)
                        .GET("/total/amount", handler::totalAmountApproved)
                        .POST("", handler::saveLoanPetitionInformation)
                        .GET("", handler::getLoanPetitionInformation))
                .build();
    }
}
