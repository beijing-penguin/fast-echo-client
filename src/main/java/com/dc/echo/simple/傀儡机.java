package com.dc.echo.simple;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Scanner;

import org.apache.commons.codec.binary.Base64;

import com.dc.echo.client.EchoCode;
import com.dc.echo.client.EchoConnection;
import com.dc.echo.client.EchoCoreUtils;
import com.dc.echo.client.Message;
import com.dc.echo.client.MessageListener;

import io.netty.channel.ChannelHandlerContext;
import net.coobird.thumbnailator.Thumbnails;

public class 傀儡机 {
    public static void main(String[] args) throws Throwable {
        Robot myRobot = new Robot();
        //EchoConnection conn = new EchoConnection("47.104.77.145", 6666);
        EchoConnection conn = new EchoConnection("111.205.170.145", 6666);
        conn.setListener(new MessageListener() {
            @Override
            public void callback(ChannelHandlerContext ctx, byte[] dataByteArr) {
                Message message = EchoCoreUtils.byteToMessage(dataByteArr);
                if (message.getSender() != null) {
                    try {
                        if (message.getMsgCode() == 4) {
                            myRobot.mouseMove(Integer.parseInt(message.getContent().split(",")[0]), Integer.parseInt(message.getContent().split(",")[1]));;
                        }
                        if (message.getMsgCode() == 5) {//鼠标按住
                        	myRobot.mouseMove(Integer.parseInt(message.getContent().split(",")[0]), Integer.parseInt(message.getContent().split(",")[1]));;
                            myRobot.mousePress(InputEvent.BUTTON1_MASK);
                        }
                        if (message.getMsgCode() == 6) {//鼠标释放
                            myRobot.mouseMove(Integer.parseInt(message.getContent().split(",")[0]), Integer.parseInt(message.getContent().split(",")[1]));;
                            myRobot.mouseRelease(InputEvent.BUTTON1_MASK);
                        }
                        if (message.getMsgCode() == 7) {//右击
                            myRobot.mouseMove(Integer.parseInt(message.getContent().split(",")[0]), Integer.parseInt(message.getContent().split(",")[1]));;
                            myRobot.mousePress(InputEvent.BUTTON3_MASK);
                        }
                        if (message.getMsgCode() == 8) {//右击
                            myRobot.mouseMove(Integer.parseInt(message.getContent().split(",")[0]), Integer.parseInt(message.getContent().split(",")[1]));;
                            myRobot.mouseRelease(InputEvent.BUTTON3_MASK);
                        }
                        if (message.getMsgCode() == 9) {//滚动
                            myRobot.mouseWheel(Integer.parseInt(message.getContent()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        conn.setReadTimeOut(10);//10s超时
        conn.setSync(true);//开启同步请求
        conn.connect();

        String encoding = System.getProperty("file.encoding");
        Scanner sc = new Scanner(System.in,System.getProperty("file.encoding"));
        System.out.println(System.getProperty("file.encoding"));
        String username = null;
        while(true) {
            System.out.print(new String("输入自己的用户名："));
            username = sc.nextLine();
            try {
                Message message = EchoCoreUtils.byteToMessage(login(conn, username));
                if(message.getMsgCode()==EchoCode.SUCCESS) {
                    break;
                }
                if (message.getMsgCode() == EchoCode.USER_EXIST) {// 用户已被注册，请重启程序，并使用新的用户名登录
                    System.out.println("用户已被注册，请使用新的用户名登录");
                }
            }catch (Throwable e) {
                System.out.print("登录失败");
                e.printStackTrace();
            }
            System.out.print(new String("输入自己的用户名："));
        }

        System.out.print("输入对方的用户名：");
        String receiver = sc.nextLine();
        System.out.print(username+":");
        try {
            while(true) {
                Message message = new Message();
                message.setVersion("1.0");
                message.setMsgCode(EchoCode.MESSAGE_TRANS_ACTION);
                message.setReceiver(new String[] {receiver});
                message.setSender(username);
                message.setEncoding(encoding);
                message.setSendTime(System.currentTimeMillis());

                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                BufferedImage image = new Robot().createScreenCapture(new Rectangle(screenSize));
                
                
                    Graphics g = image.getGraphics();
                    Image scaledImage = image.getScaledInstance(1200, 800, Image.SCALE_SMOOTH);
                    BufferedImage ret = new BufferedImage(1200, 800, BufferedImage.TYPE_INT_RGB);
                    g = ret.getGraphics();
                    g.drawImage(scaledImage, 0, 0, null);
                ByteArrayOutputStream out = new ByteArrayOutputStream();

                Thumbnails.of((BufferedImage)scaledImage).scale(1).outputQuality(0.25f).outputFormat("jpg").toOutputStream(out);
                out.flush();
                message.setContent(Base64.encodeBase64String(out.toByteArray()));
                conn.setSync(false);//设置异步请求
                conn.sendMessage(EchoCoreUtils.messageToByteArr(message));//发送消息
                Thread.sleep(20);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.print(username+":");
        sc.close();
    }
    public static byte[] login(EchoConnection conn, String username) throws Throwable {
        System.out.println("正在登录...........................");
        Message message = new Message();
        message.setVersion("1.0");
        message.setMsgCode(EchoCode.LOGIN_ACTION);
        message.setSender(username);
        return conn.sendMessage(EchoCoreUtils.messageToByteArr(message));// 开始登录
    }
}
