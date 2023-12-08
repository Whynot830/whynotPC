package com.example.whynotpc.utils;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.function.Supplier;

public class JPACallHandler {
    public static <T> Result<T> handleCall(Supplier<T> jpaCall) {
        try {
            T result = jpaCall.get();
            return new Result<>(200, result);
        } catch (DataIntegrityViolationException e) {
            var msg = e.getMessage();
            if (msg.contains("unique"))
                return new Result<>(409);
            return new Result<>(400);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new Result<>(400);
        } catch (ExpiredJwtException e) {
            return new Result<>(403);
        } catch (NoAuthenticationException e) {
            return new Result<>(401);
        } catch (EntityNotFoundException e) {
            return new Result<>(404);
        }
        catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return new Result<>(500);
        }
    }

    public record Result<T>(int statusCode, T result) {
        public Result(int statusCode) {
            this(statusCode, null);
        }
    }
}
