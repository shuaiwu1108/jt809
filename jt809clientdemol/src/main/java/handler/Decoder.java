package handler;

import bean.Message;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class Decoder extends FrameDecoder
{
    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        int head = buffer.getByte(0);
        int tail = buffer.getByte(buffer.capacity() - 1);
        if( !(head == Message.MSG_HEAD && tail == Message.MSG_TALL)){
            return null;
        }
        buffer.skipBytes(1);
        Message msg = this.buildMessage(buffer);
        return msg;
    }

    private Message buildMessage(ChannelBuffer buffer){
        Message msg = new Message();
        msg.setMsgLength(buffer.readUnsignedInt());
        msg.setMsgSn(buffer.readInt());//4byte
        msg.setMsgId(buffer.readUnsignedShort());//2byte
        msg.setMsgGesscenterId(buffer.readUnsignedInt());//4byte
        msg.setVersionFlag(buffer.readBytes(3).array());//3byte
        msg.setEncryptFlag(buffer.readUnsignedByte());//1byte
        msg.setEncryptKey(buffer.readUnsignedInt());//4byte
        if(buffer.readableBytes() >= 9){//跳过UTC时间
            buffer.skipBytes(buffer.readableBytes() - 8);
        }
        ChannelBuffer bodyBytes = buffer.readBytes(buffer.readableBytes() -3);
        msg.setMsgBody(bodyBytes);//请求体
        msg.setCrcCode(buffer.readUnsignedShort());//2byte
        buffer.skipBytes(1);
        return msg;
    }

}
