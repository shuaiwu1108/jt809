package netty.client;

import bean.Idc2AwsGpsVo;
import bean.Message;
import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utill.CRC16CCITT;
import utill.Constants;
import utill.JT809Constants;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

public class TcpClient809 {

    private static Logger LOG = LoggerFactory.getLogger(TcpClient809.class);
    public static int PLANT_CODE;//公司介入码
    public static int ZUCHE_ID_FUJIAN;//公司用户名
    public static String ZUCHE_PWD_FUJIAN;
    public static String LONGINSTATUS = "";
    public static String LOGINING = "logining";
    private static int LOGIN_FLAG = 0;
    private static String DOWN_LINK_IP = "127.0.0.1";//初始化基类
    private static TcpClient tcpClient = TcpClient.getInstence();//初始化
    private static TcpClient809 tcpClient809 = new TcpClient809();
    //初始化channel,以便心跳机制及时登录
    private Channel channel = tcpClient.getChannel(Constants.TCP_ADDRESS, Constants.TCP_PORT);

    public static TcpClient809 getInstance() {
        String localIp = "127.0.0.1";
        if (StringUtils.isNotBlank(localIp)) {
            PLANT_CODE = 11;
            ZUCHE_ID_FUJIAN = 11;
            ZUCHE_PWD_FUJIAN = "";
        } else {
            LOG.error("获取本机IP异常");
        }
        return tcpClient809;
    }

    /**
     * 判断是否登录 * boolean * @return
     */
    public boolean isLogined() {
        return Constants.LOGIN_STATAUS.equals(LONGINSTATUS); //Constants常量类，自己随便定义就好，LOGIN_STATAUS="0x00"
    }

    /**
     * 登录接入平台 * boolean * @return
     */
    public boolean login2FuJianGov() {
        boolean success = false;
        if (!Constants.LOGIN_STATAUS.equals(LONGINSTATUS) && !LOGINING.equals(LONGINSTATUS)) {
            //开始登录 Message为数据对象，代码稍后给出
            Message msg = new Message(JT809Constants.UP_CONNECT_REQ);
            ChannelBuffer buffer = ChannelBuffers.buffer(46);
            buffer.writeInt(ZUCHE_ID_FUJIAN);
            byte[] pwd = getBytesWithLengthAfter(8, ZUCHE_PWD_FUJIAN.getBytes());
            buffer.writeBytes(pwd);
            byte[] ip = getBytesWithLengthAfter(32, DOWN_LINK_IP.getBytes());
            buffer.writeBytes(ip);
            buffer.writeShort((short) 8091);//不明白这是什么  //从链路端口, server端通过ip+port与客户端建立从链接
            msg.setMsgBody(buffer);
            channel = tcpClient.getChannel(Constants.TCP_ADDRESS, Constants.TCP_PORT);
            channel.write(buildMessage(msg));
            LONGINSTATUS = LOGINING;
        }
        return success;
    }

    public static ChannelBuffer buildMessage(Message msg) {
        int bodyLength = 0;
        if (null != msg.getMsgBody()) {
            bodyLength = msg.getMsgBody().readableBytes();
        }
        msg.setMsgGesscenterId(PLANT_CODE);
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer(bodyLength + Message.MSG_FIX_LENGTH);
        ChannelBuffer headBuffer = ChannelBuffers.buffer(22);//---数据头
        headBuffer.writeInt(buffer.capacity() - 1);
        headBuffer.writeInt(msg.getMsgSn());
        headBuffer.writeShort((short) msg.getMsgId());
        headBuffer.writeInt(msg.getMsgGesscenterId());
        headBuffer.writeBytes(msg.getVersionFlag());
        headBuffer.writeByte(0);
        headBuffer.writeInt(10);
        buffer.writeBytes(headBuffer);//---数据体
        if (null != msg.getMsgBody()) {
            buffer.writeBytes(msg.getMsgBody());
        }
        ChannelBuffer finalBuffer = ChannelBuffers.copiedBuffer(buffer);//--crc校验码
        byte[] b = ChannelBuffers.buffer(finalBuffer.readableBytes()).array();
        finalBuffer.getBytes(0, b);
        int crcValue = CRC16CCITT.crc16(b);
        finalBuffer.writeShort((short) crcValue);//2//转义
        byte[] bytes = ChannelBuffers.copiedBuffer(finalBuffer).array();
        ChannelBuffer headFormatedBuffer = ChannelBuffers.dynamicBuffer(finalBuffer.readableBytes());
        formatBuffer(bytes, headFormatedBuffer);
        ChannelBuffer buffera = ChannelBuffers.buffer(headFormatedBuffer.readableBytes() + 2);
        buffera.writeByte(Message.MSG_HEAD);
        buffera.writeBytes(headFormatedBuffer);
        buffera.writeByte(Message.MSG_TALL);
        return ChannelBuffers.copiedBuffer(buffera);
    }

    /**
     * 发送数据到接入平台 * boolean * @param awsVo 是上层程序得到的带发送的数据对象，可以看自己的需求，替换 * @return
     */
    public boolean sendMsg2FuJianGov(Idc2AwsGpsVo awsVo) {
        boolean success = false;
        if (isLogined()) {
            //已经登录成功，开始发送数据
            LOG.info("开始发送数据");
            channel = tcpClient.getChannel(Constants.TCP_ADDRESS, Constants.TCP_PORT);
            if (null != channel && channel.isWritable()) {
                Message msg = buildSendVO(awsVo);
                ChannelBuffer msgBuffer = buildMessage(msg);
                channel.write(msgBuffer);
            } else {
                LONGINSTATUS = "";
            }
        } else if (
                LOGIN_FLAG == 0) {
            LOGIN_FLAG++;
            login2FuJianGov();
            LOG.error("--------------第一次登录");
        } else {
            LOG.error("--------------等待登录");
        }
        return success;
    }

