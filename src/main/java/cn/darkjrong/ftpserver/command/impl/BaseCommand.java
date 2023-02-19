package cn.darkjrong.ftpserver.command.impl;

import cn.darkjrong.ftpserver.callback.AlarmCallBack;
import cn.darkjrong.ftpserver.constants.FtpConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.ftpserver.command.AbstractCommand;

import java.io.File;
import java.net.InetAddress;

/**
 *  命令抽象类
 * @author Rong.Jia
 * @date 2019/10/17 09:49
 */
@Slf4j
public abstract class BaseCommand extends AbstractCommand {

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
        File file1 = new File(FtpConstant.FTP_SERVER_HOME_DIR + File.separator + fileName);
        alarmCallBack.invoke(file1, address.getHostAddress());

    }


}
