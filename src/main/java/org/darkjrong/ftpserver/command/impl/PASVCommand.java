package org.darkjrong.ftpserver.command.impl;

import org.apache.ftpserver.command.impl.PASV;

/**
 *  这个命令请求服务器- dtp监听一个数据端口(这不是它的默认数据端口)，并等待连接，
 *  而不是在收到传输命令时启动连接。此命令的响应包括此服务器正在监听的主机和端口地址。
 * @author Ron.Jia
 * @date 2019/10/16 23:47:22
 */
public class PASVCommand extends PASV {
}
