package dev.rnandor.paisho;

import dev.rnandor.paisho.ui.GameWindow;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

import static dev.rnandor.paisho.Table.Locale.*;

@Slf4j
public class Main {

    public static void main(String[] args) throws InterruptedException {
        log.info("Starting Ginseng PaiSho UI...");

        try {
            var w = new GameWindow("Ginseng PaiSho", 1000, 700);
        } catch (Exception e) {
            log.error("Error while creating window:", e);
        }
    }
}