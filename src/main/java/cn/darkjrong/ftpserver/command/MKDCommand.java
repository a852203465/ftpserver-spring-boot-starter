package cn.darkjrong.ftpserver.command;

import lombok.extern.slf4j.Slf4j;
import org.apache.ftpserver.command.impl.MKD;
import org.springframework.stereotype.Component;

/**
 * 此命令导致在路径名中指定的目录被创建为目录（如果路径名是绝对的）或当前工作目录的子目录（如果路径名是相对的）
 * @author Ron.Jia
 * @date 2019/10/16 23:47:22
 */
@Slf4j
@Component("MKD")
public class MKDCommand extends MKD {









}
