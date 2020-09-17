package handler;

import bean.Message;
import netty.client.TcpClient809;
import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utill.JT809Constants;

public class HeartBeatHandler extends IdleStateAwareChannelHandler {
    private static Logger LOG = LoggerFactory.getLogger(HeartBeatHandler.class);

    @Override
    public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception {

        if (StringUtils.isBlank(TcpClient809.LONGINSTATUS) || TcpClient809.LOGINING.equals(TcpClient809.LONGINSTATUS)) {
            TcpClient809.getInstance().login2FuJianGov();
            LOG.error("利用空闲心跳去登录------ 开始登录");
        }

        if (e.getState() == IdleState.WRITER_IDLE) {
            LOG.error("链路空闲，发送心跳!");
            Message msg = new Message(JT809Constants.UP_LINKETEST_REQ);
            e.getChannel().write(TcpClient809.buildMessage(msg));
            super.channelIdle(ctx, e);
        }
    }
}
