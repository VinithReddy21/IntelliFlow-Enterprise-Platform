package com.intelliflow.modules.document.storage.exception;

import com.intelliflow.common.exception.BaseException;
import com.intelliflow.common.exception.ErrorCode;

public class StorageException extends BaseException {

    public StorageException(String message) {
        super(ErrorCode.INTERNAL_ERROR, message);
    }

    public StorageException(String message, Throwable cause) {
        super(ErrorCode.INTERNAL_ERROR, message, cause);
    }
}
