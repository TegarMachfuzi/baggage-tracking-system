package com.baggage.util;

import com.baggage.constant.RespCodeConstant;
import com.baggage.constant.RespMsgConstant;
import com.baggage.dto.response.ResponseModel;

public class ResponseUtil {

    public static ResponseModel success(Object data) {
        ResponseModel response = new ResponseModel();
        response.setResponseCode(RespCodeConstant.RC_00);
        response.setResponseMessage(RespMsgConstant.SUCCESS);
        response.setData(data);
        return response;
    }

    public static ResponseModel success() {
        return success(null);
    }

    public static ResponseModel error(String code, String message) {
        ResponseModel response = new ResponseModel();
        response.setResponseCode(code);
        response.setResponseMessage(message);
        response.setData(null);
        return response;
    }

    public static ResponseModel error(String message) {
        return error(RespCodeConstant.RC_96, message);
    }

    public static ResponseModel unauthorized() {
        return error(RespCodeConstant.RC_401, "Unauthorized");
    }
}
