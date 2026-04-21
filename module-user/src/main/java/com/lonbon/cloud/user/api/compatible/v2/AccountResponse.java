package com.lonbon.cloud.user.api.compatible.v2;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AccountResponse {

    private AccountVO account;
}
