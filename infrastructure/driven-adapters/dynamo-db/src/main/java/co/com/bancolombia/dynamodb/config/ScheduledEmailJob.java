package co.com.bancolombia.dynamodb.config;

import co.com.bancolombia.dynamodb.SesEmailService;
import co.com.bancolombia.dynamodb.dto.EmailRequest;
import co.com.bancolombia.model.loanpetitioninformation.LoanPetitionInformation;
import co.com.bancolombia.model.loanpetitioninformation.gateways.LoanPetitionInformationRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Component
public class ScheduledEmailJob {
    private final SesEmailService service;
    private final LoanPetitionInformationRepository repository;
    private final UserRepository userRepository;
    private final ResourceLoader resourceLoader;
    private final List<String> recipients;
    private final String htmlLocation;
    private final String subjectBase;

    public ScheduledEmailJob(
            SesEmailService service, LoanPetitionInformationRepository repository, UserRepository userRepository,
            ResourceLoader resourceLoader,
            @Value("${app.mail.recipients}") String recipientsCsv,
            @Value("${app.mail.html}") String htmlLocation,
            @Value("${app.mail.subject:Reporte diario}") String subjectBase) {
        this.service = service;
        this.repository = repository;
        this.userRepository = userRepository;
        this.resourceLoader = resourceLoader;
        this.recipients = Arrays.stream(recipientsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        this.htmlLocation = htmlLocation;
        this.subjectBase = subjectBase;
    }

    @Scheduled(cron = "${app.mail.cron:0 30 7 * * *}", zone = "America/Lima")
    public void sendDailyReport() {
        Mono<String> htmlMono = Mono.fromCallable(this::loadHtml)
                .subscribeOn(Schedulers.boundedElastic());

        Mono<Integer> totalApprovedParameter = repository.countPetitionsApproved();
        Mono<BigDecimal> totalAmountParameter = repository.totalAmountLoanPetitionsApproved();
        Flux<LoanPetitionInformation> items = repository.findAll().cache();

        Mono<Map<String, User>> userDataUnique = items
                .map(LoanPetitionInformation::getDocumentNumber)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .flatMap(document ->
                        userRepository.findUserByDocumentNumber(document)
                                .map(user -> Tuples.of(document, user))
                )
                .collectMap(Tuple2::getT1, Tuple2::getT2);

        Mono<String> rows = Mono.zip(items.collectList(), userDataUnique)
                .map(tuple -> {
                    List<LoanPetitionInformation> list = tuple.getT1();
                    Map<String, User> usersByDoc = tuple.getT2();
                    StringBuilder stringBuilder = new StringBuilder();
                    for (LoanPetitionInformation data : list) {
                        User userData = usersByDoc.getOrDefault(data.getDocumentNumber(), null);
                        stringBuilder.append(toRowHtml(data, userData));
                    }
                    return stringBuilder.toString();
                });

        Mono.zip(htmlMono, totalApprovedParameter, totalAmountParameter, rows)
                .flatMap(tuple -> {
                    String template = tuple.getT1();
                    Integer totalApproved = tuple.getT2();
                    BigDecimal totalAmount = tuple.getT3();
                    String rowsData = tuple.getT4();

                    Map<String, String> params = new HashMap<>();
                    params.put("title", "Reporte diario");
                    params.put("date", LocalDate.now(ZoneId.of("America/Lima")).toString());
                    params.put("recipientName", "Equipo Comercial");
                    params.put("badge", "Producción");
                    params.put("introText", "Resumen del día para solicitudes de préstamo.");
                    params.put("totalApproved", String.valueOf(totalApproved));
                    params.put("totalAmount", String.valueOf(totalAmount));
                    params.put("rows", rowsData);
                    params.put("supportEmail", "soporte@gmail.com");
                    String html = render(template, params);
                    return service.send(new EmailRequest(recipients.get(0), subjectWithDate(), html), true);
                })
                .doOnError(e -> System.err.println("[MAIL JOB] Error enviando correo: " + e.getMessage()))
                .subscribe();
    }

    private String subjectWithDate() {
        return subjectBase + " " + LocalDate.now(ZoneId.of("America/Lima"));
    }

    private String loadHtml() throws Exception {
        Resource res = resourceLoader.getResource(htmlLocation);
        try (InputStream is = res.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String render(String template, Map<String, String> params) {
        String out = template;
        for (Map.Entry<String, String> e : params.entrySet()) {
            String key = e.getKey();
            String val = Optional.ofNullable(e.getValue()).orElse("");
            out = out.replace("{{{" + key + "}}}", val);
            out = out.replace("{{" + key + "}}", val);
        }
        return out;
    }

    private String toRowHtml(LoanPetitionInformation data, User userData) {
        String id = data.getId().toString();
        String documentNumber = data.getDocumentNumber();
        String email = data.getEmail();
        String amount = data.getAmount().toString();
        String fullName = userData.getName() + " " + userData.getLastName();
        String address = userData.getAddress();

        return """
                <tr>
                  <td style="text-align:left;">%s</td>
                  <td style="text-align:left;">%s</td>
                  <td style="text-align:left;">%s</td>
                  <td style="text-align:left;">%s</td>
                  <td style="text-align:left;">%s</td>
                  <td style="text-align:left;">%s</td>
                </tr>
                """.formatted(id, documentNumber, email, amount, fullName, address);
    }
}
