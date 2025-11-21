package com.romy.platform.config;


import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AzureBlobConfig {

    private final String endPoint = "https://romystorage.blob.core.windows.net/";
    private final String tenantId = "test1";
    private final String clientId = "test1";
    private final String secret = "test1";




    @Bean
    public BlobServiceClient blobServiceClient() {

        ClientSecretCredential credential = new ClientSecretCredentialBuilder()
                .tenantId(this.tenantId)
                .clientId(this.clientId)
                .clientSecret(this.secret).build();

        return new BlobServiceClientBuilder().endpoint(this.endPoint).credential(credential).buildClient();
    }

    @Bean
    public BlobServiceAsyncClient blobServiceAsyncClient() {

        ClientSecretCredential credential = new ClientSecretCredentialBuilder()
                .tenantId(this.tenantId)
                .clientId(this.clientId)
                .clientSecret(this.secret).build();

        return new BlobServiceClientBuilder().endpoint(this.endPoint).credential(credential).buildAsyncClient();
    }


}
