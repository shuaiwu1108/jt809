package server;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import server.bean.Message;

public class Decoder extends FrameDecoder {

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        // TODO Auto-generated method stub

        int head = buffer.getByte(0);
        int tail = buffer.getByte(buffer.capacity() - 1);
        System.out.println(buffer.getByte(buffer.capacity() - 1));
        System.out.println(buffer.getByte(0));

        if (!(head == Message.MSG_HEAD && tail == Message.MSG_TAIL)) {
            return null;
        }

        //跳过头标识
        buffer.skipBytes(1);
        //读取报文长度（目前有不一致情况）

        Message msg = this.buildMessage(buffer);
        return msg;
    }


    private Message buildMessage(ChannelBuffer buffer) {
        Message msg = new Message();
        //读取报文长度（目前有不一致情况）
        msg.setMsgLength(buffer.readUnsignedInt());
        msg.setMsgSn(buffer.readInt());//4 byte
        msg.setMsgId(buffer.readUnsignedShort());//2 byte
        msg.setMsgGesscenterId(buffer.readUnsignedInt());//4 byte
        msg.setVersionFlag(buffer.readBytes(3).array());//3 byte
        msg.setEncryptFlag(buffer.readUnsignedByte());//1 byte
        msg.setEncryptKey(buffer.readUnsignedInt());//4 byte
        ChannelBuffer bodyBytes = buffer.readBytes(buffer.readableBytes() - 2 - 1);//数据体为变长字节
        msg.setMsgBody(bodyBytes);
        msg.setCrcCode(buffer.readUnsignedShort());//2 byte
        //跳过尾标识
        buffer.skipBytes(1);//1 byte
        //buffer.readByte();
        System.out.println("after build message readable bytes:" + buffer.readableBytes());
        return msg;
    }


}
