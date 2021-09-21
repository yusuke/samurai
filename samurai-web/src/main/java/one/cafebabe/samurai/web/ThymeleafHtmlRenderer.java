/*
 * Copyright 2003-2012 Yusuke Yamamoto
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
package one.cafebabe.samurai.web;

import one.cafebabe.samurai.core.StackLine;
import one.cafebabe.samurai.core.ThreadDump;
import one.cafebabe.samurai.core.ThreadDumpSequence;
import one.cafebabe.samurai.core.ThreadStatistic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ThymeleafHtmlRenderer implements Constants {
    private final ResourceBundle resource = ResourceBundle.getBundle("one.cafebabe.samurai.web.messages");
    public String config_wrapDump = "true";
    private String baseurl;
    private final Util util = new Util();

    ClassLoaderTemplateResolver resolver = getResolver();

    public ThymeleafHtmlRenderer() {
        this(null);
    }

    public ThymeleafHtmlRenderer(@Nullable String baseurl) {
        this.baseurl = baseurl;
        if (baseurl == null) {
            try {
                //noinspection ConstantConditions
                this.baseurl = this.getClass().getResource("/one/cafebabe/samurai/web/table.html").toURI().toString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        if (this.baseurl.endsWith(".jar")) {
            this.baseurl = "jar:" + this.baseurl + "!/";
        }
    }

    public String process(@NotNull String template, Context context) {
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);
        return engine.process(template, context);
    }


    @NotNull
    public static ClassLoaderTemplateResolver getResolver() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setPrefix("one/cafebabe/samurai/web/");
        resolver.setSuffix(".html");
        resolver.setCacheable(true);
        resolver.setCacheTTLMs(60000L);
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }

    public String render(ThreadFilter filter, ThreadStatistic statistic, Map<String, Object> map) {
        Context context = new Context();
        context.setVariables(map);
        context.setVariable("resource", resource);
        context.setVariable("wrap", config_wrapDump);
        context.setVariable("stats", statistic);
        context.setVariable("filter", filter);
        context.setVariable("util", util);
        context.setVariable("baseurl", baseurl);

        return process(filter.mode.name(), context);
    }

    public int length(Object[] array) {
        return array.length;
    }


    /**
     * Saves threadstatistic as html files.<br>
     *
     * @param stats     statistics to be saved.
     * @param directory Directory to save the html files.
     * @param listener  listener to receive progress events
     * @throws IOException when save action fails
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void saveTo(@NotNull ThreadStatistic stats, @NotNull File directory, @NotNull ProgressListener listener) throws IOException {
        File tableDir = new File(directory.getAbsolutePath() + File.separator + Constants.MODE_TABLE);
        File fullDir = new File(directory.getAbsolutePath() + File.separator + Constants.MODE_FULL);
        File sequenceDir = new File(directory.getAbsolutePath() + File.separator + Constants.MODE_SEQUENCE);
        directory.mkdirs();
        tableDir.mkdirs();
        fullDir.mkdirs();
        sequenceDir.mkdirs();
        ThreadFilter filter = new ThreadFilter();
        ThreadDumpSequence[] st = stats.getStackTracesAsArray();
        int count = stats.getFullThreadDumpCount() * 2 + st.length * 2 + 2;
        int progress = 0;
        listener.notifyProgress(progress++, count);
        //save index page
        saveAs(directory, "index.html", "<html><head><meta http-equiv=\"Refresh\" content=\"0;URL=./table/index.html\"/></head><body></body></html>");
        listener.notifyProgress(progress++, count);
        filter.mode = ThreadFilter.View.table;
        filter.setThreadId(stats.getFirstThreadId());
        filter.setFullThreadIndex(0);
        Map<String, Object> webContext = new HashMap<>();
        webContext.put("fontFamily", "Helvetica Neue");
        webContext.put("fontSize", "12");
        //save table view
        saveAs(directory, Constants.MODE_TABLE + "/index.html", stats, filter, webContext);
        listener.notifyProgress(progress++, count);
        //save full thread dump view
        filter.setShrinkIdle(false);
        filter.mode = ThreadFilter.View.full;
        do {
            for (int i = 0; i < stats.getFullThreadDumpCount(); i++) {
                filter.setFullThreadIndex(i);
                saveAs(directory, Constants.MODE_FULL + "/index-" + i + "_shrink-" + filter.getShrinkIdle() + ".html", stats, filter, webContext);
                listener.notifyProgress(progress++, count);
            }
            filter.setShrinkIdle(!filter.getShrinkIdle());
        } while (filter.getShrinkIdle());

        //save sequence thread dump view
        filter.setShrinkIdle(false);
        filter.mode = ThreadFilter.View.sequence;
        do {
            for (ThreadDumpSequence aSt : st) {
                filter.setThreadId(aSt.getId());
                saveAs(directory, Constants.MODE_SEQUENCE + "/threadId-" + filter.getThreadId() + "_shrink-" + filter.getShrinkIdle() + ".html", stats, filter, webContext);
                listener.notifyProgress(progress++, count);
            }
            filter.setShrinkIdle(!filter.getShrinkIdle());
        } while (filter.getShrinkIdle());
    }

    public void saveAs(File dir, String fileName, ThreadStatistic stats, ThreadFilter filter, Map<String, Object> webContext) throws IOException {
        saveAs(dir, fileName, render(filter, stats, webContext));
    }

    public void saveAs(File dir, String fileName, String utf8Content) throws IOException {
        Files.writeString(dir.toPath().resolve(fileName), utf8Content, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public static class Util {
        public String asHTML(StackLine line, int index, boolean shrink) {
            if (line.isHoldingLock()) {
                String objId = line.getLockedObjectId();
                int objIdBegin = line.getLine().indexOf(objId);
                StringBuilder html = new StringBuilder();
                html.append(escape(line.getLine().substring(0, objIdBegin)));
                if (-1 != index) {
                    html.append("<a name=\"").append(objId).append("_").append(index).append("\"></a>");
                } else {
                    html.append("<a name=\"").append(objId).append("\"></a>");
                }
                html.append(objId);
                html.append(escape(line.getLine().substring(objIdBegin + objId.length())));
                return html.toString();
            } else if (line.isTryingToGetLock() && null != line.getLockedObjectId()) {
                String objId = line.getLockedObjectId();
                int objIdBegin = line.getLine().indexOf(objId);
                StringBuilder html = new StringBuilder();
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

        public String threadDumpToClass(ThreadDump threadDump) {
            if (threadDump == null) {
                return "notexist";
            }
            if (threadDump.isBlocked()) {
                return "blocked";
            }
            if (threadDump.isBlocking()) {
                return "blocking";
            }
            if (threadDump.isIdle()) {
                return "idle";
            }
            return "normal";
        }

        public String threadDumpToImageSrc(ThreadDump threadDump, int count, ThreadDumpSequence sequence) {
            if (threadDump == null) {
                return "space.gif";
            }
            if (threadDump.isDeadLocked()) {
                return "deadlocked.gif";
            }
            if (sequence.sameAsBefore(count)) {
                return "same-h.gif";
            }
            return "space.gif";
        }

        public String threadDumpToClassName(ThreadDump threadDump) {
            if (threadDump == null) {
                return "back-notexist";
            }
            if (threadDump.isBlocked()) {
                return "back-blocked";
            }

            if (threadDump.isBlocking()) {
                return "back-blocking";
            }

            if (threadDump.isIdle()) {
                return "back-idle";
            }
            return "back-normal";
        }

        public String escape(String from) {
            int lessThanIndex = from.indexOf("<");
            int greaterThanIndex = from.indexOf(">");
            if (-1 == lessThanIndex && -1 == greaterThanIndex) {
                return from;
            }
            StringBuilder to = new StringBuilder(from);
            while (-1 != lessThanIndex) {
                to.replace(lessThanIndex, lessThanIndex + 1, "&lt;");
                lessThanIndex = to.indexOf("<", lessThanIndex + 4);
                if (greaterThanIndex != -1) {
                    greaterThanIndex += 3;
                }
            }
            while (-1 != greaterThanIndex) {
                to.replace(greaterThanIndex, greaterThanIndex + 1, "&gt;");
                greaterThanIndex = to.indexOf(">", greaterThanIndex + 4);
            }
            return to.toString();
        }
    }
}