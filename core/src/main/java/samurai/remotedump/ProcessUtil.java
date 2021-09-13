/*
 * *
 *  Copyright 2015 Yusuke Yamamoto
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  Distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package samurai.remotedump;

import sun.jvmstat.monitor.*;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ProcessUtil {
    public static List<VM> getVMs(String host) throws URISyntaxException, MonitorException {
        int localPid = (int)ProcessHandle.current().pid();
        List<VM> vms = new ArrayList<>();
        HostIdentifier hi = new HostIdentifier(host);
        MonitoredHost mh = MonitoredHost.getMonitoredHost(hi);
        Set<Integer> jvms = mh.activeVms();
        for (Integer pidInteger : jvms) {
            try {
                int pid = pidInteger;
                // exclude local process from the list
                if (pid != localPid) {
                    MonitoredVm mv = mh.getMonitoredVm(new VmIdentifier("//" + pid + "?mode=r"), 0);
                    StringMonitor sm = (StringMonitor) mv.findByName("sun.rt.javaCommand");
                    String fullCommandLine = "";
                    if (sm != null) {
                        fullCommandLine = sm.stringValue();
                    }
                    vms.add(new VM(pid, fullCommandLine));
                }
            } catch (MonitorException me) {
                // target process is no longer available
            }
        }
        return Collections.unmodifiableList(vms);
    }

    public static void main(String... args) throws URISyntaxException, MonitorException {
        List<VM> vms = getVMs("localhost");
        vms.forEach(e -> System.out.printf("%s %s%n", e.getPid(), e.getFqcn()));
    }

}