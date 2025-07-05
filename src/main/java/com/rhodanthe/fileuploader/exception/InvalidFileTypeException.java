package com.rhodanthe.fileuploader.exception;

public class InvalidFileTypeException extends FileUploadException {
    public InvalidFileTypeException(String message) {
        super(message);
    }
}
