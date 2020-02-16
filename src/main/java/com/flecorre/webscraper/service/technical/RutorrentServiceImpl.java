package com.flecorre.webscraper.service.technical;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class RutorrentServiceImpl implements RutorrentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RutorrentServiceImpl.class);

    @Override
    public boolean sendToRutorrent(File file) {
        boolean isUploaded = false;
        String server = "nereus.bysh.me";
        int port = 21;
        String user = "";
        String pass = "";

        FTPClient ftpClient = new FTPClient();

        try {
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.changeWorkingDirectory("/torrents/watch/");
            String firstRemoteFile = file.getName();
            InputStream inputStream = new FileInputStream(file);

            LOGGER.info("RUTORRENT: Start uploading first file");
            isUploaded = ftpClient.storeFile(firstRemoteFile, inputStream);
            inputStream.close();
            if (isUploaded) {
                file.delete();
                LOGGER.info("RUTORRENT: {} has been uploaded successfully", file.getName());

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return isUploaded;
    }

}
