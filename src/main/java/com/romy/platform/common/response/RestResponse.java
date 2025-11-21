package com.romy.platform.common.response;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RestResponse {

    protected String uri;

    protected RestResponse(String uri) {
        this.uri= uri;
    }
}
