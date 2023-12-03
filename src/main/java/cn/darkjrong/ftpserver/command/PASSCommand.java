package cn.darkjrong.ftpserver.command;

import lombok.extern.slf4j.Slf4j;
import org.apache.ftpserver.command.impl.PASS;
import org.springframework.stereotype.Component;


/**
 * 参数字段是Telnet字符串，用于指定用户的密码。此命令必须紧跟在USER命令之后
 * @author Ron.Jia
 * @date 2019/10/16 23:47:22
 */
@Slf4j
@Component("PASS")
public class PASSCommand extends PASS {







}
