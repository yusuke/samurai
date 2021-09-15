package samurai.core;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpringBootActuatorThreadDump extends ThreadDump {
    private final String id;

    public SpringBootActuatorThreadDump(JSONObject json) throws JSONException {
        super(json.getString("threadName"), json.getString("threadName"), json.getString("threadState"));
        IS_BLOCKED = this.getCondition().equals("BLOCKED");
        IS_IDLE = this.getCondition().equals("WAITING");
        IS_DAEMON = json.getBoolean("daemon");
        IS_BLOCKING = this.getCondition().equals("TIMED_WAITING");
        id = json.getString("threadId");
        JSONArray lockedMonitorsArray = json.getJSONArray("lockedMonitors");
        List<List<String>> lockedMonitors = new ArrayList<>(lockedMonitorsArray.length());
        for (int i = 0; i < lockedMonitorsArray.length(); i++) {
            lockedMonitors.add(lockedMonitorsToStackLine(lockedMonitorsArray.getJSONObject(i)));
        }
        JSONArray stackTraceArray = json.getJSONArray("stackTrace");
        for (int i = 0; i < stackTraceArray.length(); i++) {
            String stackLine = toStackLine(stackTraceArray.getJSONObject(i));
            Optional<List<String>> first = lockedMonitors.stream().filter(e -> e.get(0).equals(stackLine)).findFirst();
            first.ifPresentOrElse(strings -> {
                lockedMonitors.remove(strings);
                addStackLine(new StackLine(strings.get(0)));
                addStackLine(new StackLine(strings.get(1)));
            }, () -> addStackLine(new StackLine(stackLine)));
        }
        if (json.has("lockInfo") && !json.isNull("lockInfo")) {
            JSONObject lockInfo = json.getJSONObject("lockInfo");
            addStackLine(new StackLine(lockInfoToStackLine(lockInfo)));
        }
    }

    @Override
    void addStackLine(String line) {
        // do nothing
    }

    @Override
    public String getId() {
        return this.id;
    }

    static String toStackLine(JSONObject stackeTrace) throws JSONException {
        String classMethod = String.format("at %s.%s", stackeTrace.getString("className")
                , stackeTrace.getString("methodName"));
        String lineNumber;
        int lineNumberInt = stackeTrace.getInt("lineNumber");
        switch (lineNumberInt) {
            case -2:
                lineNumber = String.format("(%sNative Method)", getModuleName(stackeTrace));
                break;
            case -1:
                lineNumber = "(Unknown Source)";
                break;
            default:
                lineNumber = String.format("(%s%s:%s)", getModuleName(stackeTrace),
                        stackeTrace.getString("fileName")
                        , lineNumberInt);
        }
        return classMethod + lineNumber;
    }

    static String getModuleName(JSONObject stackeTrace) throws JSONException {
        String moduleName = stackeTrace.getString("moduleName");
        String moduleVersion = stackeTrace.getString("moduleVersion");
        if (moduleName != null && moduleVersion != null && !moduleName.equals("null")
                && !moduleVersion.equals("null")) {
            return String.format("%s@%s/", moduleName, moduleVersion);
        } else {
            return "";
        }
    }

    static List<String> lockedMonitorsToStackLine(JSONObject lockedMonitor) throws JSONException {
        List<String> lines = new ArrayList<>();
        lines.add(toStackLine(lockedMonitor.getJSONObject("lockedStackFrame")));
        lines.add(String.format("- locked <%s> (a %s)",
                lockedMonitor.getString("identityHashCode"),
                lockedMonitor.getString("className")));

        return lines;
    }

    static String lockInfoToStackLine(JSONObject lockInfo) throws JSONException {
        return String.format("- waiting to lock <%s> (a %s)",
                lockInfo.getString("identityHashCode"),
                lockInfo.getString("className"));
    }
}
