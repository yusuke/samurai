package samurai.web;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import samurai.core.StackLine;
import samurai.core.ThreadDumpSequence;
import samurai.core.ThreadStatistic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * <p>Title: Samurai</p>
 * <p/>
 * <p>Description: a thread dump analyzing tool</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003,2004,2005,2006</p>
 * <p/>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public class VelocityHtmlRenderer implements Constants {
    private ResourceBundle resource = ResourceBundle.getBundle("samurai.web.messages");
    public String config_wrapDump = "true";
    public String style;
    private Template tableView;
    private Template threaddumpView;
    private Template sequenceView;
    private String baseurl;
    private Util util = new Util();

    public VelocityHtmlRenderer(String style) {
        this(style, VelocityHtmlRenderer.class.getProtectionDomain().getCodeSource().getLocation().toString());
        if (baseurl.endsWith(".jar")) {
            baseurl = "jar:" + baseurl + "!/";
        }
        baseurl += "samurai/web/images/";
    }

    public VelocityHtmlRenderer(String style, String baseurl) {
        this.style = style;
        this.baseurl = baseurl;
        Velocity.setProperty("input.encoding", "UTF-8");
        Velocity.setProperty("resource.loader", "class");
        Velocity.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.setProperty("runtime.log.logsystem.class", "samurai.web.NullVelocityLogger");
        try {
            Velocity.init();
            tableView = Velocity.getTemplate("samurai/web/table.vm");
            threaddumpView = Velocity.getTemplate("samurai/web/threaddump.vm");
            sequenceView = Velocity.getTemplate("samurai/web/sequence.vm");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String render(ThreadFilter filter, ThreadStatistic statistic, Map myContext) {

        VelocityContext context = new VelocityContext(new VelocityContext(myContext));
        Writer writer = new StringWriter();
        context.put("resource", resource);
        context.put("style", style);
        context.put("wrap", config_wrapDump);
        context.put("stats", statistic);
        context.put("filter", filter);
        context.put("util", util);
        context.put("baseurl", baseurl);

        try {
            if (filter.isTableView()) {
                tableView.merge(context, writer);
            } else if (filter.isThreadDumpView()) {
                threaddumpView.merge(context, writer);
            } else if (filter.isSequenceView()) {
                sequenceView.merge(context, writer);
            }
            writer.close();
            return writer.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new AssertionError("should never happen");
        }
    }

    public int length(Object[] array) {
        return array.length;
    }


    public void saveTo(ThreadStatistic stats, File directory, ProgressListener listener) throws IOException {

        directory.mkdirs();
        File tableDir = new File(directory.getAbsolutePath() + File.separator + Constants.MODE_TABLE);
        tableDir.mkdirs();
        File fullDir = new File(directory.getAbsolutePath() + File.separator + Constants.MODE_FULL);
        fullDir.mkdirs();
        File sequenceDir = new File(directory.getAbsolutePath() + File.separator + Constants.MODE_SEQUENCE);
        sequenceDir.mkdirs();
        ThreadFilter filter = new ThreadFilter();
        ThreadDumpSequence[] st = stats.getStackTracesAsArray();
        int count = stats.getFullThreadDumpCount() * 2 + st.length * 2 + 2;
        int progress = 0;
        listener.notifyProgress(progress++, count);
        //save index page
        saveAs(directory, "index.html", "<html><head><meta http-equiv=\"Refresh\" content=\"0;URL=./table/index.html\"/></head><body></body></html>");
        listener.notifyProgress(progress++, count);
        filter.setMode(Constants.MODE_TABLE);
        filter.setThreadId(stats.getFirstThreadId());
        filter.setFullThreadIndex(0);
        Map velocityContext = new HashMap(2);
        //save table view
        saveAs(directory, Constants.MODE_TABLE + "/index.html", stats, filter, velocityContext);
        listener.notifyProgress(progress++, count);
        //save full thread dump view
        filter.setShrinkIdle(false);
        filter.setMode(Constants.MODE_FULL);
        do {
            for (int i = 0; i < stats.getFullThreadDumpCount(); i++) {
                filter.setFullThreadIndex(i);
                saveAs(directory, Constants.MODE_FULL + "/index-" + i + "_shrink-" + filter.getShrinkIdle() + ".html", stats, filter, velocityContext);
                listener.notifyProgress(progress++, count);
            }
            filter.setShrinkIdle(!filter.getShrinkIdle());
        } while (filter.getShrinkIdle());

        //save sequence thread dump view
        filter.setShrinkIdle(false);
        filter.setMode(Constants.MODE_SEQUENCE);
        do {
            for (ThreadDumpSequence aSt : st) {
                filter.setThreadId(aSt.getId());
                saveAs(directory, Constants.MODE_SEQUENCE + "/threadId-" + filter.getThreadId() + "_shrink-" + filter.getShrinkIdle() + ".html", stats, filter, velocityContext);
                listener.notifyProgress(progress++, count);
            }
            filter.setShrinkIdle(!filter.getShrinkIdle());
        } while (filter.getShrinkIdle());
    }

    public void saveAs(File dir, String fileName, ThreadStatistic stats, ThreadFilter filter, Map velocityContext) throws IOException {
        saveAs(dir, fileName, render(filter, stats, velocityContext));
    }

    public void saveAs(File dir, String fileName, String utf8Content) throws IOException {
        FileOutputStream fos = null;
        BufferedWriter writer = null;
        try {
            fos = new FileOutputStream(dir + File.separator + fileName);
            writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
            writer.write(utf8Content);
            writer.close();
        } finally {
            if (null != writer) {
                writer.close();
            }
            if (null != fos) {
                fos.close();
            }
        }
    }

    public static class Util {
        public String asHTML(StackLine line, int index, boolean shrink) {
            if (line.isHoldingLock()) {
                String objId = line.getLockedObjectId();
                int objIdBegin = line.getLine().indexOf(objId);
                StringBuffer html = new StringBuffer();
                html.append(escape(line.getLine().substring(0, objIdBegin)));
                if (-1 != index) {
                    html.append("<a name=\"").append(objId).append("_").append(index).append("\"></a>");
                } else {
                    html.append("<a name=\"").append(objId).append("\"></a>");
                }
                html.append(objId);
                html.append(escape(line.getLine().substring(objIdBegin + objId.length())));
                return html.toString();
            } else
            if (line.isTryingToGetLock() && null != line.getLockedObjectId()) {
                String objId = line.getLockedObjectId();
                int objIdBegin = line.getLine().indexOf(objId);
                StringBuffer html = new StringBuffer();
                html.append(escape(line.getLine().substring(0, objIdBegin)));
                if (-1 != index) {
                    html.append("<a href=\"../sequence/threadId-").append(line.getBlockerId()).append("_shrink-").append(shrink).append(".html#").append(objId).append("_").append(index).append("\">");
                } else {
                    html.append("<a href=\"#").append(objId).append("\">");
                }
                html.append(objId);
                html.append("</a>");
                html.append(escape(line.getLine().substring(objIdBegin + objId.length())));
                return html.toString();
            } else {
                return escape(line.getLine());
            }
        }

        public String asHTML(StackLine line) {
            return asHTML(line, -1, false);
        }

        public String escape(String from) {
            StringBuffer to = new StringBuffer(from.length());
            for (int i = 0; i < from.length(); i++) {
                char theChar = from.charAt(i);
                if ('<' == theChar) {
                    to.append("&lt;");
                } else if ('>' == theChar) {
                    to.append("&gt;");
                } else {
                    to.append(theChar);
                }
            }
            return to.toString();
        }
    }
}