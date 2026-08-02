package com.intelliflow.modules.document.storage.exception;

import com.intelliflow.common.exception.BaseException;
import com.intelliflow.common.exception.ErrorCode;

public class StorageFileNotFoundException extends BaseException {

    public StorageFileNotFoundException(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND, message);
    }

    public StorageFileNotFoundException(String message, Throwable cause) {
        super(ErrorCode.RESOURCE_NOT_FOUND, message, cause);
    }
}
