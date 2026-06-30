package com.example.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
public class BlobStorageService {

    private final BlobContainerClient containerClient;

    public BlobStorageService(
            @Value("${azure.storage.connection-string}") String connectionString,
            @Value("${azure.storage.container-name}") String containerName) {
        this.containerClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient()
                .getBlobContainerClient(containerName);
    }

    public String subir(MultipartFile archivo) throws IOException {
        String ext = StringUtils.getFilenameExtension(
                Objects.requireNonNull(archivo.getOriginalFilename(), "nombre de archivo requerido"));
        String nombreBlob = UUID.randomUUID() + (ext != null ? "." + ext : "");

        BlobClient blobClient = containerClient.getBlobClient(nombreBlob);
        blobClient.upload(archivo.getInputStream(), archivo.getSize(), true);
        blobClient.setHttpHeaders(new BlobHttpHeaders().setContentType(archivo.getContentType()));

        return blobClient.getBlobUrl();
    }

    public void eliminar(String blobUrl) {
        String nombreBlob = blobUrl.substring(blobUrl.lastIndexOf('/') + 1);
        containerClient.getBlobClient(nombreBlob).deleteIfExists();
    }
}
