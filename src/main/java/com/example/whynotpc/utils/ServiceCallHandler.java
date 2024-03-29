package com.example.whynotpc.utils;

import com.example.whynotpc.models.response.AuthResponse;
import com.example.whynotpc.models.response.BasicResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Supplier;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;


public class ServiceCallHandler {
    public record Result<T>(int statusCode, T result) {
        public Result(int statusCode) {
            this(statusCode, null);
        }
    }

//    public static <T> Result<T> handleCall(Supplier<T> supplier) {
//        try {
//            T result = supplier.get();
//            return new Result<>(200, result);
//        } catch (DataIntegrityViolationException e) {
//            var msg = e.getMessage();
//            if (msg.contains("unique"))
//                return new Result<>(409);
//            return new Result<>(400);
//        } catch (IllegalArgumentException | IllegalStateException e) {
//            return new Result<>(400);
//        } catch (ExpiredJwtException e) {
//            return new Result<>(403);
//        } catch (NoAuthenticationException e) {
//            return new Result<>(401);
//        } catch (EntityNotFoundException e) {
//            return new Result<>(404);
//        } catch (RuntimeException e) {
//            System.out.println(e.getMessage());
//            return new Result<>(500);
//        }
//    }


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