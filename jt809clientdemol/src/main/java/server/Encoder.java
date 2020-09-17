package server;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import server.bean.Message;
import server.utill.Util;

public class Encoder extends SimpleChannelHandler {

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        // TODO Auto-generated method stub
        ChannelBuffer buffer = buildMessage((Message) e.getMessage());
        ;
        if (buffer != null) {
            Channels.write(ctx, e.getFuture(), buffer);
        }
    }


    /**
     * 生成下行报文
     *
     * @param msg
     * @return
     */
    private ChannelBuffer buildMessage(Message msg) {
        int bodyLength = msg.getMsgBody().capacity();
        ChannelBuffer buffer = ChannelBuffers.buffer(bodyLength + Message.MSG_FIX_LENGTH);
        buffer.writeByte(Message.MSG_HEAD);  //1
        //--------------数据头----------
        buffer.writeInt(buffer.capacity());  //4
        buffer.writeInt(msg.getMsgSn());     //4
        buffer.writeShort(msg.getMsgId());   //2
        buffer.writeInt(1); //4
        buffer.writeBytes(msg.getVersionFlag());//3
        buffer.writeByte(0);//1
        buffer.writeInt(20000000);//4
        //--------------数据体----------
        buffer.writeBytes(msg.getMsgBody());
        //------------crc校验码---------
        byte[] b = ChannelBuffers.buffer(bodyLength + 22).array();
        buffer.getBytes(1, b);
        int crcValue = Util.crc16(b);
        buffer.writeShort(crcValue);//2
        buffer.writeByte(Message.MSG_TAIL);//1
        System.out.println("before send ：" + buffer);
        return buffer;
    }

}
