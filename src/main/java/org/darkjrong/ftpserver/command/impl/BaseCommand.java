package org.darkjrong.ftpserver.command.impl;

import org.apache.ftpserver.command.AbstractCommand;
import org.darkjrong.ftpserver.callback.AlarmCallBack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 *  命令抽象类
 * @author Rong.Jia
 * @date 2019/10/17 09:49
 */
public abstract class BaseCommand extends AbstractCommand {

    private static final Logger logger = LoggerFactory.getLogger(BaseCommand.class);

    protected AlarmCallBack alarmCallBack;

    public static String callback = "";

    public BaseCommand() {

        Assert.notNull(callback, "'callback' must be not null");

        try {
            alarmCallBack = (AlarmCallBack)Class.forName(callback).newInstance();
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            logger.error("The callback registration failed  ", e);
        }

    }
}
