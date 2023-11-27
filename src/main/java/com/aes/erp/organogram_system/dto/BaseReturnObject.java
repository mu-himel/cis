package com.aes.erp.organogram_system.dto;

import com.aes.erp.organogram_system.APIResponseStatus;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BaseReturnObject {

    private boolean success;
    private String message;
    private Object data;

    public BaseReturnObject get(boolean success, String message, Object data) {
        this.setSuccess(success);
        this.setMessage(message);
        this.setData(data);
        return this;
    }

    public BaseReturnObject getFailedReturnObject(String message) {
        return new BaseReturnObject().get(
                false,
                message,
                null
        );
    }

    public BaseReturnObject getSuccessReturnObject(String message) {
        return new BaseReturnObject().get(
                true,
                message,
                null
        );
    }

    public APIResponse convertToAPIResponse() {
        return new APIResponse(
                message,
                success,
                data,
                this.success
                        ? APIResponseStatus.SUCCESS
                        : APIResponseStatus.ERROR
        );
    }
}
