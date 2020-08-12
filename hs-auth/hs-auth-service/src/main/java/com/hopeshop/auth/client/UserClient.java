package com.hopeshop.auth.client;

import com.hopeshop.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * Created by silence on 2020/1/19.
 */
@FeignClient(value = "user-service")
public interface UserClient extends UserApi {
}
