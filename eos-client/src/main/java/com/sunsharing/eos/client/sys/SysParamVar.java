/**
 * @(#)SysParamVar
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-18 上午1:03
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.client.sys;

import javax.servlet.ServletRequest;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br>  系统支持js入参值为${}变量获取的接口
 * <br>  当入参为后台变量形式,如入参userId=${userId},那么后台java端将userId转换为系统定义的值
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
public interface SysParamVar {

    /**
     * 类初始化方法，像有些系统缓存类的需要用到初始化
     */
    void init();

    /**
     * 获取参数为paramKey的变量值
     *
     * @param req
     * @param paramKey
     * @return
     */
    String getParamVariable(ServletRequest req, String paramKey);
}