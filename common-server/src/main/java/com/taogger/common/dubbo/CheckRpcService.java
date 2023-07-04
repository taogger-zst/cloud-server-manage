package com.taogger.common.dubbo;

import com.taogger.common.dubbo.vo.CheckRpcResponse;

/**
 * @author taogger
 * @description 内容审查service
 * @date 2023-07-04 15:27
 **/
public interface CheckRpcService {

    CheckRpcResponse textCheck(String str);

    CheckRpcResponse imageCheck(String str);
}
