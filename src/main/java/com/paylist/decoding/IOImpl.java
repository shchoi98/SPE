package com.paylist.decoding;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Scanner;

import com.paylist.models.Invoice;
import com.paylist.models.Status;

import org.springframework.stereotype.Component;

import com.paylist.decoding.IO;

@Component
public class IOImpl implements IO {

    private String content;
    private String sender;
    private String email;
    private Date date;

    @Override
    public Invoice decodeEmail(String filename) throws IOException, ParseException {

        String inputPath = "src/main/resources/static/emails/" + filename;
        File file = new File(inputPath);

        // retrieve filename/sender/sender email/dateReceived/(status)
        Scanner scanMeta = new Scanner(file);
        while (scanMeta.hasNextLine()) {
            String text = scanMeta.nextLine();
            String[] arrOfStr = text.split(" ");

            if (text.matches(".*filename.*")) {
                String fileString = arrOfStr[arrOfStr.length - 1].replaceAll("[\"]", "");
                String fileContent = slice(fileString, '=');
                content = fileContent;

            } else if (text.matches("From.*")) {
                // second third element
                sender = arrOfStr[1] + " " + arrOfStr[2];
            } else if (text.matches("Date.*")) {
                // !
                String date1 = slice(text, ' ');
                Date inFormat = sTod(date1);
                date = inFormat;
                // String dateString = text;
                // date = new Date();
            } else if (text.matches("Return-Path:.*")) {
                email = arrOfStr[1].replaceAll("[<>]", "");
            }
        }
        scanMeta.close();

        Scanner sc = new Scanner(file);
        String patternString = ".*Attachment.*";
        String patternStringE = "--.*--";
        String encodedString = "";
        while (sc.hasNextLine()) {
            String text = sc.nextLine();
            if (text.matches(patternString)) { // starting point of the attached file
                while (sc.hasNextLine()) {
                    String next = sc.nextLine();
                    if (!next.matches(patternStringE)) { // end point
                        encodedString = encodedString + next;
                    }
                }
            }

        }

        // decode
        String outputPath = "src/main/resources/static/invoices/" + content;
        try (FileOutputStream fos = new FileOutputStream(outputPath);) {

            byte[] decoder = Base64.getDecoder().decode(encodedString);
            fos.write(decoder);

        } catch (Exception e) {
            e.printStackTrace();
        }

        sc.close();

        return new Invoice(content, sender, email, date, Status.PENDING);
    }

    // !
    public static String slice(String original, Character a) {
        char[] chars = original.toCharArray();
        int start = 0;
        int end = chars.length;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == a) {
                start = i + 1;
                break;
            }
        }
        return original.substring(start, end);
    }

    // !
    public static Date sTod(String dateString) throws ParseException {
        Date date1 = new SimpleDateFormat("E, dd MMM yyyy").parse(dateString);
        // make into the ideal form
        String dateFormat = new SimpleDateFormat("yyyy-MM-dd").format(date1);
        // parse that to the ideal form
        Date dateFinal = new SimpleDateFormat("yyyy-MM-dd").parse(dateFormat);
        return dateFinal;
    }
}
