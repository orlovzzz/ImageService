package org.example.config;

import com.example.grpc.AuthServiceGrpc;
import com.example.grpc.ImageServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${service.security}")
    private String securityService;

    @Value("${service.images}")
    private String imageService;

    @Bean
    public AuthServiceGrpc.AuthServiceBlockingStub getSecurityStub() {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(securityService).usePlaintext().build();
        AuthServiceGrpc.AuthServiceBlockingStub stub = AuthServiceGrpc.newBlockingStub(channel);
        return stub;
    }

    @Bean
    public ImageServiceGrpc.ImageServiceBlockingStub getImagesStub() {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(imageService).usePlaintext().build();
        ImageServiceGrpc.ImageServiceBlockingStub stub = ImageServiceGrpc.newBlockingStub(channel);
        return stub;
    }

}
