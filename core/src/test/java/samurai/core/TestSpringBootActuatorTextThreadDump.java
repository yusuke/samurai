package samurai.core;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestSpringBootActuatorTextThreadDump {
    @Test
    void stacklines() throws IOException {
        ThreadStatistic statistic = new ThreadStatistic();
        ThreadDumpExtractor dumpExtractor = new ThreadDumpExtractor(statistic);
        dumpExtractor.analyze(TestSpringBootActuatorTextThreadDump.class.getResourceAsStream("/SpringBoot/spring-boot-2.5.4-java8-text.dmp"));
        FullThreadDump fullThreadDump = statistic.getFullThreadDumps().get(0);
        assertTrue(fullThreadDump.isDeadLocked());
        assertEquals(24, fullThreadDump.getThreadDumps().size());

        assertEquals("Full thread dump OpenJDK 64-Bit Server VM (17+35-2724 mixed mode, emulated-client, sharing):", fullThreadDump.getHeader());
        ThreadDump lockingThread = fullThreadDump.getThreadDumpById("t@17");
        assertFalse(lockingThread.isBlocked());
        assertTrue(lockingThread.isBlocking());
        assertTrue(lockingThread.isIdle());

        ThreadDump deadkLock1 = fullThreadDump.getThreadDumpById("t@20");
        assertTrue(deadkLock1.isBlocked());
        assertTrue(deadkLock1.isBlocking());
        assertFalse(deadkLock1.isIdle());
        assertTrue(deadkLock1.isDeadLocked());
        List<StackLine> stackLines = deadkLock1.getStackLines();
        System.out.println(stackLines);
        assertEquals("   java.lang.Thread.State: BLOCKED", stackLines.get(0).line);
        assertEquals("\tat app//com.example.actuatordemo.ActuatorDemoApplication.lambda$main$3(ActuatorDemoApplication.java:44)", stackLines.get(1).line);
        assertEquals("\t- waiting to lock <b5379e1> (a java.lang.Object) owned by \"deadLock2\" t@21", stackLines.get(2).line);
        assertEquals("\t- locked <41d2f8a> (a java.lang.Object)", stackLines.get(3).line);
        assertEquals("\tat app//com.example.actuatordemo.ActuatorDemoApplication$$Lambda$95/0x0000000800cc1670.run(Unknown Source)", stackLines.get(4).line);
        assertEquals("\tat java.base@17/java.lang.Thread.run(Thread.java:833)", stackLines.get(5).line);
        assertEquals("", stackLines.get(6).line);
        assertEquals("   Locked ownable synchronizers:", stackLines.get(7).line);
        assertEquals("\t- None", stackLines.get(8).line);

        ThreadDumpSequence[] stackTracesAsArray = statistic.getStackTracesAsArray();
        ThreadDump[] threadDumps = stackTracesAsArray[10].asArray();
        ThreadDump threadDumpInSequence = threadDumps[0];
        assertEquals("deadLock2", threadDumpInSequence.getName());
        assertTrue(threadDumpInSequence.isDeadLocked());

    }

    @Test
    void toStackLine() throws JSONException {
        assertEquals("at java.lang.Thread.sleep(java.base@17-panama/Native Method)",
                SpringBootActuatorJSONThreadDump.toStackLine(new JSONObject("{\n" +
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
                SpringBootActuatorJSONThreadDump.toStackLine(new JSONObject("{\n" +
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
                SpringBootActuatorJSONThreadDump.toStackLine(new JSONObject("{\n" +
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
        List<String> stringList = SpringBootActuatorJSONThreadDump.lockedMonitorsToStackLine(new JSONObject("{\n" +
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
                SpringBootActuatorJSONThreadDump.lockInfoToStackLine(new JSONObject("{\n" +
                        "        \"className\": \"java.lang.Object\",\n" +
                        "        \"identityHashCode\": 325888395\n" +
                        "      }")));
    }
}