    /**
     * 转换VO * void * @param awsVo
     */
    private Message buildSendVO(Idc2AwsGpsVo awsVo) {
        Message msg = new Message(JT809Constants.UP_EXG_MSG);
        ChannelBuffer buffer = ChannelBuffers.buffer(36);//是否加密
        buffer.writeByte((byte) 0);//0未加密 // 1//日月年dmyy
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        buffer.writeByte((byte) cal.get(Calendar.DATE));
        buffer.writeByte((byte) (cal.get(Calendar.MONTH) + 1));
        String strYear = cal.get(Calendar.YEAR) + "";
        buffer.writeBytes(getBytesWithLengthAfter(4, strYear.getBytes()));//4//时分秒
        buffer.writeByte((byte) cal.get(Calendar.HOUR));
        buffer.writeByte((byte) cal.get(Calendar.MINUTE));
        buffer.writeByte((byte) cal.get(Calendar.SECOND));//3//经度，纬度
        buffer.writeInt(formatLonLat(awsVo.getLon()));//4
        buffer.writeInt(formatLonLat(awsVo.getLat()));//4//速度
        buffer.writeShort(awsVo.getSpeed());//2//行驶记录速度
        buffer.writeShort(awsVo.getSpeed());//2//车辆当前总里程数
        buffer.writeInt(awsVo.getMileage());//4//方向
        buffer.writeShort(awsVo.getDirection());//2//海拔
        buffer.writeShort((short) 0);//2//车辆状态buffer.writeInt(1);//4//报警状态
        buffer.writeInt(0);//0表示正常；1表示报警//4
        ChannelBuffer headBuffer = ChannelBuffers.buffer(buffer.capacity() + 28);
        headBuffer.writeBytes(getBytesWithLengthAfter(21, awsVo.getVehicleNo().getBytes()));//21 车牌号
        headBuffer.writeByte((byte) 1);//1 车牌颜色：注意不是车身颜色
        headBuffer.writeShort(JT809Constants.UP_EXG_MSG_REAL_LOCATION);//2 子业务码
        headBuffer.writeInt(buffer.capacity());
        headBuffer.writeBytes(buffer);
        msg.setMsgBody(headBuffer);
        return msg;
    }

    /**
     * 报文转义 * void * @param bytes * @param formatBuffer
     */
    private static void formatBuffer(byte[] bytes, ChannelBuffer formatBuffer) {
        for (byte b : bytes) {
            switch (b) {
                case 0x5b:
                    byte[] formatByte0x5b = new byte[2];
                    formatByte0x5b[0] = 0x5a;
                    formatByte0x5b[1] = 0x01;
                    formatBuffer.writeBytes(formatByte0x5b);
                    break;
                case 0x5a:
                    byte[] formatByte0x5a = new byte[2];
                    formatByte0x5a[0] = 0x5a;
                    formatByte0x5a[1] = 0x02;
                    formatBuffer.writeBytes(formatByte0x5a);
                    break;
                case 0x5d:
                    byte[] formatByte0x5d = new byte[2];
                    formatByte0x5d[0] = 0x5e;
                    formatByte0x5d[1] = 0x01;
                    formatBuffer.writeBytes(formatByte0x5d);
                    break;
                case 0x5e:
                    byte[] formatByte0x5e = new byte[2];
                    formatByte0x5e[0] = 0x5e;
                    formatByte0x5e[1] = 0x02;
                    formatBuffer.writeBytes(formatByte0x5e);
                    break;
                default:
                    formatBuffer.writeByte(b);
                    break;
            }
        }
    }

    /**
     * 16进制字符串转换成byte数组 * byte[] * @param hex
     */
    public static byte[] hexStringToByte(String hex) {
        hex = hex.toUpperCase();
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    /**
     * 格式化经纬度,保留六位小数 * int * @param needFormat * @return
     */
    private int formatLonLat(Double needFormat) {
        NumberFormat numFormat = NumberFormat.getInstance();
        numFormat.setMaximumFractionDigits(6);
        numFormat.setGroupingUsed(false);
        String fristFromat = numFormat.format(needFormat);
        Double formatedDouble = Double.parseDouble(fristFromat);
        numFormat.setMaximumFractionDigits(0);
        String formatedValue = numFormat.format(formatedDouble * 1000000);
        return Integer.parseInt(formatedValue);
    }

    /**
     * 补全位数不够的定长参数 有些定长参数，实际值长度不够，在后面补0x00 * byte[] * @param length * @param pwdByte * @return
     */
    private byte[] getBytesWithLengthAfter(int length, byte[] pwdByte) {
        byte[] lengthByte = new byte[length];
        for (int i = 0; i < pwdByte.length; i++) {
            lengthByte[i] = pwdByte[i];
        }
        for (int i = 0; i < (length - pwdByte.length); i++) {
            lengthByte[pwdByte.length + i] = 0x00;
        }
        return lengthByte;
    }

    public static void main(String[] args) {
        TcpClient809 s = TcpClient809.getInstance();
        Idc2AwsGpsVo awsVo = new Idc2AwsGpsVo();
        awsVo.setDirection((short) 12);
        awsVo.setLon(117.2900911);
        awsVo.setLat(39.56362);
        awsVo.setSpeed((short) 45);
        awsVo.setMileage(10001);
        awsVo.setVehicleNo("幽123D32");
        LOG.info("开始send message");
        s.sendMsg2FuJianGov(awsVo);
        try {
            Thread.sleep(20 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        s.sendMsg2FuJianGov(awsVo);
    }
}
