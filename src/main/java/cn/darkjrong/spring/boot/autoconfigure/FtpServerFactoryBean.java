package cn.darkjrong.spring.boot.autoconfigure;

import cn.darkjrong.ftpserver.command.FtpCommandFactory;
import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import cn.darkjrong.ftpserver.command.impl.BaseCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
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
public class FtpServerFactoryBean implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(FtpServerFactoryBean.class);

    public static final String FTP_SERVER_HOME_DIR = System.getProperty("user.dir") +"/data"+ File.separator +"res";

    private FtpServer ftpServer;
    private FtpServerProperties properties;

    @Override
    public void destroy() throws Exception {
        if (this.ftpServer != null) {
            this.ftpServer.stop();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        Assert.notNull(this.properties, "'properties' must be not null");
        Assert.notNull(this.properties.getUsername(), "'username' must be not null");
        Assert.notNull(this.properties.getPassword(), "'password' must be not null");
        Assert.notNull(this.properties.getPort(), "port' must be not null");
        Assert.notNull(this.properties.getCallback(), "'callback' must be not null");

        BaseCommand.callback = properties.getCallback();

        // 检查目录是否存在
        mkHomeDir(FTP_SERVER_HOME_DIR);

        FtpServerFactory serverFactory = new FtpServerFactory();

        // FTP服务连接配置
        ConnectionConfigFactory connectionConfigFactory = new ConnectionConfigFactory();
        connectionConfigFactory.setAnonymousLoginEnabled(false);
        connectionConfigFactory.setMaxLogins(Integer.parseInt(properties.getMaxLogin()));
        connectionConfigFactory.setMaxThreads(Integer.parseInt(properties.getMaxThreads()));
        serverFactory.setConnectionConfig(connectionConfigFactory.createConnectionConfig());

        ListenerFactory listenerFactory = new ListenerFactory();

        // 配置FTP端口  控制端口
        listenerFactory.setPort(Integer.parseInt(properties.getPort()));

        // 主动模式/被动模式配置
        DataConnectionConfigurationFactory dataConnectionConfFactory = new DataConnectionConfigurationFactory();
        dataConnectionConfFactory.setActiveEnabled(Boolean.TRUE);
        dataConnectionConfFactory.setActiveIpCheck(Boolean.TRUE);
        dataConnectionConfFactory.setActiveLocalAddress(properties.getHost());
        dataConnectionConfFactory.setActiveLocalPort(Integer.parseInt(properties.getActivePort()));
        dataConnectionConfFactory.setPassiveIpCheck(Boolean.TRUE);
        Optional.ofNullable(properties.getPassivePorts()).ifPresent(dataConnectionConfFactory::setPassivePorts);
        dataConnectionConfFactory.setPassiveExternalAddress(properties.getHost());
        listenerFactory.setDataConnectionConfiguration(dataConnectionConfFactory.createDataConnectionConfiguration());

        // 替换默认监听器
        serverFactory.addListener("default", listenerFactory.createListener());

        // 设置命令实现
        serverFactory.setCommandFactory(new FtpCommandFactory().createCommandFactory());

        // 设置用户控制中心
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        userManagerFactory.setAdminName(properties.getUsername());
        UserManager userManager = userManagerFactory.createUserManager();

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
            user.setMaxIdleTime(Integer.parseInt(properties.getMaxIdleTime()));
            user.setAuthorities(authorities);
            userManager.save(user);
        }

        serverFactory.setUserManager(userManager);

        // 创建并启动FTP服务
        ftpServer = serverFactory.createServer();

        ftpServer.start();
    }

    public void setProperties(FtpServerProperties properties) {
        this.properties = properties;
    }

    /**
     *  创建目录
     * @param homeDir 目录
     * @author Rong.Jia
     * @date 2019/10/17 00:27
     */
    public static void mkHomeDir(String homeDir) {
        try {
            Files.createDirectories(Paths.get(homeDir));
        } catch (IOException e) {
            logger.error("Directory creation failed {}", e.getMessage());
            throw new UncheckedIOException(e);
        }
    }

}
