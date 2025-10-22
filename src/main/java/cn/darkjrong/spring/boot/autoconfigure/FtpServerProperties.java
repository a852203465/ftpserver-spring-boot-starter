package cn.darkjrong.spring.boot.autoconfigure;

import cn.darkjrong.ftpserver.constants.FtpServerConstant;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ftp Server 配置类
 * @author Rong.Jia
 * @date 2019/10/17 00:23
 */
@Data
@ConfigurationProperties(prefix = "ftp.server")
public class FtpServerProperties {

    /**
     * 是否开启 FTP Server
     */
    private boolean enabled = Boolean.FALSE;

    /**
     *  IP ,   Can't "localhost", "127.0.0.1"
     */
    private String host;

    /**
     * 控制端口, 默认 21
     */
    private Integer port = 21;

    /**
     *  主动模式连接端口 , 默认: 20
     */
    private Integer activePort = 20;

    /**
     *  被动模式连接端口范围
     */
    private String passivePorts;

    /**
     *  最大登录用户, 默认  10
     */
    private Integer maxLogin = 10;

    /**
     *  最大线程个数, 默认 10
     */
    private Integer maxThreads = 10;

    /**
     *  用户名
     */
    private String username;

    /**
     *  密码
     */
    private String password;

    /**
     *  最大空闲时间 ， 单位：秒，默认 300
     */
    private Integer maxIdleTime = 300;

    /**
     * user home directory,默认：System.getProperty("user.dir") +"/data/res"
     */
    private String homeDirectory;

    /**
     * SSL配置
     */
    private FtpSsl ssl = new FtpSsl();

    public String getHomeDirectory() {
        return StrUtil.isBlank(homeDirectory) ? FtpServerConstant.FTP_SERVER_HOME_DIR : StrUtil.removeSuffix(homeDirectory, "/");
    }

    /**
     * SSL配置
     */
    @Data
    public static class FtpSsl {

        /**
         * 是否开启SSL
         */
        private boolean enabled = Boolean.FALSE;

        /**
         * 算法
         */
//        private String algorithm = "RSA";

        /**
         * 秘钥密码
         */
//        private String keyPassword;

        /**
         *
         */
//        private String keyAlias;

        /**
         * 密钥库文件
         */
        private String keystoreFile;

        /**
         * 密钥库密码
         */
        private String keystorePassword;

        /**
         * 信任库文件
         */
        private String trustStoreFile;

        /**
         * 信任库密码
         */
        private String trustStorePassword;





    }









}
