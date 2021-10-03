/*
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
package one.cafebabe.samurai.remotedump;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.jvmstat.monitor.*;

import java.net.URISyntaxException;
import java.util.*;

public class ProcessUtil {
    private static final Logger logger = LogManager.getLogger();

    public static List<VM> getVMs(String host) throws URISyntaxException, MonitorException {
        List<VM> vms = new ArrayList<>();
        HostIdentifier hi = new HostIdentifier(host);
        MonitoredHost mh = MonitoredHost.getMonitoredHost(hi);
        Set<Integer> jvms = mh.activeVms();
        for (Integer pidInteger : jvms) {
            try {
                int pid = pidInteger;
                MonitoredVm vm = mh.getMonitoredVm(new VmIdentifier("//" + pid + "?mode=r"), 0);
                List<Monitor> byPattern = vm.findByPattern(".*");
                Map<String, String> props = new HashMap<>();
                for (Monitor monitor : byPattern) {
                    if(monitor instanceof StringMonitor){
                        props.put(monitor.getName(),((StringMonitor)monitor).stringValue());
                    }else if(monitor instanceof IntegerMonitor){
                        props.put(monitor.getName(),String.valueOf(((IntegerMonitor)monitor).intValue()));
                    }
                }
                vms.add(new VM(pid, props));
                vm.detach();
            } catch (MonitorException me) {
                logger.warn("target process[{}] is no longer available", pidInteger, me);
            }
        }
        return Collections.unmodifiableList(vms);
    }

    public static void main(String... args) throws URISyntaxException, MonitorException {
        List<VM> vms = getVMs("localhost");
        vms.forEach(e -> logger.info("{} {}", e.getPid(), e.getFqcn()));
    }

}