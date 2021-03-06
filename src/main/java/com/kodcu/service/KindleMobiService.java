package com.kodcu.service;

import com.kodcu.controller.AsciiDocController;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zeroturnaround.exec.ProcessExecutor;

import java.io.File;
import java.nio.file.Path;

/**
 * Created by usta on 02.09.2014.
 */
@Component
public class KindleMobiService {

    private static final Logger logger = LoggerFactory.getLogger(KindleMobiService.class);

    @Autowired
    private AsciiDocController asciiDocController;

    @Autowired
    private IndikatorService indikatorService;

    public void produceMobi(Path currentPath, String kindlegenDir) {

        try {
            indikatorService.startCycle();
            ProcessExecutor processExecutor = new ProcessExecutor();
            processExecutor.readOutput(true);
            processExecutor.directory(new File(kindlegenDir));
            String message = processExecutor
                    .command("kindlegen", currentPath.resolve("book.epub").toString())
                    .execute()
                    .outputUTF8();
            logger.debug(message);
            indikatorService.completeCycle();

            Platform.runLater(() -> {
                asciiDocController.getRecentFiles().remove(currentPath.resolve("book.mobi").toString());
                asciiDocController.getRecentFiles().add(0, currentPath.resolve("book.mobi").toString());
            });

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        } finally {
            indikatorService.hideIndikator();
        }
    }
}
