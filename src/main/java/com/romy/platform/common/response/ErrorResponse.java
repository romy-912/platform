package com.romy.platform.common.response;

import com.romy.platform.common.exception.ErrorElement;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Getter
@Setter
public class ErrorResponse extends RestResponse {


    private List<ErrorElement> errors = new ArrayList<>();

    public ErrorResponse(String uri) {
        super(uri);
    }

    public ErrorResponse(String uri, ErrorElement... errors) {
        super(uri);
        Collections.addAll(this.errors, errors);
    }


}
