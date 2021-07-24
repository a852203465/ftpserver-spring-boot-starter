package com.github.darkjrong.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ftp Server 配置类
 * @author Rong.Jia
 * @date 2019/10/17 00:23
 */
@ConfigurationProperties(prefix = "ftp")
public class FtpServerProperties {

    /**
     *  IP ,   Can't "localhost", "127.0.0.1"
     */
    private String host;

    /**
     * 控制端口, 默认 21
     */
    private Integer port;

    /**
     *  主动模式连接端口 ,   默认: 20
     */
    private Integer activePort;

    /**
     *  被动模式连接端口范围
     */
    private String passivePorts;

    /**
     *  最大登录用户, 默认  10
     */
    private Integer maxLogin;

    /**
     *  最大线程个数, 默认 10
     */
    private Integer maxThreads;

    /**
     *  用户名
     */
    private String username;

    /**
     *  密码
     */
    private String password;

    /**
     * 是否开启 FTP Server
     */
    private Boolean enabled = Boolean.FALSE;

    /**
     *  最大空闲时间 ， 单位：秒，默认 300
     */
    private Integer maxIdleTime;

    public Integer getMaxIdleTime() {
        return maxIdleTime == null ? 300 : maxIdleTime;
    }

    public void setMaxIdleTime(Integer maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }



    public Integer getMaxLogin() {
        return maxLogin == null ? 10 : maxLogin;
    }

    public Integer getMaxThreads() {
        return maxThreads == null ? 10 : maxThreads;
    }

    public Integer getActivePort() {
        return activePort == null ? 20 : activePort;
    }

    public void setActivePort(Integer activePort) {
        this.activePort = activePort;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port == null ? 21 : port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getPassivePorts() {
        return passivePorts;
    }

    public void setPassivePorts(String passivePorts) {
        this.passivePorts = passivePorts;
    }

    public void setMaxLogin(Integer maxLogin) {
        this.maxLogin = maxLogin;
    }

    public void setMaxThreads(Integer maxThreads) {
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


}
