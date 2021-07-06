package cn.darkjrong.ftpserver.command.impl;

import cn.darkjrong.ftpserver.callback.AlarmCallBack;
import cn.darkjrong.spring.boot.autoconfigure.FtpServerFactoryBean;
import cn.hutool.core.io.FileUtil;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.impl.*;
import org.apache.ftpserver.util.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * 将数据追加到远程主机上文件的末尾。如果该文件尚不存在，则会创建它。该命令之前必须有PORT或PASV命令
 * @author Ron.Jia
 * @date 2019/10/16 23:47:22
 */
public class APPECommand extends BaseCommand {

    private final Logger LOG = LoggerFactory.getLogger(APPECommand.class);

    public APPECommand(AlarmCallBack alarmCallBack) {
        super(alarmCallBack);
    }

    /**
     * Execute command.
     */
    @Override
    public void execute(final FtpIoSession session,
                        final FtpServerContext context, final FtpRequest request){

        try {

            // reset state variables
            session.resetState();

            // argument check
            String fileName = request.getArgument();
            if (fileName == null) {
                session
                        .write(LocalizedDataTransferFtpReply
                                .translate(
                                        session,
                                        request,
                                        context,
                                        FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS,
                                        "APPE", null, null));
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

            // get filenames
            FtpFile file = null;
            try {
                file = session.getFileSystemView().getFile(fileName);
            } catch (Exception e) {
                LOG.debug("File system threw exception", e);
            }
            if (file == null) {
                session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
                        FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
                        "APPE.invalid", fileName, null));
                return;
            }
            fileName = file.getAbsolutePath();

            // check file existance
            if (file.doesExist() && !file.isFile()) {
                session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
                        FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
                        "APPE.invalid", fileName, file));
                return;
            }

            // check permission
            if (!file.isWritable()) {
                session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
                        FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
                        "APPE.permission", fileName, file));
                return;
            }

            // get data connection
            session.write(LocalizedFtpReply.translate(session, request, context,
                    FtpReply.REPLY_150_FILE_STATUS_OKAY, "APPE", fileName));

            DataConnection dataConnection;
            try {
                dataConnection = session.getDataConnection().openConnection();
            } catch (Exception e) {
                LOG.debug("Exception when getting data input stream", e);
                session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
                        FtpReply.REPLY_425_CANT_OPEN_DATA_CONNECTION, "APPE",
                        fileName, file));
                return;
            }

            // get data from client
            boolean failure = false;
            OutputStream os = null;
            long transSz = 0L;
            try {

                // find offset
                long offset = 0L;
                if (file.doesExist()) {
                    offset = file.getSize();
                }

                // open streams
                os = file.createOutputStream(offset);

                // transfer data
                transSz = dataConnection.transferFromClient(session.getFtpletSession(), os);

                // attempt to close the output stream so that errors in
                // closing it will return an error to the client (FTPSERVER-119)
                if (os != null) {
                    os.close();
                }

                // notify the statistics component
                ServerFtpStatistics ftpStat = (ServerFtpStatistics) context
                        .getFtpStatistics();
                ftpStat.setUpload(session, file, transSz);

            } catch (SocketException e) {
                LOG.debug("SocketException during file upload", e);
                failure = true;
                session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
                        FtpReply.REPLY_426_CONNECTION_CLOSED_TRANSFER_ABORTED,
                        "APPE", fileName, file));
            } catch (IOException e) {
                LOG.debug("IOException during file upload", e);
                failure = true;
                session
                        .write(LocalizedDataTransferFtpReply
                                .translate(
                                        session,
                                        request,
                                        context,
                                        FtpReply.REPLY_551_REQUESTED_ACTION_ABORTED_PAGE_TYPE_UNKNOWN,
                                        "APPE", fileName, file));
            } finally {
                // make sure we really close the output stream
                IoUtils.close(os);

                //业务处理
                InetAddress address = ((InetSocketAddress) session.getRemoteAddress()).getAddress();

                super.sendFile(fileName, address);
            }

            // if data transfer ok - send transfer complete message
            if (!failure) {
                session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
                        FtpReply.REPLY_226_CLOSING_DATA_CONNECTION, "APPE",
                        fileName, file, transSz));
            }
        } finally {
            session.getDataConnection().closeDataConnection();
        }
    }
}
