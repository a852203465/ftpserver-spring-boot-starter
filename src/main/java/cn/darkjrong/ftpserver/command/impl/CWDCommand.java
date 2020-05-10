package cn.darkjrong.ftpserver.command.impl;

import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFileActionFtpReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 更改工作目录。如果未指定目录名称，则假定为根目录（/）
 * @author Ron.Jia
 * @date 2019/10/16 23:47:22
 */
public class CWDCommand extends AbstractCommand {

    private final Logger LOG = LoggerFactory.getLogger(CWDCommand.class);
    @Override
    public void execute(FtpIoSession session, FtpServerContext context, FtpRequest request) throws IOException, FtpException {
        // reset state variables
        session.resetState();

        // get new directory name
        String dirName = "/";

        // change directory
        FileSystemView fsview = session.getFileSystemView();
        boolean success = false;
        try {
            success = fsview.changeWorkingDirectory(dirName);
        } catch (Exception ex) {
            LOG.debug("Failed to change directory in file system", ex);
        }
        FtpFile cwd = fsview.getWorkingDirectory();
        if (success) {
            dirName = cwd.getAbsolutePath();
            session.write(LocalizedFileActionFtpReply.translate(session, request, context,
                    FtpReply.REPLY_250_REQUESTED_FILE_ACTION_OKAY, "CWD",
                    dirName, cwd));
        } else {
            session.write(LocalizedFileActionFtpReply.translate(session, request, context,
                            FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
                            "CWD", null, cwd));
        }
    }

}
