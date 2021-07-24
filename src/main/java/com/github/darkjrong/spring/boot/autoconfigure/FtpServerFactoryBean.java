package com.github.darkjrong.spring.boot.autoconfigure;

import com.github.darkjrong.ftpserver.callback.AlarmCallBack;
import com.github.darkjrong.ftpserver.command.FtpCommandFactory;
import org.apache.ftpserver.*;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ftp Server工厂类
 * @author Rong.Jia
 * @date 2019/10/17 00:31
 */
public class FtpServerFactoryBean implements InitializingBean, DisposableBean, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(FtpServerFactoryBean.class);

    public static final String FTP_SERVER_HOME_DIR = System.getProperty("user.dir") +"/data"+ File.separator +"res";

    private FtpServer ftpServer;
    private FtpServerProperties properties;
    private ApplicationContext applicationContext;

    @Override
    public void destroy() {
        if (this.ftpServer != null) {
            this.ftpServer.stop();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setProperties(FtpServerProperties properties) {
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() {

        Assert.notNull(this.properties, "'properties' must be not null");
        Assert.notNull(this.properties.getUsername(), "'username' must be not null");
        Assert.notNull(this.properties.getPassword(), "'password' must be not null");
        Assert.notNull(this.properties.getPort(), "port' must be not null");

        // 检查目录是否存在
        mkHomeDir(FTP_SERVER_HOME_DIR);

        ftpServer = createServer();

        Optional.ofNullable(ftpServer).ifPresent(a -> {
            try {
                a.start();
            } catch (FtpException e) {
                logger.error("Exception to start FTP Server {}", e.getMessage());
            }
        });
    }

    /**
     * 创建回调
     *
     * @return {@link AlarmCallBack}  回调
     */
    private AlarmCallBack createCallback() {

        try {
            return applicationContext.getBean(AlarmCallBack.class);
        }catch (Exception e) {
            logger.error("createCallback {}" , e.getMessage());
            throw new NoSuchBeanDefinitionException(AlarmCallBack.class, e.getMessage());
        }
    }

    /**
     *  创建目录
     * @param homeDir 目录
     * @author Rong.Jia
     * @date 2019/10/17 00:27
     */
    private void mkHomeDir(String homeDir) {
        try {
            Files.createDirectories(Paths.get(homeDir));
        } catch (IOException e) {
            logger.error("Directory creation failed {}", e.getMessage());
        }
    }

    /**
     * 创建服务器
     *
     * @return {@link FtpServer} Ftp Server
     */
    private FtpServer createServer () {

        FtpServerFactory serverFactory = new FtpServerFactory();

        // FTP服务连接配置
        serverFactory.setConnectionConfig(createConnectionConfig());

        // 替换默认监听器
        serverFactory.addListener("default", createListener());

        // 设置命令实现
        serverFactory.setCommandFactory(FtpCommandFactory.createCommandFactory(createCallback()));

        // 设置用户控制中心
        UserManager userManager = createUserManager();

        try {

            boolean exist = userManager.doesExist(properties.getUsername());

            // need to init user
            if (!exist) {
                List<Authority> authorities = new ArrayList<>();
                authorities.add(new WritePermission());
                authorities.add(new ConcurrentLoginPermission(0, 0));
                BaseUser user = new BaseUser();
                user.setName(properties.getUsername());
                user.setPassword(properties.getPassword());
                user.setHomeDirectory(FTP_SERVER_HOME_DIR);
                user.setMaxIdleTime(properties.getMaxIdleTime());
                user.setAuthorities(authorities);
                userManager.save(user);
            }

            serverFactory.setUserManager(userManager);

            // 创建
            return serverFactory.createServer();
        }catch (Exception e) {
            logger.error("Create an FTP Server exception {} ", e.getMessage());
        }

        return null;
    }

    /**
     * 创建连接配置
     *
     * @return {@link ConnectionConfig} 配置信息
     */
    private ConnectionConfig createConnectionConfig() {

        ConnectionConfigFactory connectionConfigFactory = new ConnectionConfigFactory();
        connectionConfigFactory.setAnonymousLoginEnabled(false);
        connectionConfigFactory.setMaxLogins(properties.getMaxLogin());
        connectionConfigFactory.setMaxThreads(properties.getMaxThreads());

        return connectionConfigFactory.createConnectionConfig();
    }

    /**
     * 创建数据连接配置
     *
     * @return {@link DataConnectionConfiguration} 数据连接配置
     */
    private  DataConnectionConfiguration createDataConnectionConfiguration() {

        //主动模式/被动模式配置
        DataConnectionConfigurationFactory dataConnectionConfFactory = new DataConnectionConfigurationFactory();
        dataConnectionConfFactory.setActiveEnabled(Boolean.TRUE);
        dataConnectionConfFactory.setActiveIpCheck(Boolean.TRUE);
        dataConnectionConfFactory.setActiveLocalAddress(properties.getHost());
        dataConnectionConfFactory.setActiveLocalPort(properties.getActivePort());
        dataConnectionConfFactory.setPassiveIpCheck(Boolean.TRUE);
        Optional.ofNullable(properties.getPassivePorts()).ifPresent(dataConnectionConfFactory::setPassivePorts);
        dataConnectionConfFactory.setPassiveExternalAddress(properties.getHost());

        return dataConnectionConfFactory.createDataConnectionConfiguration();
    }

    /**
     * 创建用户管理器
     *
     * @return {@link UserManager} 用户管理器
     */
    private UserManager createUserManager () {

        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        userManagerFactory.setAdminName(properties.getUsername());
        return userManagerFactory.createUserManager();
    }

    /**
     * 创建监听器
     *
     * @return {@link Listener} 监听器
     */
    private Listener createListener() {

        ListenerFactory listenerFactory = new ListenerFactory();

        // 配置FTP端口  控制端口
        listenerFactory.setPort(properties.getPort());

        // 主动模式/被动模式配置
        listenerFactory.setDataConnectionConfiguration(createDataConnectionConfiguration());

        return listenerFactory.createListener();
    }

}
