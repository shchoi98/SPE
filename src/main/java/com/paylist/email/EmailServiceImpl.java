package com.paylist.email;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.paylist.models.Invoice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailServiceImpl implements EmailService {

    @Autowired
    private AmazonSimpleEmailService emailClient;

    @Value("${aws_ses_email}")
    private String fromEmail;

    @Override
    public void sendEmail(Invoice invoice) {
        try {
            SendEmailRequest request = new SendEmailRequest()
                    .withDestination(new Destination().withToAddresses(invoice.getEmail()))
                    .withMessage(new Message().withBody(new Body().withHtml(new Content().withCharset("UTF-8")
                            .withData("<h1>An Invoice has been paid </h1> <p>Dear " + invoice.getSender()
                                    + ", </p> <p> Your invoice " + invoice.getFilename() + " has been paid. </p>"
                                    + "<p>Sent By <a href=\"http://ec2-3-92-184-106.compute-1.amazonaws.com:8080/view?\"> Paylist</a></p>"))
                            .withText(new Content().withCharset("UTF-8").withData("Sent By Paylist"))).withSubject(
                                    new Content().withCharset("UTF-8").withData("An Invoice has been paid - Paylist")))
                    .withSource(fromEmail);
            emailClient.sendEmail(request);
            System.out.println("Email sent to: " + invoice.getEmail());
        } catch (Exception ex) {
            System.out.println("The email was not sent. Error message: " + ex.getMessage());
        }
    }
}