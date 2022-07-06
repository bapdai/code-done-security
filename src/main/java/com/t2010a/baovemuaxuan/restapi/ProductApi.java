package com.t2010a.baovemuaxuan.restapi;

import com.t2010a.baovemuaxuan.entity.Product;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1/products")
public class ProductApi {

    @RequestMapping(method = RequestMethod.GET)
    public Product get(){
        return new Product(1, "Hello world");
    }
}
