package com.taogger.check.service;

import com.taogger.common.dubbo.CheckRpcService;
import com.taogger.common.dubbo.vo.CheckRpcResponse;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author taogger
 * @description 内容审查service-rpc
 * @date 2023-07-04 16:02
 **/
@DubboService
public class CheckRpcServiceImpl implements CheckRpcService {
    @Override
    public CheckRpcResponse textCheck(String str) {
        CheckRpcResponse checkRpcResponse = new CheckRpcResponse();
        //模拟审查
        if (str.equals("臭不要脸")) {
            checkRpcResponse.setAbnormal("骂人");
            checkRpcResponse.setPass(Boolean.FALSE);
        } else if (str.equals("刘备")) {
            checkRpcResponse.setAbnormal("政治名人");
            checkRpcResponse.setPass(Boolean.FALSE);
        } else {
            checkRpcResponse.setAbnormal("正常");
            checkRpcResponse.setPass(Boolean.TRUE);
        }
        return checkRpcResponse;
    }

    @Override
    public CheckRpcResponse imageCheck(String str) {
        return null;
    }
}
