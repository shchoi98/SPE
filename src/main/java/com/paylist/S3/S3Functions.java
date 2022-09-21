package com.paylist.S3;

import java.io.File;
import java.util.List;

public interface S3Functions {
  public String download(String fileName);

  public File downloadFile(String fileName);

  public void uploadFile(String fileName);

  public void deleteFile(String fileName);

  public void copyFile(String fileName, String destination);

  public List<String> listFiles(String directory);
}