package com.paylist.controllers;

import com.paylist.models.Invoice;
import com.paylist.models.Status;
import com.paylist.S3.S3Functions;
import com.paylist.decoding.IO;
import com.paylist.email.EmailService;
import com.paylist.models.repositories.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Date;
import java.util.List;

@RestController
@ComponentScan({ "com.paylist.email", "com.paylist.S3", "com.paylist.decoding" })
public class PaylistRestController {

    @Autowired // injects an instance of this interface
    private InvoiceRepository invoiceRepository;

    @Autowired
    S3Functions s3Functions;

    @Autowired
    IO io;

    @Autowired
    EmailService emailService;

    @GetMapping("/api/invoices")
    public List<Invoice> getAllInvoices() { // needs to change so that it retrieves them based on user id
        return invoiceRepository.findAll();
    }

    @GetMapping("/api/invoices/id")
    public ResponseEntity<Invoice> getInvoiceById(@RequestParam("id") String id) {
        int invoiceId = Integer.parseInt(id);
        Invoice invoice = invoiceRepository.findByUid(invoiceId);
        return new ResponseEntity<Invoice>(invoice, HttpStatus.OK);
    }

    @PostMapping("/api/invoices")
    public ResponseEntity<Invoice> changeStatus(@RequestParam("id") String uid,
            @RequestParam("status") String newStatus) {
        int invoiceId = Integer.parseInt(uid);
        Invoice invoice = invoiceRepository.findByUid(invoiceId);
        Status status = Status.values()[Integer.parseInt(newStatus)];
        invoice.setStatus(status);

        invoiceRepository.save(invoice);
        return new ResponseEntity<Invoice>(invoice, HttpStatus.OK);
    }

    @PutMapping("/api/create")
    public void saveInvoice(@RequestParam("filename") String filename, @RequestParam("sender") String sender,
            @RequestParam("email") String email, @RequestParam("dateReceived") Date dateReceived,
            @RequestParam("status") String status) {
        Invoice invoice = new Invoice();
        invoice.setFilename(filename);
        invoice.setSender(sender);
        invoice.setEmail(email);
        invoice.setDateReceived(dateReceived);
        Status statusNew = Status.values()[Integer.parseInt(status)];
        invoice.setStatus(statusNew);

        invoiceRepository.save(invoice);

    }

    @DeleteMapping("/api/delete")
    public void deleteInvoice(@RequestParam("id") int id) {

        invoiceRepository.deleteById(id);
    }

    @GetMapping("api/email/string")
    public ResponseEntity<String> getEmailStringFromS3(@RequestParam("file") String fileName) {
        String fileString = s3Functions.download("emails/" + fileName);// downloads the file, returns as string
        return new ResponseEntity<String>(fileString, HttpStatus.OK);// return the string
    }

    @GetMapping("api/email/list")
    public ResponseEntity<List<String>> getListOfNewEmails() {
        List<String> newEmails = s3Functions.listFiles("emails/");
        return new ResponseEntity<List<String>>(newEmails, HttpStatus.OK);
    }

    @GetMapping("api/email/file")
    public ResponseEntity<String> getEmailFileFromS3(@RequestParam("file") String fileName) {
        s3Functions.downloadFile("emails/" + fileName);
        return new ResponseEntity<String>("Downloaded", HttpStatus.OK);
    }

    @DeleteMapping("api/email/file")
    public ResponseEntity<String> deleteEmailFile(@RequestParam("file") String fileName) {
        s3Functions.deleteFile("emails/" + fileName);
        return new ResponseEntity<String>("Deleted", HttpStatus.OK);
    }

    @PutMapping("api/email/file")
    public ResponseEntity<String> copyEmailFile(@RequestParam("file") String fileName) {
        s3Functions.copyFile("emails/" + fileName, "old-emails/" + fileName);
        return new ResponseEntity<String>("Copied", HttpStatus.OK);
    }

    @GetMapping("api/invoice/file")
    public ResponseEntity<File> getInvoiceFileFromS3(@RequestParam("file") String fileName) {
        File localFile = s3Functions.downloadFile("invoices/" + fileName);
        return new ResponseEntity<File>(localFile, HttpStatus.OK);
    }

    @GetMapping("api/invoice/upload")
    public ResponseEntity<String> uploadInvoiceFile(@RequestParam("file") String fileName) {
        s3Functions.uploadFile(fileName);// uploads the file with fileName
        return new ResponseEntity<String>("Ok", HttpStatus.OK);
    }

    @PostMapping("api/decode")
    public void decodeEmailFile(@RequestParam("file") String filename) {
        getEmailFileFromS3(filename);
        try {
            Thread.sleep(1000);
            Invoice newInvoice = io.decodeEmail(filename); // Decode the invoice and get the new Invoice
            invoiceRepository.save(newInvoice); // Save the invoice to the database

            String invoiceFile = newInvoice.getFilename(); // Get the filename of the invoice
            uploadInvoiceFile(invoiceFile); // Upload the invoice file to the S3 Bucket
        } catch (Exception e) {
            System.out.println("Error"); // Catching any errors
        }
    }

    @PostMapping("api/mail/paid")
    public void paidEmail(@RequestParam("id") String id) {
        Invoice invoice = this.getInvoiceById(id).getBody();
        try {
            emailService.sendEmail(invoice);
        } catch (MailException mailException) {
            System.out.println(mailException);
        }
    }
}
