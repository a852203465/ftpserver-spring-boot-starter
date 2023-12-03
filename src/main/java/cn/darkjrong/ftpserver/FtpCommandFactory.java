package cn.darkjrong.ftpserver;

import lombok.extern.slf4j.Slf4j;
import org.apache.ftpserver.command.Command;
import org.apache.ftpserver.command.CommandFactory;
import org.apache.ftpserver.command.CommandFactoryFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * ftp 工厂类
 *
 * @author Ron.Jia
 * @date 2019/10/16 23:47:22
 */
@Slf4j
@Component
public class FtpCommandFactory implements CommandFactory, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Command getCommand(String commandName) {
        return applicationContext.getBean(commandName, Command.class);
    }

    /**
     * 创建命令工厂
     *
     * @return {@link CommandFactory} 命令工厂
     */
    public CommandFactory createCommandFactory() {
        CommandFactoryFactory commandFactoryFactory = new CommandFactoryFactory();
        commandFactoryFactory.setUseDefaultCommands(false);
        Map<String, Command> commandMap = applicationContext.getBeansOfType(Command.class);
        commandFactoryFactory.setCommandMap(commandMap);
        return commandFactoryFactory.createCommandFactory();
    }


}
