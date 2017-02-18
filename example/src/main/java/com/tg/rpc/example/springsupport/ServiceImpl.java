package com.tg.rpc.example.springsupport;

import com.tg.rpc.example.springsupport.Service;
import org.springframework.stereotype.Component;

/**
 * Created by twogoods on 17/2/18.
 */
@Component
public class ServiceImpl {

    public String test() {
        return "hahah";
    }
}
