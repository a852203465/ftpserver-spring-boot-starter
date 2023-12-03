package cn.darkjrong.ftpserver.command;

import lombok.extern.slf4j.Slf4j;
import org.apache.ftpserver.command.impl.SIZE;
import org.springframework.stereotype.Component;

/**
 * 返回文件的大小（以字节为单位）
 * @author Ron.Jia
 * @date 2019/10/16 23:47:22
 */
@Slf4j
@Component("SIZE")
public class SIZECommand extends SIZE {



}
