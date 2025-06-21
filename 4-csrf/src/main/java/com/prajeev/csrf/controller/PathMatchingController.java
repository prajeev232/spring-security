package com.prajeev.csrf.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class PathMatchingController {
    @GetMapping(path = "/public-data")
    public String getPublicData() {
        return "This is public data!";
    }

    @RequestMapping(method = RequestMethod.OPTIONS, path = "/public-data")
    public String optionsPublicData() {
        return "This is public data!";
    }

    @GetMapping(path = "/private-data")
    public String privateData() {
        return "This is private data!";
    }
}
