package one.cafebabe.samurai.remotedump;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import sun.jvmstat.monitor.MonitorException;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Execution(ExecutionMode.CONCURRENT)
class VMTest {
    
    @Test
    void IntelliJIDEA() throws URISyntaxException, MonitorException {
        ProcessUtil.getVMs("localhost");
                
                
        Map<String, String> map = new HashMap<>();
        map.put("sun.rt.javaCommand","");
        map.put("java.property.java.class.path","/Users/yusuke/Library/Application Support/JetBrains/Toolbox/apps/IDEA-U/ch-1/213.4293.20/IntelliJ IDEA 2021.3 EAP.app/Contents/lib/util.jar:/Users/yusuke/Library/Application Support/JetBrains/Toolbox/apps/IDEA-U/ch-1/213.4293.20/IntelliJ IDEA 2021.3 EAP.app/Contents/lib/bootstrap.jar");
        VM vm = new VM(1234, map);
        assertEquals("IntelliJ IDEA 2021.3 EAP.app", vm.getHumanFriendlyFqcn());
    }
    @Test
    void ToolboxApp(){
        Map<String, String> map = new HashMap<>();
        map.put("sun.rt.javaCommand","");
        map.put("java.property.java.class.path", "/Applications/JetBrains Toolbox.app/Contents/MacOS/../lib/jetty-util-ajax-9.4.40.v20210413.jar:/Applications/JetBrains Toolbox.app/Contents/MacOS/../lib/ui-geometry-desktop-0.5.0-build224.jar:/Applications/JetBrains Toolbox.app/Contents/MacOS/../lib/util-strings-212.629.jar:/Applications/JetBrains Toolbox.app/Contents/MacOS/../lib/intellij-deps-fastutil-8.5.2-6.jar:/Applications/JetBrains Toolbox.app/Contents/MacOS/../lib/kotlin-stdlib-1.5.20.jar:/Applications/JetBrains Toolbox.app/Contents/MacOS/../lib/proxy-vole-11b8f6af4a.jar:/Applications/JetBrains Toolbox.app/Contents/MacOS/../lib/okio-jvm-2.8.0.jar:/Applications/JetBrains Toolbox.app/Contents/MacOS/../lib/toolbox-1.21.9712.jar:/Applications/JetBrains Toolbox.app/Contents/MacOS/../lib/kotlin-stdlib-jdk8-1.5.20.jar:/Applications/JetBrains Toolbox.app/Contents/MacOS/../lib/jna-platform-5.6.1.2.jar:/Applications/JetBrains Toolbox.app/Contents/MacOS/../lib/kotlinx-coroutines-swing-1.5.0.jar:/Applications/JetBrains Toolbox.app/Contents/MacOS/../lib/ini4j-0.5.\n");
        VM vm = new VM(1234, map);
        assertEquals("JetBrains Toolbox.app", vm.getHumanFriendlyFqcn());
    }

    @Test
    void isGrater() throws URISyntaxException, MonitorException {
        List<VM> localhost = ProcessUtil.getVMs("localhost");
        for (VM vm : localhost) {
            System.out.println(vm.version);
        }
        assertEquals(3, VM.toIntVersion("1.3.0_292"));
        assertEquals(4, VM.toIntVersion("1.4.0_292"));
        assertEquals(5, VM.toIntVersion("1.5.0_292"));
        assertEquals(6, VM.toIntVersion("1.6.0_292"));
        assertEquals(7, VM.toIntVersion("1.7.0_292"));
        assertEquals(8, VM.toIntVersion("1.8.0_292"));
        assertEquals(11, VM.toIntVersion("11.0.12"));

//        VM vm = new VM(1111, "11", "com.samurai.Main");
//        assertFalse(vm.isVersionGraterThan(11));
//        assertTrue(vm.isVersionGraterThan(10));
    }


}
