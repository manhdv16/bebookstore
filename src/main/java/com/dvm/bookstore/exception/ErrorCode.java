package com.dvm.bookstore.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(500, "Uncategorized exception"),
    CATEGORY_NAME_EXISTS(400, "Category name already exists"),
    CATEGORY_NOT_FOUND(400, "Not found category"),
    CATEGORY_NAME_INVALID(400, "Category name must be not blank"),
    DESCRIPTION_INVALID(400, "Description must be not blank"),
    BOOK_NOT_FOUND(400,"Not found book"),
    PASSWORD_NOT_MATCH(400,"Password not match"),
    USER_NOT_FOUND(400,"Not found user"),
    URL_NOT_FOUND(400,"Error uploading file to cloudinary");


    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
