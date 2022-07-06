package com.t2010a.baovemuaxuan.restapi;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1/admins")
public class AdminApi {

    @RequestMapping(method = RequestMethod.GET)
    public String get() {
        return "Hello admin";
    }
}
