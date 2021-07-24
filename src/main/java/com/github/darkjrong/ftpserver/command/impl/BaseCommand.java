package com.github.darkjrong.ftpserver.command.impl;

import com.github.darkjrong.ftpserver.callback.AlarmCallBack;
import com.github.darkjrong.spring.boot.autoconfigure.FtpServerFactoryBean;
import org.apache.ftpserver.command.AbstractCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;

/**
 *  命令抽象类
 * @author Rong.Jia
 * @date 2019/10/17 09:49
 */
public abstract class BaseCommand extends AbstractCommand {

    private static final Logger logger = LoggerFactory.getLogger(BaseCommand.class);

    AlarmCallBack alarmCallBack;

    public BaseCommand(AlarmCallBack alarmCallBack) {
        this.alarmCallBack = alarmCallBack;
    }

    /**
     * 发送文件
     *
     * @param fileName 文件名称
     * @param address  地址
     */
    void sendFile(String fileName, InetAddress address) {

        File file1 = new File(FtpServerFactoryBean.FTP_SERVER_HOME_DIR + File.separator + fileName);
        alarmCallBack.invoke(file1, address.getHostAddress());

    }


}
