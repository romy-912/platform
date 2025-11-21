package com.romy.platform.common.response;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SuccessResponse<T> extends RestResponse {

    private T data;

    public SuccessResponse(String uri, T data) {
        super(uri);
        this.data = data;
    }

}
