package com.example.whynotpc.utils;

import com.example.whynotpc.models.response.AuthResponse;
import com.example.whynotpc.models.response.BasicResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Supplier;

import static org.springframework.http.HttpStatus.*;


public class ResponseHandler {
    public static <T extends BasicResponse> ResponseEntity<T> handleServiceCall(Supplier<T> serviceCall) {
        var response = serviceCall.get();
        var status = HttpStatus.resolve(response.getStatusCode());

        if (status == null || status == INTERNAL_SERVER_ERROR)
            return ResponseEntity.internalServerError().build();

        if (status == OK || status == CREATED || response instanceof AuthResponse)
            return ResponseEntity.status(status).body(response);

        return ResponseEntity.status(status).build();
    }
}