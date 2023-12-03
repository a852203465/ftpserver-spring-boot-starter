package cn.darkjrong.ftpserver.command;

import lombok.extern.slf4j.Slf4j;
import org.apache.ftpserver.command.impl.LIST;
import org.springframework.stereotype.Component;

/**
 * 此命令使列表从服务器发送到被动DTP。
 * 如果路径名指定目录或其他文件组，则服务器应在指定目录中传输文件列表。
 * 如果路径名指定了文件，则服务器应在该文件上发送当前信息。
 * null参数表示用户的当前工作目录或默认目录。数据传输是通过数据连接进行的。
 * 该命令之前必须有PORT或PASV命令
 * @author Ron.Jia
 * @date 2019/10/16 23:47:22
 */
@Slf4j
@Component("LIST")
public class LISTCommand extends LIST {












}
