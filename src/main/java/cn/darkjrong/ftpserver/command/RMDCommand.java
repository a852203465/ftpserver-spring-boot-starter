package cn.darkjrong.ftpserver.command;

import lombok.extern.slf4j.Slf4j;
import org.apache.ftpserver.command.impl.RMD;
import org.springframework.stereotype.Component;

/**
 * 这个命令导致在路径名中指定的目录作为一个目录(如果路径名是绝对的)
 * 或作为当前工作目录的子目录(如果路径名是相对的)被删除。
 *
 * @author Rong.Jia
 * @date 2022/01/07
 */
@Slf4j
@Component("RMD")
public class RMDCommand extends RMD {


}
