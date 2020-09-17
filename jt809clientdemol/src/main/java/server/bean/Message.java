package server.bean;

import org.jboss.netty.buffer.ChannelBuffer;

public class Message {
    public static final int MSG_HEAD = 0x5b;
    public static final int MSG_TAIL = 0x5d;

    //报文中除数据体外，固定的数据长度
    public static final int MSG_FIX_LENGTH = 26;

    //报文序列号，自增。
    private static int internalMsgNo = 0;
    private long msgLength, encryptFlag=1,  msgGesscenterId, encryptKey;
    private int crcCode,msgId,msgSn;
    private ChannelBuffer msgBody;
    private byte[] versionFlag = {0,0,1};

    //下行报文标识，值为1时，代表发送的数据；默认为0，代表接收的报文
    //private int downFlag = 0;

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

    public int getCrcCode() {
        return crcCode;
    }

    public void setCrcCode(int crcCode) {
        this.crcCode = crcCode;
    }

    public byte[] getVersionFlag() {
        return versionFlag;
    }

    public void setVersionFlag(byte[] versionFlagBytes) {
        this.versionFlag = versionFlagBytes;
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

    public void setEncryptFlag(int encryptFlag) {
        this.encryptFlag = encryptFlag;
    }

    public int getMsgSn() {
        return msgSn;
    }

    public void setMsgSn(int msgSn) {
        this.msgSn = msgSn;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public long getMsgGesscenterId() {
        return msgGesscenterId;
    }

    public void setMsgGesscenterId(long msgGesscenterId) {
        this.msgGesscenterId = msgGesscenterId;
    }

    public long getEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(long encryptKey) {
        this.encryptKey = encryptKey;
    }

    public ChannelBuffer getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(ChannelBuffer msgBody) {
        System.out.println("0x"+Integer.toHexString(this.msgId)+": "+msgBody.capacity()+" 字节数据体.");
        this.msgBody = msgBody;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return ("MSG_ID:" + this.msgId + " --> " + this.msgBody + " version flag:" + this.versionFlag + " encryptKey:"
                + this.encryptKey + " crcCode:" + this.crcCode+" msgGesscenterId:"+this.msgGesscenterId);
    }
}
