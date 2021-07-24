package com.github.darkjrong.ftpserver.command.impl;

import org.apache.ftpserver.command.impl.PORT;

/**
 * 该参数是用于数据连接的数据端口的HOST-PORT规范。
 * 用户和服务器数据端口都有默认值，在正常情况下，不需要此命令及其答复。
 * 如果使用此命令，则参数是32位Internet主机地址和16位TCP端口地址的串联。
 * 该地址信息分为8位字段，每个字段的值以十进制数（以字符串表示）传输。字
 * 段之间用逗号分隔。端口命令为：端口h1，h2，h3，h4，p1，p2
 * @author Ron.Jia
 * @date 2019/10/16 23:47:22
 */
public class PORTCommand extends PORT {
}
