package com.aes.erp.organogram_system.dto;

import lombok.ToString;

@ToString(callSuper = true)
public class FSReturnObject extends BaseReturnObject {

    public FSReturnObject setReturnObject(boolean success, String message, Object data) {
        super.setSuccess(success);
        super.setMessage(message);
        super.setData(data);
        return this;
    }
}