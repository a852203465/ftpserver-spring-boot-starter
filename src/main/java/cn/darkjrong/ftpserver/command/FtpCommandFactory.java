package cn.darkjrong.ftpserver.command;

import cn.darkjrong.ftpserver.command.impl.*;
import org.darkjrong.ftpserver.command.impl.*;
import org.apache.ftpserver.command.Command;
import org.apache.ftpserver.command.CommandFactory;
import org.apache.ftpserver.command.CommandFactoryFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 *  ftp 工厂类
 * @author Ron.Jia
 * @date 2019/10/16 23:47:22
 */
public class FtpCommandFactory implements CommandFactory {

    private static final ConcurrentHashMap<String, Command> COMMAND_MAP = new ConcurrentHashMap<>();

    static {
        COMMAND_MAP.put("USER", new USERCommand());
        COMMAND_MAP.put("PASS", new PASSCommand());
        COMMAND_MAP.put("CWD", new CWDCommand());
        COMMAND_MAP.put("LIST", new LISTCommand());
        COMMAND_MAP.put("PASV", new PASVCommand());
        COMMAND_MAP.put("PWD", new PWDCommand());
        COMMAND_MAP.put("TYPE", new TYPECommand());
        COMMAND_MAP.put("QUIT", new QUITCommand());
        COMMAND_MAP.put("STOR", new STORCommand());
        COMMAND_MAP.put("DELE", new DELECommand());
        COMMAND_MAP.put("RNTO", new RNTOCommand());
        COMMAND_MAP.put("RNFR", new RNFRCommand());
        COMMAND_MAP.put("APPE", new APPECommand());
        COMMAND_MAP.put("MKD",new MKDCommand());
        COMMAND_MAP.put("AUTH",new AUTHCommand());
        COMMAND_MAP.put("SIZE",new SIZECommand());
        COMMAND_MAP.put("PORT",new PORTCommand());
    }

    public CommandFactory createCommandFactory() {
        CommandFactoryFactory commandFactoryFactory = new CommandFactoryFactory();
        commandFactoryFactory.setUseDefaultCommands(false);
        commandFactoryFactory.setCommandMap(COMMAND_MAP);
        return commandFactoryFactory.createCommandFactory();
    }

    @Override
    public Command getCommand(String commandName) {
        return COMMAND_MAP.get(commandName);
    }
}
