package org.example.service;

import com.example.grpc.AuthServiceGrpc;
import com.example.grpc.AuthServiceOuterClass;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.entity.Users;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class AuthServiceImpl extends AuthServiceGrpc.AuthServiceImplBase {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void registration(AuthServiceOuterClass.Request request, StreamObserver<AuthServiceOuterClass.Response> responseObserver) {
        Users user = userRepository.findByUsername(request.getUsername());
        if (user != null) {
            AuthServiceOuterClass.Response response = AuthServiceOuterClass.Response.newBuilder()
                    .setSuccess(false).setResponse("User with username " + user.getUsername() + " already exists").build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }
        user = new Users();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        userRepository.save(user);
        AuthServiceOuterClass.Response response = AuthServiceOuterClass.Response.newBuilder()
                .setSuccess(true).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void login(AuthServiceOuterClass.Request request, StreamObserver<AuthServiceOuterClass.Response> responseObserver) {
        Users user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            AuthServiceOuterClass.Response response = AuthServiceOuterClass.Response.newBuilder()
                    .setSuccess(false).setResponse("User with username " + request.getUsername() + " not found").build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }
        System.out.println(user.getPassword());
        System.out.println(request.getPassword());
        if (!user.getPassword().equals(request.getPassword())) {
            AuthServiceOuterClass.Response response = AuthServiceOuterClass.Response.newBuilder()
                    .setSuccess(false).setResponse("Password incorrect").build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }
        AuthServiceOuterClass.Response response = AuthServiceOuterClass.Response.newBuilder()
                .setSuccess(true).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getUser(AuthServiceOuterClass.Request request, StreamObserver<AuthServiceOuterClass.User> responseObserver) {
        Users u = userRepository.findByUsername(request.getUsername());
        if (u == null) {
            AuthServiceOuterClass.User user = AuthServiceOuterClass.User.newBuilder()
                    .setIsSearch(false).build();
            responseObserver.onNext(user);
            responseObserver.onCompleted();
            return;
        }
        AuthServiceOuterClass.User user = AuthServiceOuterClass.User.newBuilder()
                .setIsSearch(true).setUsername(u.getUsername()).setUserId(u.getId()).build();
        responseObserver.onNext(user);
        responseObserver.onCompleted();
    }
}
