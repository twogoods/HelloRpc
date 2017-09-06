package com.tg.rpc.core.handler.response;

import com.tg.rpc.core.entity.Request;
import com.tg.rpc.core.entity.Response;

/**
 * Created by twogoods on 17/2/17.
 */
public interface ResponseHandler {
    Response handle(Request request);
}
