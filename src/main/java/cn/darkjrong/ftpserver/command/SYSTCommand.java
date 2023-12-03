package cn.darkjrong.ftpserver.command;

import lombok.extern.slf4j.Slf4j;
import org.apache.ftpserver.command.impl.SYST;
import org.springframework.stereotype.Component;

/**
 * 查找服务器上的操作系统类型
 *
 * @author Rong.Jia
 * @date 2022/01/07
 */
@Slf4j
@Component("SYST")
public class SYSTCommand extends SYST {



}
