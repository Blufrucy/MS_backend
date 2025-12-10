package com.example.msBackend.controller;

import com.example.msBackend.service.SeckillActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("SeckillActivityController")
public class SeckillActivityController {
    @Autowired
    private SeckillActivityService seckillActivityService;
}
