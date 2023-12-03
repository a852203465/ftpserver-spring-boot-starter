package cn.darkjrong.ftpserver.command;

import lombok.extern.slf4j.Slf4j;
import org.apache.ftpserver.command.impl.AUTH;
import org.springframework.stereotype.Component;

/**
 * 建立SSL加密的会话。仅支持SSL类型
 * @author Ron.Jia
 * @date 2019/10/16 23:47:22
 */
@Slf4j
@Component("AUTH")
public class AUTHCommand extends AUTH {



}
