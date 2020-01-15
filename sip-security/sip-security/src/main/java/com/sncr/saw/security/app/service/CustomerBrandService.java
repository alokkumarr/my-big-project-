package com.sncr.saw.security.app.service;

import com.sncr.saw.security.app.exception.FileNotFoundException;
import com.sncr.saw.security.app.exception.StorageException;
import com.sncr.saw.security.app.model.response.CustomerBrandResponse;
import com.sncr.saw.security.app.properties.NSSOProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * This service class to perform CRUD operation for branding logo.
 *
 * @author alok.kumarr
 * @since 3.5.0
 */
@Service
public class CustomerBrandService {

  @Autowired
  NSSOProperties properties;

  public CustomerBrandResponse storeFile(MultipartFile file) {
    CustomerBrandResponse brandResponse = new CustomerBrandResponse();
    try {

      String fileUploadLocation = properties.getStorageFileLocation();
      Path fileStorageLocation = Paths.get(fileUploadLocation).toAbsolutePath().normalize();

      // create directory if not exist
      File dir = new File(fileUploadLocation);
      if (!dir.exists()) {
        Files.createDirectories(fileStorageLocation);
      }

      // Normalize file name
      String fileName = StringUtils.cleanPath(file.getOriginalFilename());

      // Check if the file's name contains invalid characters
      if (fileName.contains("..")) {
        throw new StorageException("Sorry! Filename contains invalid path sequence " + fileName);
      }

      // Copy file to the target location (Replacing existing file with the same name)
      Path targetLocation = fileStorageLocation.resolve(fileName);
      Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

      brandResponse.setBrandLogoName(fileName);
      brandResponse.setBrandLogoUrl(targetLocation.toString());

      return brandResponse;
    } catch (IOException ex) {
      String errorMessage = String.format("Could not store file %s. Please try again! %s",
          brandResponse.getBrandLogoName(),
          ex.getMessage());
      throw new StorageException(errorMessage);
    }
  }


  public Resource loadFileAsResource(String fileName) {
    try {
      Path filePath = Paths.get(properties.getStorageFileLocation()).toAbsolutePath().resolve(fileName).normalize();
      Resource resource = new UrlResource(filePath.toUri());
      if (resource.exists()) {
        return resource;
      } else {
        throw new FileNotFoundException("File not found " + "");
      }
    } catch (MalformedURLException ex) {
      throw new FileNotFoundException("File not found " + "", ex);
    }
  }
}