package bean;

import org.jboss.netty.buffer.ChannelBuffer;

import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable {
    private static final long serialVersionUID = 4398559115325723920L;

    public static final int MSG_HEAD = 0x5b;
    public static final int MSG_TALL = 0x5d;

    //报文中除数据体外，固定的数据长度
    public static final int MSG_FIX_LENGTH = 26;

    private static int internalMsgNo = 0;
    private long msgLength;
    private long encryptFlag=1;
    private int msgGesscenterId;
    private long encryptKey;
    private int crcCode;
    private int msgId;
    private int msgSn;

    private ChannelBuffer msgBody;
    private byte[] versionFlag = {0,0,1};

    //下行报文标识，值为1时，代表发送的数据；默认为0，代表接收的报文
//  private int downFlag = 0;

    public Message(){}


    public Message(int msgId){
        //下行报文需要填充报文序列号
        synchronized((Integer)internalMsgNo) {
            if(internalMsgNo == Integer.MAX_VALUE){
                internalMsgNo = 0;
            }
        }
        this.msgSn = ++internalMsgNo;
        this.msgId = msgId;
        //this.downFlag = 1;
    }


    public static int getInternalMsgNo() {
        return internalMsgNo;
    }


    public static void setInternalMsgNo(int internalMsgNo) {
        Message.internalMsgNo = internalMsgNo;
    }


    public long getMsgLength() {
        return msgLength;
    }


    public void setMsgLength(long msgLength) {
        this.msgLength = msgLength;
    }


    public long getEncryptFlag() {
        return encryptFlag;
    }


    public void setEncryptFlag(long encryptFlag) {
        this.encryptFlag = encryptFlag;
    }


    public int getMsgGesscenterId() {
        return msgGesscenterId;
    }


    public void setMsgGesscenterId(int msgGesscenterId) {
        this.msgGesscenterId = msgGesscenterId;
    }
    public void setMsgGesscenterId(long msgGesscenterId) {
        this.msgGesscenterId = (int)msgGesscenterId;
    }


    public long getEncryptKey() {
        return encryptKey;
    }


    public void setEncryptKey(long encryptKey) {
        this.encryptKey = encryptKey;
    }


    public int getCrcCode() {
        return crcCode;
    }


    public void setCrcCode(int crcCode) {
        this.crcCode = crcCode;
    }


    public int getMsgId() {
        return msgId;
    }


    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }


    public int getMsgSn() {
        return msgSn;
    }


    public void setMsgSn(int msgSn) {
        this.msgSn = msgSn;
    }


    public ChannelBuffer getMsgBody() {
        return msgBody;
    }


    public void setMsgBody(ChannelBuffer msgBody) {
        this.msgBody = msgBody;
    }


    public byte[] getVersionFlag() {
        return versionFlag;
    }


    public void setVersionFlag(byte[] versionFlag) {
        this.versionFlag = versionFlag;
    }


    public static int getMsgHead() {
        return MSG_HEAD;
    }


    public static int getMsgTall() {
        return MSG_TALL;
    }


    public static int getMsgFixLength() {
        return MSG_FIX_LENGTH;
    }


    @Override
    public String toString() {
        return "Message [msgLength=" + msgLength + ", encryptFlag=" + encryptFlag + ", msgGesscenterId="
                + msgGesscenterId + ", encryptKey=" + encryptKey + ", crcCode=" + crcCode + ", msgId=" + msgId
                + ", msgSn=" + msgSn + ", msgBody=" + msgBody + ", versionFlag=" + Arrays.toString(versionFlag) + "]";
    }
}
