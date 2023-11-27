package com.aes.erp.organogram_system.dto;

import com.aes.erp.organogram_system.APIResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.io.Serializable;
import java.util.*;

import static java.util.Objects.isNull;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class APIResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private APIResponseStatus status;

    private boolean success;

    private Object data;

    private String message;

    private List<String> errors;

    private Map<String, List<String>> fieldsErrors;

    private String pathVariableError;

    public APIResponse(APIResponseStatus status) {
        this.status = status;
    }

    public APIResponse(String message, boolean success, Object data, APIResponseStatus status ) {
        this.message = message;
        this.success = success;
        this.data = data;
        this.status = status;
    }

    public APIResponse addPathVariableError(String errorMessage) {
        this.pathVariableError = errorMessage;
        return this;
    }

    public APIResponse addErrorMessage(String msg) {
        if (isNull(errors)) {
            errors = new ArrayList<>();
        }
        errors.add(msg);
        return this;
    }

    public APIResponse addFieldError(String fieldName, String msg) {
        if (isNull(fieldsErrors)) {
            fieldsErrors = new HashMap<>();
        }

        List<String> errors = fieldsErrors.get(fieldName);
        if (isNull(errors)) {
            errors = new ArrayList<>();
            fieldsErrors.put(fieldName, errors);
        }
        errors.add(msg);
        return this;
    }
    public void clear() {
        if (Objects.nonNull(fieldsErrors))
            fieldsErrors.clear();
    }

    public static APIResponse success(Object data) {
        return new APIResponse(APIResponseStatus.SUCCESS).setData(data);
    }

    public static APIResponse error() {
        return new APIResponse(APIResponseStatus.ERROR);
    }

    public static APIResponse error(String msg) {
        return error().addErrorMessage(msg);
    }

    public static APIResponse prepareErrorResponse(BindingResult result) {
        APIResponse response = APIResponse.error();
        for (FieldError fe : result.getFieldErrors()) {
            response.addFieldError(fe.getField(), fe.getDefaultMessage());
        }
        return response;
    }

    public APIResponse setResponse(String message, boolean success, Object data, APIResponseStatus status) {
        this.message = message;
        this.success = success;
        this.data = data;
        this.status = status;
        return this;
    }

    public static APIResponse getApiResponse() {
        return new APIResponse();
    }

}
