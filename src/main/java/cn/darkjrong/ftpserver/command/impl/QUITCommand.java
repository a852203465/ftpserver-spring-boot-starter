package cn.darkjrong.ftpserver.command.impl;

import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFtpReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * 关闭连接
 * @author Ron.Jia
 * @date 2019/10/16 23:47:22
 */
public class QUITCommand extends AbstractCommand {

    private final Logger LOG = LoggerFactory.getLogger(QUITCommand.class);

    /**
     * Execute command
     */
    @Override
    public void execute(final FtpIoSession session,
                        final FtpServerContext context, final FtpRequest request)
            throws IOException {
        session.resetState();
        //业务处理
        InetAddress testAddress = ((InetSocketAddress) session.getRemoteAddress()).getAddress();
        session.write(LocalizedFtpReply.translate(session, request, context,
                FtpReply.REPLY_221_CLOSING_CONTROL_CONNECTION, "QUIT", null));

        session.close(false).awaitUninterruptibly(10000);
        session.getDataConnection().closeDataConnection();
    }
}
