/**
 * @(#)NettyClient
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-5 上午10:19
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.rpc.remoting.netty;

import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;
import com.sunsharing.eos.common.utils.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
public class NettyClient {
    Logger logger = Logger.getLogger(NettyClient.class);

    public static final int CONNECT_TIMEOUT = 5000;

    public static Map<String, ArrayBlockingQueue<ResponsePro>> result =
            new ConcurrentHashMap<String, ArrayBlockingQueue<ResponsePro>>();

    // 因ChannelFactory的关闭有DirectMemory泄露，采用静态化规避
    // https://issues.jboss.org/browse/NETTY-424
    private final static ChannelFactory clientSocketChannelFactory = new
            NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
            Executors.newCachedThreadPool());
    private ClientBootstrap clientBootstrap;

    public ResponsePro sendMsg(String ip, int port, RequestPro pro, int timeout) throws Throwable {

        ChannelFuture future = null;
        clientBootstrap = new ClientBootstrap(clientSocketChannelFactory);
        ChannelPipeline pipeline = clientBootstrap.getPipeline();
        pipeline.addLast("decoder", new ExDecode());
        pipeline.addLast("encoder", new ExEncode());
        pipeline.addLast("handler", new ClientHandler());

        clientBootstrap.setOption("tcpNoDelay", true);
        clientBootstrap.setOption("keepAlive", true);
        clientBootstrap.setOption("connectTimeoutMillis", CONNECT_TIMEOUT);
        //clientBootstrap.setOption("reuseAddress", true); //注意child前缀
        future = clientBootstrap.connect(new InetSocketAddress(ip, port));
        boolean ret = future.awaitUninterruptibly(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
        if (ret && future.isSuccess()) {
            logger.info("client is connected to netty server " + ip + ":" + port);
            if (StringUtils.isBlank(pro.getMsgId())) {
                pro.setMsgId(StringUtils.genUUID());
            }
            result.put(pro.getMsgId(), new ArrayBlockingQueue<ResponsePro>(1));
            try {

                future.getChannel().write(pro);

                //等待返回
                ArrayBlockingQueue<ResponsePro> blockingQueue = result.get(pro.getMsgId());
                ResponsePro result = blockingQueue.poll(timeout, TimeUnit.MILLISECONDS);
                logger.debug("返回结果:" + result);
                if (result == null) {
                    logger.error("等待结果超时!");
                    throw new RpcException(RpcException.TIMEOUT_EXCEPTION);
                }
                return result;
            } catch (Exception e) {
                logger.error("请求出错！", e);
                throw e;
            } finally {
                result.remove(pro.getMsgId());
                if (future != null) {
                    future.getChannel().close();
                }
//                clientBootstrap.releaseExternalResources();
            }
        } else {
            throw new RpcException(RpcException.CONNECT_EXCEPTION, "client failed to connect to server "
                    + ip + ", error message is:" + future.getCause() == null ? "unknown" : future.getCause().getMessage(), future.getCause());
        }
    }
}
