package co.com.bancolombia.dynamodb;

import co.com.bancolombia.dynamodb.dto.EmailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sesv2.SesV2AsyncClient;
import software.amazon.awssdk.services.sesv2.model.*;

import javax.swing.text.AbstractDocument;

@Service
public class SesEmailService {

    private final SesV2AsyncClient ses;
    private final String from;

    public SesEmailService(SesV2AsyncClient ses,
                           @Value("${mail.from}") String from) {
        this.ses = ses;
        this.from = from;
    }

    public Mono<Void> send(EmailRequest req, boolean html) {
        AbstractDocument.Content subject = Content.builder().data(req.subject()).build();
        Body body = html
                ? Body.builder().html(Content.builder().data(req.text()).build()).build()
                : Body.builder().text(Content.builder().data(req.text()).build()).build();

        EmailContent content = EmailContent.builder()
                .simple(Message.builder().subject(subject).body(body).build())
                .build();

        SendEmailRequest send = SendEmailRequest.builder()
                .fromEmailAddress(from)
                .destination(Destination.builder().toAddresses(req.to()).build())
                .content(content)
                .build();

        return Mono.fromFuture(ses.sendEmail(send)).then();
    }
}