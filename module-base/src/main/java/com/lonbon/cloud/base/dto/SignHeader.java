package com.lonbon.cloud.base.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springdoc.core.annotations.ParameterObject;

@Data
@ParameterObject
@Schema(description = "参数签名Header")
public class SignHeader {
    private String nonce;
    private String signature;
    private String timestamp;

}
