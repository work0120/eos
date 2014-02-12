/**
 * @(#)RpcInvocation
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-1-22 下午9:34
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.rpc.impl;

import com.sunsharing.eos.common.Constants;
import com.sunsharing.eos.common.config.ServiceConfig;
import com.sunsharing.eos.common.rpc.Invocation;
import com.sunsharing.eos.common.utils.StringUtils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br>
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
public class RpcInvocation implements Invocation, Serializable {

    private String id;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] arguments;
    private String retType;
    private String mock;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public String getRetType() {
        return retType;
    }

    public void setRetType(String retType) {
        this.retType = retType;
    }

    public String getMock() {
        return mock;
    }

    public void setMock(String mock) {
        this.mock = mock;
    }

    @Override
    public String toString() {
        return "RpcInvocation [methodName=" + methodName + ", parameterTypes="
                + Arrays.toString(parameterTypes) + ", arguments=" + Arrays.toString(arguments)
                + "]";
    }

    /**
     * 取得最终的mock值
     *
     * @param config
     * @param useMock
     * @return
     */
    public String getRealMock(ServiceConfig config, boolean useMock) {
        String realMock = this.mock;
        if (useMock) {
            if (Constants.RETURN_TYPE_VOID.equals(this.retType) && !StringUtils.isBlank(realMock)) {
                //如果方法是void的并且要走mock,那么直接设置mock为void
                realMock = Constants.MOCK_VOID;
            } else if (StringUtils.isBlank(realMock)) {
                realMock = config.getMethodMock(methodName);
            }
        }
        return realMock;
    }
}

