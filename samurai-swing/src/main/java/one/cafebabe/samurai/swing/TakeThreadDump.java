/*
 * Copyright 2003-2015 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package one.cafebabe.samurai.swing;

import com.sun.tools.attach.AttachNotSupportedException;
import one.cafebabe.samurai.remotedump.ProcessUtil;
import one.cafebabe.samurai.remotedump.VM;
import one.cafebabe.samurai.remotedump.VirtualMachineUtil;
import one.cafebabe.samurai.util.Configuration;
import one.cafebabe.samurai.util.GUIResourceBundle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.jvmstat.monitor.MonitorException;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TakeThreadDump {
    private static final Logger logger = LogManager.getLogger();

    private static final GUIResourceBundle resources = GUIResourceBundle.getInstance();

    private final FileHistory fileHistory;
    JMenu takeThreadDumpMenu;

    public TakeThreadDump(Configuration config, FileHistory fileHistory, JMenu takeThreadDumpMenu) {
        config.apply(this);
        this.takeThreadDumpMenu = takeThreadDumpMenu;
        this.fileHistory = fileHistory;
        takeThreadDumpMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                updateChildMenuItems();
            }

            @Override
            public void menuDeselected(MenuEvent e) {

            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }
        });
    }

    public JMenu getTakeThreadDumpMenu() {
        return this.takeThreadDumpMenu;
    }

    private void updateChildMenuItems() {
        try {
            List<VM> currentVms = ProcessUtil.getVMs("localhost");

            for (int i = 0; i < takeThreadDumpMenu.getItemCount(); i++) {
                LocalProcessMenuItem item = (LocalProcessMenuItem) takeThreadDumpMenu.getItem(i);
                boolean found = false;
                for (VM vm : currentVms) {
                    if (item.getVm().pid == vm.pid) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    takeThreadDumpMenu.remove(item);
                }

            }

            for (VM vm : currentVms) {
                boolean found = false;
                for (int i = 0; i < takeThreadDumpMenu.getItemCount(); i++) {
                    LocalProcessMenuItem item = (LocalProcessMenuItem) takeThreadDumpMenu.getItem(i);
                    if (item.getVm().pid == vm.pid) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    JMenuItem localProcess = new LocalProcessMenuItem(vm);
                    localProcess.setToolTipText(vm.fullCommandLine);
                    takeThreadDumpMenu.add(localProcess);
                }
            }
        } catch (URISyntaxException | MonitorException e) {
            logger.warn("unable to monitor", e);
            e.printStackTrace();
        }
    }

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    ExecutorService executor = Executors.newFixedThreadPool(1);

    class LocalProcessMenuItem extends JMenuItem {
        private static final long serialVersionUID = 2629440395264682598L;
        final VM vm;

        public LocalProcessMenuItem(VM vm) {
            super(vm.toLabel());
            this.vm = vm;
            addActionListener(e -> executor.execute(() -> {
                try {
                    Path path = Paths.get(System.getProperty("user.home"),
                            String.format("%s-%d-%s.txt", vm.fqcn, vm.pid, LocalDateTime.now().format(dateTimeFormatter)));
                    Files.writeString(path, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                    Files.writeString(path, "pid: " + vm.pid + "\n\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                    Files.writeString(path, "FQCN: " + vm.fqcn + "\n\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                    Files.writeString(path, String.format("Command line:\n%s\n\n", vm.fullCommandLine), StandardOpenOption.CREATE, StandardOpenOption.APPEND);

                    fileHistory.open(path.toFile());

                    for (int i = 0; i < 3; i++) {
                        Files.write(path, VirtualMachineUtil.getThreadDump(vm.pid), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException | AttachNotSupportedException | IOException e1) {
                    logger.warn("failed to attach pid[{}]",vm.pid, e1);
                }
            }));
        }

        public VM getVm() {
            return vm;
        }

    }
}
