package com.josephus.e_commerce_backend_app.common.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoValidFilesException extends ResponseStatusException {
    public NoValidFilesException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }
}