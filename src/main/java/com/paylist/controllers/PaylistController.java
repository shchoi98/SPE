package com.paylist.controllers;

import com.paylist.models.Invoice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
public class PaylistController {

    @Autowired // injects an instance of this interface
    private PaylistRestController paylistRestController;

    private void getNewInvoices() {
        List<String> newEmails = paylistRestController.getListOfNewEmails().getBody();
        for (String email : newEmails) {
            if (email != "AMAZON_SES_SETUP_NOTIFICATION") {
                paylistRestController.decodeEmailFile(email);
                paylistRestController.copyEmailFile(email);
                paylistRestController.deleteEmailFile(email);
            }
        }
    }

    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public String viewInvoices(Model model) {
        List<Invoice> invoices = paylistRestController.getAllInvoices();// getting the invoices
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> getNewInvoices());
        while (!future.isDone())
            ;
        model.addAttribute("invoices", invoices); // adding the list of invoices to the view
        model.addAttribute("title", "View - "); // adding the title to the page
        return "view";
    }

    // Receives the updated statuses
    @RequestMapping(value = "/view", method = RequestMethod.POST) // needs to know which user is looking at it
    public String updateInvoice(HttpServletRequest request, Model model) {
        paylistRestController.changeStatus(request.getParameter("uid"), request.getParameter("status"));
        if (Integer.parseInt(request.getParameter("status")) == 0) {
            paylistRestController.paidEmail(request.getParameter("uid"));
        }
        List<Invoice> invoices = paylistRestController.getAllInvoices();

        model.addAttribute("invoices", invoices); // adding the list of invoices to the view
        model.addAttribute("title", "View - "); // adding the title to the page
        return "view";
    }

    @RequestMapping(value = "/file", method = RequestMethod.GET)
    public String returnInvoice(HttpServletRequest request, Model model) {
        String fileName = request.getParameter("file");

        CompletableFuture<File> future = CompletableFuture
                .supplyAsync(() -> paylistRestController.getInvoiceFileFromS3(fileName).getBody());
        while (!future.isDone())
            ;

        String filepath;
        try {
            File localfile = future.get();
            while (!localfile.exists())
                ;
            Thread.sleep(1000);
            filepath = localfile.getName();
        } catch (Exception e) {
            filepath = fileName;
        }

        model.addAttribute("title", filepath + " - ");
        model.addAttribute("file", filepath);
        return "file";
    }
}
