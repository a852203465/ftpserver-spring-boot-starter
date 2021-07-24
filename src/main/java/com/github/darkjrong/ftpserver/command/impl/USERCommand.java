
package com.github.darkjrong.ftpserver.command.impl;

import org.apache.ftpserver.command.impl.USER;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFtpReply;
import org.apache.ftpserver.impl.ServerFtpStatistics;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginRequest;
import org.apache.mina.filter.logging.MdcInjectionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * 参数字段是用于标识用户的Telnet字符串。
 * 用户标识是服务器访问其文件系统所需的标识。
 * 在建立控制连接后，该命令通常是用户发送的第一条命令
 * @author Ron.Jia
 * @date 2019/10/16 23:47:22
 */
public class USERCommand extends USER {

    private final Logger LOG = LoggerFactory.getLogger(USERCommand.class);

    /**
     * Execute command.
     */
    @Override
    public void execute(final FtpIoSession session,
                        final FtpServerContext context, final FtpRequest request)
            throws IOException, FtpException {
        //以下源码
        boolean success = false;
        ServerFtpStatistics stat = (ServerFtpStatistics) context
                .getFtpStatistics();
        try {

            // reset state variables
            session.resetState();

            // argument check
            String userName = request.getArgument();
            if (userName == null) {
                session
                        .write(LocalizedFtpReply
                                .translate(
                                        session,
                                        request,
                                        context,
                                        FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS,
                                        "USER", null));
                return;
            }

            // Add to the MDC logging
            MdcInjectionFilter.setProperty(session, "userName", userName);

            // already logged-in
            User user = session.getUser();
            if (session.isLoggedIn()) {
                if (userName.equals(user.getName())) {
                    session.write(LocalizedFtpReply.translate(session, request,
                            context, FtpReply.REPLY_230_USER_LOGGED_IN, "USER",
                            null));
                    success = true;
                } else {
                    session.write(LocalizedFtpReply.translate(session, request,
                            context, 530, "USER.invalid", null));
                }
                return;
            }

            // anonymous login is not enabled
            boolean anonymous = "anonymous".equals(userName);
            if (anonymous
                    && (!context.getConnectionConfig()
                    .isAnonymousLoginEnabled())) {
                session.write(LocalizedFtpReply.translate(session, request, context,
                        FtpReply.REPLY_530_NOT_LOGGED_IN, "USER.anonymous",
                        null));
                return;
            }

            // anonymous login limit check
            int currAnonLogin = stat.getCurrentAnonymousLoginNumber();
            int maxAnonLogin = context.getConnectionConfig()
                    .getMaxAnonymousLogins();
            if (maxAnonLogin == 0) {
                LOG.debug("Currently {} anonymous users logged in, unlimited allowed", currAnonLogin);
            } else {
                LOG.debug("Currently {} out of {} anonymous users logged in", currAnonLogin, maxAnonLogin);
            }
            if (anonymous && (currAnonLogin >= maxAnonLogin)) {
                LOG.debug("Too many anonymous users logged in, user will be disconnected");

                session
                        .write(LocalizedFtpReply
                                .translate(
                                        session,
                                        request,
                                        context,
                                        FtpReply.REPLY_421_SERVICE_NOT_AVAILABLE_CLOSING_CONTROL_CONNECTION,
                                        "USER.anonymous", null));
                return;
            }

            // login limit check
            int currLogin = stat.getCurrentLoginNumber();
            int maxLogin = context.getConnectionConfig().getMaxLogins();

            if (maxLogin == 0) {
                LOG.debug("Currently {} users logged in, unlimited allowed", currLogin);
            } else {
                LOG.debug("Currently {} out of {} users logged in", currLogin, maxLogin);
            }

            if (maxLogin != 0 && currLogin >= maxLogin) {
                LOG.debug("Too many users logged in, user will be disconnected");

                session
                        .write(LocalizedFtpReply
                                .translate(
                                        session,
                                        request,
                                        context,
                                        FtpReply.REPLY_421_SERVICE_NOT_AVAILABLE_CLOSING_CONTROL_CONNECTION,
                                        "USER.login", null));
                return;
            }

            User configUser = context.getUserManager().getUserByName(userName);
            if (configUser != null) {
                // user login limit check

                InetAddress address = null;
                if (session.getRemoteAddress() instanceof InetSocketAddress) {
                    address = ((InetSocketAddress) session.getRemoteAddress())
                            .getAddress();
                }

                ConcurrentLoginRequest loginRequest = new ConcurrentLoginRequest(
                        stat.getCurrentUserLoginNumber(configUser) + 1,
                        stat.getCurrentUserLoginNumber(configUser, address) + 1);

                if (configUser.authorize(loginRequest) == null) {
                    LOG.debug("User logged in too many sessions, user will be disconnected");
                    session
                            .write(LocalizedFtpReply
                                    .translate(
                                            session,
                                            request,
                                            context,
                                            FtpReply.REPLY_421_SERVICE_NOT_AVAILABLE_CLOSING_CONTROL_CONNECTION,
                                            "USER.login", null));
                    return;
                }
            }

            // finally set the user name
            success = true;
            session.setUserArgument(userName);
            if (anonymous) {
                session.write(LocalizedFtpReply.translate(session, request, context,
                        FtpReply.REPLY_331_USER_NAME_OKAY_NEED_PASSWORD,
                        "USER.anonymous", userName));
            } else {
                session.write(LocalizedFtpReply.translate(session, request, context,
                        FtpReply.REPLY_331_USER_NAME_OKAY_NEED_PASSWORD,
                        "USER", userName));
            }
        } finally {

            // if not ok - close connection
            if (!success) {
                LOG.debug("User failed to login, session will be closed");
                session.close(false).awaitUninterruptibly(10000);
            }
        }
    }

}



