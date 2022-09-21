package com.paylist.S3;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.paylist.S3.S3Functions;

@Component
public class S3FunctionsImpl implements S3Functions {

    @Autowired
    private AmazonS3 s3client;

    @Value("${jsa.s3.bucket}")
    private String bucketName;

    private String processS3Object(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        StringBuffer sb = new StringBuffer();
        String str;
        while ((str = reader.readLine()) != null) {
            sb.append(str);
        }

        reader.close();

        return sb.toString();
    }

    @Override
    public String download(String fileName) {
        try {
            S3Object s3object = s3client.getObject(new GetObjectRequest(bucketName, fileName));
            System.out.println("Content-Type: " + s3object.getObjectMetadata().getContentType());
            return processS3Object(s3object.getObjectContent());
        } catch (IOException e) {
            return "File Does Not Exist";
        }
    }

    @Override
    public File downloadFile(String fileName) {
        File localFile = new File("src/main/resources/static/" + fileName);
        if (!localFile.exists()) {
            s3client.getObject(new GetObjectRequest(bucketName, fileName), localFile);
        }
        return localFile;
    }

    @Override
    public void uploadFile(String fileName) {
        File file = new File("src/main/resources/static/invoices/" + fileName);
        s3client.putObject(new PutObjectRequest(bucketName, "invoices/" + fileName, file));
    }

    @Override
    public void deleteFile(String fileName) {
        s3client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
    }

    @Override
    public void copyFile(String fileName, String destination) {
        s3client.copyObject(new CopyObjectRequest(bucketName, fileName, bucketName, destination));
    }

    @Override
    public List<String> listFiles(String directory) {
        ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withPrefix(directory)
                .withDelimiter("/");
        ListObjectsV2Result result = s3client.listObjectsV2(req);
        List<String> listOfNewEmails = new ArrayList<>();
        for (S3ObjectSummary summary : result.getObjectSummaries()) {
            if (!summary.getKey().equals("emails/")) {
                listOfNewEmails.add(summary.getKey().replaceAll("emails/", ""));
            }
        }
        return listOfNewEmails;
    }

}