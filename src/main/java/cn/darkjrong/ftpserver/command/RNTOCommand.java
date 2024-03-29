package cn.darkjrong.ftpserver.command;

import lombok.extern.slf4j.Slf4j;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedRenameFtpReply;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 该命令指定在紧接的RNFR命令中指定的文件的新路径名。这两个命令共同导致文件被重命名
 * @author Ron.Jia
 * @date 2019/10/16 23:47:22
 */
@Slf4j
@Component("RNTO")
public class RNTOCommand extends AbstractCommand {

    @Override
    public void execute(FtpIoSession session, FtpServerContext context, FtpRequest request) throws IOException, FtpException {
        try {

            FtpFile frFile = session.getRenameFrom();
            if (frFile == null) {
                session.write(LocalizedRenameFtpReply.translate(session, request, context, 503, "RNTO", (String) null, (FtpFile) null, (FtpFile) null));
                return;
            }

            String toFileStr = frFile.getAbsolutePath().replace(".tmp", ".jpg");
            FtpFile toFile = null;

            try {
                toFile = session.getFileSystemView().getFile(toFileStr);
            } catch (Exception e) {
                log.debug("Exception getting file object", e);
            }

            if (toFile == null) {
                session.write(LocalizedRenameFtpReply.translate(session, request, context, 553, "RNTO.invalid", (String) null, frFile, toFile));
                return;
            }

            toFileStr = toFile.getAbsolutePath();
            if (toFile.isWritable()) {
                if (!frFile.doesExist()) {
                    session.write(LocalizedRenameFtpReply.translate(session, request, context, 553, "RNTO.missing", (String) null, frFile, toFile));
                    return;
                }

                String logFrFileAbsolutePath = frFile.getAbsolutePath();
                if (frFile.move(toFile)) {
                    session.write(LocalizedRenameFtpReply.translate(session, request, context, 250, "RNTO", toFileStr, frFile, toFile));
                    log.info("File rename from \"{}\" to \"{}\"", logFrFileAbsolutePath, toFile.getAbsolutePath());
                } else {
                    session.write(LocalizedRenameFtpReply.translate(session, request, context, 553, "RNTO", toFileStr, frFile, toFile));
                }
                return;
            }

            session.write(LocalizedRenameFtpReply.translate(session, request, context, 553, "RNTO.permission", (String) null, frFile, toFile));
        } finally {
            session.resetState();
        }
    }
}
