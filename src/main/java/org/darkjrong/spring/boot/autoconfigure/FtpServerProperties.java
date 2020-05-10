package org.darkjrong.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ftp Server 配置类
 * @author Rong.Jia
 * @date 2019/10/17 00:23
 */
@ConfigurationProperties(prefix = "ftp")
public class FtpServerProperties {

    private static final String ENABLED = "false";

    /**
     *  IP ,   Can't "localhost", "127.0.0.1"
     */
    private String host;

    /**
     * 控制端口, 默认 21
     */
    private String port;

    /**
     *  主动模式连接端口 ,   默认: 20
     */
    private String activePort;

    /**
     *  被动模式连接端口范围
     */
    private String passivePorts;

    /**
     *  最大登录用户, 默认  10
     */
    private String maxLogin;

    /**
     *  最大线程个数, 默认 10
     */
    private String maxThreads;

    /**
     *  用户名
     */
    private String username;

    /**
     *  密码
     */
    private String password;

    /**
     *  回调函数实现类完整路径
     */
    private String callback;

    /**
     * 是否开启 FTP Server
     */
    private String enabled = ENABLED;

    /**
     *  最大空闲时间 ， 单位：秒，默认 300
     */
    private String maxIdleTime;

    public String getMaxIdleTime() {
        return maxIdleTime == null ? String.valueOf(300) : maxIdleTime;
    }

    public void setMaxIdleTime(String maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getMaxLogin() {
        return maxLogin == null ? String.valueOf(10) : maxLogin;
    }

    public String getMaxThreads() {
        return maxThreads == null ? String.valueOf(10) : maxThreads;
    }

    public String getActivePort() {
        return activePort == null ? String.valueOf(20) : activePort;
    }

    public void setActivePort(String activePort) {
        this.activePort = activePort;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port == null ? String.valueOf(21) : port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPassivePorts() {
        return passivePorts;
    }

    public void setPassivePorts(String passivePorts) {
        this.passivePorts = passivePorts;
    }

    public void setMaxLogin(String maxLogin) {
        this.maxLogin = maxLogin;
    }

    public void setMaxThreads(String maxThreads) {
        this.maxThreads = maxThreads;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }



}
