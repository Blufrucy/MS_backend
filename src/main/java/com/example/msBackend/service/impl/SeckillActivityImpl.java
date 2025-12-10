package com.example.msBackend.service.impl;

import com.example.msBackend.mapper.SeckillActivityMapper;
import com.example.msBackend.service.SeckillActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeckillActivityImpl implements SeckillActivityService {
    @Autowired
    private SeckillActivityMapper seckillActivityMapper;
}
