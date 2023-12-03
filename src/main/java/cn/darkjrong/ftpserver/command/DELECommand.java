package cn.darkjrong.ftpserver.command;

import lombok.extern.slf4j.Slf4j;
import org.apache.ftpserver.command.impl.DELE;
import org.springframework.stereotype.Component;

/**
 * 删除提供的路径指定的文件
 * @author Ron.Jia
 * @date 2019/10/16 23:47:22
 */
@Slf4j
@Component("DELE")
public class DELECommand extends DELE {
}
