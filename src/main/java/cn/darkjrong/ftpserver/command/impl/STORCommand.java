package cn.darkjrong.ftpserver.command.impl;

import cn.darkjrong.ftpserver.callback.AlarmCallBack;
import cn.hutool.core.io.FileUtil;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.impl.*;
import org.apache.ftpserver.util.IoUtils;
import cn.darkjrong.spring.boot.autoconfigure.FtpServerFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;


/**
 * 此命令使服务器DTP接受通过数据连接传输的数据，并将数据作为文件存储在服务器站点上。
 * 如果路径名中指定的文件在服务器站点上存在，则其内容应由正在传输的数据替换。
 * 如果路径名中指定的文件不存在，则会在服务器站点上创建一个新文件
 * @author Ron.Jia
 * @date 2019/10/16 23:47:22
 */
public class STORCommand extends BaseCommand {

    private final Logger log = LoggerFactory.getLogger(STORCommand.class);

    public STORCommand(AlarmCallBack alarmCallBack) {
        super(alarmCallBack);
    }

    @Override
    public void execute(final FtpIoSession session,
                        final FtpServerContext context, final FtpRequest request) {

        try {

            // argument check
            String fileName = request.getArgument();
            if (fileName == null) {
                session.write(LocalizedDataTransferFtpReply
                        .translate(
                                session,
                                request,
                                context,
                                FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS,
                                "STOR", null, null));
                return;
            }
            // 24-10-2007 - added check if PORT or PASV is issued, see
            // https://issues.apache.org/jira/browse/FTPSERVER-110
            DataConnectionFactory connFactory = session.getDataConnection();
            if (connFactory instanceof IODataConnectionFactory) {
                InetAddress address = ((IODataConnectionFactory) connFactory)
                        .getInetAddress();
                if (address == null) {
                    session.write(new DefaultFtpReply(
                            FtpReply.REPLY_503_BAD_SEQUENCE_OF_COMMANDS,
                            "PORT or PASV must be issued first"));
                    return;
                }

            }

            // get filename
            FtpFile file = null;
            try {
                file = session.getFileSystemView().getFile(fileName);
            } catch (Exception ex) {
                log.debug("Exception getting file object", ex);
            }
            if (file == null) {
                session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
                        FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
                        "STOR.invalid", fileName, file));
                return;
            }
            fileName = file.getAbsolutePath();

            // get permission
            if (!file.isWritable()) {
                session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
                        FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
                        "STOR.permission", fileName, file));
                return;
            }

            // get data connection
            session.write(
                    LocalizedFtpReply.translate(session, request, context,
                            FtpReply.REPLY_150_FILE_STATUS_OKAY, "STOR",
                            fileName)).awaitUninterruptibly(10000);

            IODataConnection dataConnection;
            try {
                dataConnection = (IODataConnection) session.getDataConnection().openConnection();
            } catch (Exception e) {
                log.error("Exception getting the input data stream", e);
                session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
                        FtpReply.REPLY_425_CANT_OPEN_DATA_CONNECTION, "STOR",
                        fileName, file));
                return;
            }
            boolean failure = false;
            long transSz = 0L;
            OutputStream outStream = null;
            try {
                outStream = file.createOutputStream(0);
                transSz = dataConnection.transferFromClient(session.getFtpletSession(), outStream);
                if (outStream != null) {
                    outStream.close();
                }

                ServerFtpStatistics ftpStat = (ServerFtpStatistics) context.getFtpStatistics();
                ftpStat.setUpload(session, file, transSz);
            } catch (SocketException var29) {
                log.debug("Socket exception during data transfer", var29);
                failure = true;
                session.write(LocalizedDataTransferFtpReply.translate(session, request, context, 426, "STOR", fileName, file));
            } catch (IOException var30) {
                log.debug("IOException during data transfer", var30);
                failure = true;
                session.write(LocalizedDataTransferFtpReply.translate(session, request, context, 551, "STOR", fileName, file));
            } finally {
                IoUtils.close(outStream);
                //业务处理
                InetAddress address = ((InetSocketAddress) session.getRemoteAddress()).getAddress();

                super.sendFile(fileName, address);
            }

            // if data transfer ok - send transfer complete message
            if (!failure) {
                session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
                        FtpReply.REPLY_226_CLOSING_DATA_CONNECTION, "STOR",
                        fileName, file, transSz));
            }
        } finally {
            session.resetState();
            session.getDataConnection().closeDataConnection();
        }
    }
}
