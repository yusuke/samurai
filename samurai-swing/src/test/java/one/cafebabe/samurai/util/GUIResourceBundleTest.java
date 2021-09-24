package one.cafebabe.samurai.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GUIResourceBundleTest {

    @Test
    void getLocalizedMessage() {
        GUIResourceBundle messages = new GUIResourceBundle("messages", "one/cafebabe/samurai/swing");
        assertNotNull(messages.getMessage("AboutSamuraiDialog.releaseNote"));
        assertNotNull(messages.getLocalizedMessage("*AboutSamuraiDialog.releaseNote*"));
        assertNotEquals("*MainFrame.title*",messages.getLocalizedMessage("*MainFrame.title*"));
        assertNotNull(messages.getLocalizedMessage("<html>\n" +
                "<body>\n" +
                "*AboutSamuraiDialog.releaseNote*\n" +
                "</body>\n</html>"));
        assertFalse(messages.getLocalizedMessage("<html>\n" +
                "<body>\n" +
                "*AboutSamuraiDialog.releaseNote*\n" +
                "</body>\n</html>").contains("*AboutSamuraiDialog.releaseNote*"));
    }
}