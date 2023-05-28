package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@AllArgsConstructor
@Data
public class ResponseResult  {
    int code;
    String msg;
    Object body;
}
