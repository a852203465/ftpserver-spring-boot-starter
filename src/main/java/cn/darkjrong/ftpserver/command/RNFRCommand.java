package cn.darkjrong.ftpserver.command;

import lombok.extern.slf4j.Slf4j;
import org.apache.ftpserver.command.impl.RNFR;
import org.springframework.stereotype.Component;

/**
 * 此命令指定要重命名的文件的旧路径名。此命令后必须紧跟一个RNTO命令，以指定新的文件路径名
 * @author Ron.Jia
 * @date 2019/10/16 23:47:22
 */
@Slf4j
@Component("RNFR")
public class RNFRCommand extends RNFR {












}
