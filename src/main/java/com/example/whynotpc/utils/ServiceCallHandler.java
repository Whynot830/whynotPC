package com.example.whynotpc.utils;

import com.example.whynotpc.models.response.AuthResponse;
import com.example.whynotpc.models.response.BasicResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Supplier;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * Utility class for handling service call responses.
 */
public class ServiceCallHandler {
    /**
     * Retrieves the response from a supplier and generates an appropriate ResponseEntity based on the response.
     *
     * @param supplier the supplier providing the response
     * @param <T>      the type of the response, extending BasicResponse
     * @return ResponseEntity representing the response
     */
    public static <T extends BasicResponse> ResponseEntity<T> getResponse(Supplier<T> supplier) {
        var response = supplier.get();
        var status = HttpStatus.resolve(response.getStatusCode());

        if (status == null)
            return ResponseEntity.internalServerError().build();

        if ((status == OK || status == CREATED) && !(response instanceof AuthResponse))
            return ResponseEntity.status(status).body(response);

        return ResponseEntity.status(status).build();
    }
}