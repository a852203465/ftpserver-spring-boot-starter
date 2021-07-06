# ftpserver-spring-boot-starter 
FTP Server 启动包 

#使用方式
自己下载install引入使用

```
<dependency>
    <groupId>cn.darkjrong</groupId>
    <artifactId>ftpserver-spring-boot-starter</artifactId>
    <version>1.0</version>
</dependency>
```

yml配置，必须配置enabled: true，否则默认false不起作用
```yml
ftp:
    host: 192.168.205.105
    port: 21
    enabled: true
    activePort: 20
    passivePorts: 6000-6008
    username: admin
    password: 123456
```

文件接收，实现AlarmCallBack 
```java
@Slf4j
@Component
public class AlarmCallBackImpl implements AlarmCallBack {

    @Override
    public void invoke(File file, String s) {

        log.info("invoke file {}, host {}", file.getName(), s);
    }
}
```
































