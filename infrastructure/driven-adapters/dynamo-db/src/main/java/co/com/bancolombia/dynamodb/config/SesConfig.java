package co.com.bancolombia.dynamodb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2AsyncClient;

@Configuration
public class SesConfig {
    @Bean
    SesV2AsyncClient sesV2AsyncClient() {
        return SesV2AsyncClient.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
