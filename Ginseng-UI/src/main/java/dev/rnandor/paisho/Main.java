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

        Table t = new Table();
        int x = 8, y = 0;
        int mask = t.getType(x, y).get();
        log.debug("Position: ("+x+", "+y+")");
        log.debug("Red garden: "+Table.isRedGarden(mask));
        log.debug("White garden: "+Table.isWhiteGarden(mask));
        log.debug("Temple: "+Table.isTemple(mask));
        log.debug("");
        log.debug("North: "+Table.isFrom(mask, NORTHERN_TEMPLE));
        log.debug("South: "+Table.isFrom(mask, SOUTHERN_TEMPLE));
        log.debug("West: "+Table.isFrom(mask, WESTERN_TEMPLE));
        log.debug("East: "+Table.isFrom(mask, EASTERN_TEMPLE));

        var w = new GameWindow("hehe", 1000, 1000);
        w.add(new JPanel()  {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                System.out.println("a");
                g.setColor(Color.RED);
                g.drawRect(0, 0, 100, 100);
            }
        });
        System.out.println(System.currentTimeMillis()/1000);
        Thread.sleep(2000);
        w.show();
        w.setVisible(false);
        w.dispose();
        //w.repaint();
    }
}