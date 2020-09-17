package server;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import server.bean.Message;

import java.nio.charset.Charset;

public class BusiHandler extends SimpleChannelHandler {

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        // TODO Auto-generated method stub
        Message msg = (Message) e.getMessage();
        switch (msg.getMsgId()) {
            case 0x1001:
                login(msg, ctx, e);
                break;
            case 0x1005:
                System.out.println("主链路连接保持请求消息。");
                heartBeat(msg, ctx, e);
                break;
            case 0x1200:
                System.out.println("主链路动态信息交换消息");
                System.out.println("msg:" + msg.toString());
                parseGPS(msg, ctx, e);
                break;
            default:
                break;
        }
    }

    private void login(Message msg, ChannelHandlerContext ctx, MessageEvent e) {
        int userId = msg.getMsgBody().readInt();
        String passWord = msg.getMsgBody().readBytes(8).toString(Charset.forName("GBK"));
        String ip = msg.getMsgBody().readBytes(32).toString(Charset.forName("GBK"));
        int port = msg.getMsgBody().readUnsignedShort();
        msg.getMsgBody().clear();
        System.out.println("userId:" + userId);
        System.out.println("passWord:" + passWord);
        System.out.println("ip:" + ip);
        System.out.println("port:" + port);

        Message msgRep = new Message(0x1002);
        ChannelBuffer buffer = ChannelBuffers.buffer(5);
        buffer.writeByte(0x00);

        //校验码，临时写死
        buffer.writeInt(1111);
        msgRep.setMsgBody(buffer);
        ChannelFuture f = e.getChannel().write(msgRep);

        // f.addListener(ChannelFutureListener.CLOSE);
    }

    private void heartBeat(Message msg, ChannelHandlerContext ctx, MessageEvent e) {
        Message msgRep = new Message(0x1006);
        ChannelBuffer buffer = ChannelBuffers.buffer(0);
        msgRep.setMsgBody(buffer);
        ChannelFuture f = e.getChannel().write(msgRep);
    }

    private void parseGPS(Message msg, ChannelHandlerContext ctx, MessageEvent e) {
        String carNum = msg.getMsgBody().readBytes(21).toString(Charset.forName("UTF8"));
        System.out.println("carNum:" + carNum);
        byte carColor = msg.getMsgBody().readByte();
        System.out.println("carColor:" + carColor);
        int dataType = msg.getMsgBody().readUnsignedShort();
        System.out.println("dataType:" + dataType);
        int capacity = msg.getMsgBody().readInt();
        System.out.println("capacity:" + capacity);
        byte encryptKey = msg.getMsgBody().readByte();//加密
        System.out.println("encryptKey:" + encryptKey);
        byte day = msg.getMsgBody().readByte();
        System.out.println("day:" + day);
        byte month = msg.getMsgBody().readByte();
        System.out.println("month:" + month);
        String year = msg.getMsgBody().readBytes(4).toString(Charset.forName("UTF8"));
        System.out.println("year:" + year);
        byte hour = msg.getMsgBody().readByte();
        System.out.println("hour:" + hour);
        byte minute = msg.getMsgBody().readByte();
        System.out.println("minute:" + minute);
        byte second = msg.getMsgBody().readByte();
        System.out.println("second:" + second);
        int lon = msg.getMsgBody().readInt();
        System.out.println("lon:" + lon);
        int lat = msg.getMsgBody().readInt();
        System.out.println("lat:" + lat);
    }


}
