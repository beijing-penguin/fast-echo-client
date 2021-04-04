package com.dc.echo.simple;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.apache.commons.codec.binary.Base64;

import com.dc.echo.client.EchoCode;
import com.dc.echo.client.EchoConnection;
import com.dc.echo.client.EchoCoreUtils;
import com.dc.echo.client.Message;
import com.dc.echo.client.MessageListener;

import io.netty.channel.ChannelHandlerContext;
import net.coobird.thumbnailator.Thumbnails;

public class 控制机 {
    public static void main(String[] args) throws Throwable {
        EchoConnection conn = new EchoConnection("8.140.9.168", 6666);
        //EchoConnection conn = new EchoConnection("47.104.77.145", 6666);
        JFrame frame = new JFrame();
        //frame.setBackground(null);
        //frame.setLocationRelativeTo(null);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.setVisible(true);
        //frame.setFocusable(true);
        frame.setUndecorated(true);
        frame.getGraphicsConfiguration().getDevice().setFullScreenWindow(frame);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        conn.setListener(new MessageListener() {
            @Override
            public void callback(ChannelHandlerContext ctx, byte[] dataByteArr) {
                Message message = null;
                try {
                    message = EchoCoreUtils.byteToMessage(dataByteArr);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                if(message!=null && message.getContent()!=null) {
                    if (message.getSender() != null) {
                        try {
                            if (message.getMsgCode() == EchoCode.MESSAGE_TRANS_ACTION) {
//                                BufferedImage img = ImageIO.read(new ByteArrayInputStream(Base64.decodeBase64(message.getContent())));
//                                int w = img.getWidth();  
//                                int h = img.getHeight(); 
//                                
//                                
//                                BufferedImage dimg = new BufferedImage(img.getWidth()*2, img.getHeight()*2, img.getType());  
//                                Graphics2D g = dimg.createGraphics();  
//                                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//                                RenderingHints.VALUE_INTERPOLATION_BILINEAR);  
//                                g.drawImage(img, 0, 0, img.getWidth()*2, img.getHeight()*2, 0, 0, w, h, null);  
//                                g.dispose();
//                                
                                
                                ImageIcon ii = new ImageIcon(Base64.decodeBase64(message.getContent()));
                                //                            JLayeredPane layeredPane = new JLayeredPane();
                                //                            layeredPane.setBounds(0, 0, ii.getIconWidth(), ii.getIconHeight());
                                //                            frame.add(layeredPane);
                                //                            
                                //                            JPanel bgPanel = new JPanel();
                                //                            bgPanel.setBounds(0, 0, ii.getIconWidth(), ii.getIconHeight());
                                //                            layeredPane.add(bgPanel);
                                //                            
                                //                            //背景图片，添加到背景Panel里面
                                //                            JLabel bgLabel = new JLabel(ii);
                                //                            bgPanel.add(bgLabel);
                                //                            

                                frame.setSize(ii.getIconWidth()*2, ii.getIconHeight()*2);
                                Graphics gg = frame.getGraphics();
                                gg.drawImage(ii.getImage(), 0, 0, null);
                                gg.dispose();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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




        final String username2 = username;
frame.addKeyListener(new KeyListener() {
            
            @Override
            public void keyTyped(KeyEvent e) {
                
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                
            }
            
            @Override
            public void keyPressed(KeyEvent e) {

                System.err.println(e.getKeyCode());
                System.out.println(e.getKeyChar()+"..."+KeyEvent.getKeyText(e.getKeyCode()));
                Message message = new Message();
                message.setVersion("1.0");
                message.setMsgCode(11);
                message.setReceiver(new String[] {receiver});
                message.setSender(username2);
                message.setEncoding(encoding);
                message.setSendTime(System.currentTimeMillis());
                message.setContent(e.getKeyCode()+"");
                try {
                    conn.sendMessage(EchoCoreUtils.messageToByteArr(message));
                } catch (Throwable e1) {
                    e1.printStackTrace();
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });
        frame.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseMoved(MouseEvent e) {

                Message message = new Message();
                message.setVersion("1.0");
                message.setMsgCode(4);
                message.setReceiver(new String[] {receiver});
                message.setSender(username2);
                message.setEncoding(encoding);
                message.setSendTime(System.currentTimeMillis());
                message.setContent((int)((e.getX()))+","+(int)((e.getY())));
                try {
                    conn.sendMessage(EchoCoreUtils.messageToByteArr(message));
                } catch (Throwable e1) {
                    e1.printStackTrace();
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            @Override
            public void mouseDragged(MouseEvent e) {

                Message message = new Message();
                message.setVersion("1.0");
                message.setMsgCode(4);
                message.setReceiver(new String[] {receiver});
                message.setSender(username2);
                message.setEncoding(encoding);
                message.setSendTime(System.currentTimeMillis());
                message.setContent((int)((e.getX()))+","+(int)((e.getY())));
                try {
                    conn.sendMessage(EchoCoreUtils.messageToByteArr(message));
                } catch (Throwable e1) {
                    e1.printStackTrace();
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

            }
        });
        frame.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
                System.err.println(e.getX()+"="+e.getY());
                if(e.getButton()==MouseEvent.BUTTON3){//右击键
                    Message message = new Message();
                    message.setVersion("1.0");
                    message.setMsgCode(8);
                    message.setReceiver(new String[] {receiver});
                    message.setSender(username2);
                    message.setEncoding(encoding);
                    message.setSendTime(System.currentTimeMillis());
                    message.setContent((int)((e.getX()))+","+(int)((e.getY())));
                    try {
                        conn.sendMessage(EchoCoreUtils.messageToByteArr(message));
                    } catch (Throwable e1) {
                        e1.printStackTrace();
                    }
                }else {

                    Message message = new Message();
                    message.setVersion("1.0");
                    message.setMsgCode(6);
                    message.setReceiver(new String[] {receiver});
                    message.setSender(username2);
                    message.setEncoding(encoding);
                    message.setSendTime(System.currentTimeMillis());
                    message.setContent((int)((e.getX()))+","+(int)((e.getY())));
                    try {
                        conn.sendMessage(EchoCoreUtils.messageToByteArr(message));
                    } catch (Throwable e1) {
                        e1.printStackTrace();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton()==MouseEvent.BUTTON3){//右击键
                    Message message = new Message();
                    message.setVersion("1.0");
                    message.setMsgCode(7);
                    message.setReceiver(new String[] {receiver});
                    message.setSender(username2);
                    message.setEncoding(encoding);
                    message.setSendTime(System.currentTimeMillis());
                    message.setContent((int)((e.getX()))+","+(int)((e.getY())));
                    try {
                        conn.sendMessage(EchoCoreUtils.messageToByteArr(message));
                    } catch (Throwable e1) {
                        e1.printStackTrace();
                    }
                }else {


                    Message message = new Message();
                    message.setVersion("1.0");
                    message.setMsgCode(5);
                    message.setReceiver(new String[] {receiver});
                    message.setSender(username2);
                    message.setEncoding(encoding);
                    message.setSendTime(System.currentTimeMillis());
                    message.setContent((int)((e.getX()))+","+(int)((e.getY())));
                    try {
                        conn.sendMessage(EchoCoreUtils.messageToByteArr(message));
                    } catch (Throwable e1) {
                        e1.printStackTrace();
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseClicked(MouseEvent e) {
                System.err.println(e.getX()+"="+e.getY());
                Message message = new Message();
                message.setVersion("1.0");
                message.setMsgCode(10);
                message.setReceiver(new String[] {receiver});
                message.setSender(username2);
                message.setEncoding(encoding);
                message.setSendTime(System.currentTimeMillis());
                message.setContent((int)((e.getX()))+","+(int)((e.getY())));
                try {
                    conn.sendMessage(EchoCoreUtils.messageToByteArr(message));
                } catch (Throwable e1) {
                    e1.printStackTrace();
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });
        frame.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int amount = e.getScrollAmount();
                if(e.getWheelRotation()==-1) {
                    amount = -amount;
                }

                Message message = new Message();
                message.setVersion("1.0");
                message.setMsgCode(9);
                message.setReceiver(new String[] {receiver});
                message.setSender(username2);
                message.setEncoding(encoding);
                message.setSendTime(System.currentTimeMillis());
                message.setContent(amount+"");
                try {
                    conn.sendMessage(EchoCoreUtils.messageToByteArr(message));
                } catch (Throwable e1) {
                    e1.printStackTrace();
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });
//        try {
//            while(true) {
//                Message message = new Message();
//                message.setVersion("1.0");
//                message.setMsgCode(EchoCode.MESSAGE_TRANS_ACTION);
//                message.setReceiver(new String[] {receiver});
//                message.setSender(username);
//                message.setEncoding(encoding);
//                message.setSendTime(System.currentTimeMillis());
//
//                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//                BufferedImage image = new Robot().createScreenCapture(new Rectangle(screenSize));
//                ByteArrayOutputStream out = new ByteArrayOutputStream();
//
//                Thumbnails.of(image).scale(0.8D).outputQuality(0.25f).outputFormat("jpg").toOutputStream(out);
//                out.flush();
//                message.setContent(Base64.encodeBase64String(out.toByteArray()));
//                conn.setSync(false);//设置异步请求
//                conn.sendMessage(EchoCoreUtils.messageToByteArr(message));//发送消息
//                Thread.sleep(20);
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
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
