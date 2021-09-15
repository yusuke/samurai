package samurai.core;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestSpringBootActuatorJSONThreadDump {
    @Test
    void stacklines() throws IOException {
        ThreadStatistic statistic = new ThreadStatistic();
        ThreadDumpExtractor dumpExtractor = new ThreadDumpExtractor(statistic);
        dumpExtractor.analyze(TestSpringBootActuatorJSONThreadDump.class.getResourceAsStream("/SpringBoot/spring-boot-2.5.4-java8.dmp"));
        FullThreadDump fullThreadDump = statistic.getFullThreadDumps().get(0);
        assertTrue(fullThreadDump.isDeadLocked());
        ThreadDump deadkLock1 = fullThreadDump.getThreadDumpById("20");
        assertTrue(deadkLock1.isBlocked());
        assertTrue(deadkLock1.isBlocking());
        assertFalse(deadkLock1.isIdle());
        List<StackLine> stackLines = deadkLock1.getStackLines();
        System.out.println(stackLines);
        assertEquals("at com.example.actuatordemo.ActuatorDemoApplication.lambda$main$3(ActuatorDemoApplication.java:44)", stackLines.get(0).line);
        assertEquals("- locked <325888395> (a java.lang.Object)", stackLines.get(1).line);
        assertEquals("at com.example.actuatordemo.ActuatorDemoApplication$$Lambda$86/0x0000000800c81220.run(Unknown Source)", stackLines.get(2).line);
        assertEquals("at java.lang.Thread.run(java.base@17-panama/Thread.java:831)", stackLines.get(3).line);
        assertEquals("- waiting to lock <1598621278> (a java.lang.Object)", stackLines.get(4).line);
    }

    @Test
    void toStackLine() throws JSONException {
        assertEquals("at java.lang.Thread.sleep(java.base@17-panama/Native Method)",
                SpringBootActuatorThreadDump.toStackLine(new JSONObject("{\n" +
                        "\"classLoaderName\": null,\n" +
                        "\"moduleName\": \"java.base\",\n" +
                        "\"moduleVersion\": \"17-panama\",\n" +
                        "\"methodName\": \"sleep\",\n" +
                        "\"fileName\": \"Thread.java\",\n" +
                        "\"lineNumber\": -2,\n" +
                        "\"nativeMethod\": true,\n" +
                        "\"className\": \"java.lang.Thread\"\n" +
                        "        }")));
        assertEquals("at com.example.actuatordemo.ActuatorDemoApplication.sleep(ActuatorDemoApplication.java:71)",
                SpringBootActuatorThreadDump.toStackLine(new JSONObject("{\n" +
                        "\"classLoaderName\": \"app\",\n" +
                        "\"moduleName\": null,\n" +
                        "\"moduleVersion\": null,\n" +
                        "\"methodName\": \"sleep\",\n" +
                        "\"fileName\": \"ActuatorDemoApplication.java\",\n" +
                        "\"lineNumber\": 71,\n" +
                        "\"nativeMethod\": false,\n" +
                        "\"className\": \"com.example.actuatordemo.ActuatorDemoApplication\"\n" +
                        "        }")));
        assertEquals("at com.example.actuatordemo.ActuatorDemoApplication$$Lambda$83/0x0000000800c80a08.run(Unknown Source)",
                SpringBootActuatorThreadDump.toStackLine(new JSONObject("{\n" +
                        "\"classLoaderName\": \"app\",\n" +
                        "\"moduleName\": null,\n" +
                        "\"moduleVersion\": null,\n" +
                        "\"methodName\": \"run\",\n" +
                        "\"fileName\": null,\n" +
                        "\"lineNumber\": -1,\n" +
                        "\"nativeMethod\": false,\n" +
                        "\"className\": \"com.example.actuatordemo.ActuatorDemoApplication$$Lambda$83/0x0000000800c80a08\"\n" +
                        "        }")));

    }
    @Test
    void lockedMonitorsToStackLine() throws JSONException {
        List<String> stringList = SpringBootActuatorThreadDump.lockedMonitorsToStackLine(new JSONObject("{\n" +
                "          \"className\": \"java.lang.Object\",\n" +
                "          \"identityHashCode\": 1018014235,\n" +
                "          \"lockedStackDepth\": 2,\n" +
                "          \"lockedStackFrame\": {\n" +
                "            \"classLoaderName\": \"app\",\n" +
                "            \"moduleName\": null,\n" +
                "            \"moduleVersion\": null,\n" +
                "            \"methodName\": \"lambda$main$0\",\n" +
                "            \"fileName\": \"ActuatorDemoApplication.java\",\n" +
                "            \"lineNumber\": 14,\n" +
                "            \"nativeMethod\": false,\n" +
                "            \"className\": \"com.example.actuatordemo.ActuatorDemoApplication\"\n" +
                "          }\n" +
                "        }"));
        assertEquals("at com.example.actuatordemo.ActuatorDemoApplication.lambda$main$0(ActuatorDemoApplication.java:14)",
                stringList.get(0));
        assertEquals("- locked <1018014235> (a java.lang.Object)",
                stringList.get(1));
    }
    @Test
    void lockInfoToStackLine() throws  JSONException{
        assertEquals("- waiting to lock <325888395> (a java.lang.Object)",
                SpringBootActuatorThreadDump.lockInfoToStackLine(new JSONObject("{\n" +
                        "        \"className\": \"java.lang.Object\",\n" +
                        "        \"identityHashCode\": 325888395\n" +
                        "      }")));
    }
}