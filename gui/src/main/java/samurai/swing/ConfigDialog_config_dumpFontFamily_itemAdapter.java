package samurai.swing;

/**
 * Created by IntelliJ IDEA.
 * User: yusukey
 * Date: May 9, 2008
 * Time: 9:39:22 AM
 * To change this template use File | Settings | File Templates.
 */
class ConfigDialog_config_dumpFontFamily_itemAdapter implements java.awt.event.
        ItemListener {
    ConfigDialog adaptee;

    ConfigDialog_config_dumpFontFamily_itemAdapter(ConfigDialog adaptee) {
        this.adaptee = adaptee;
    }

    public void itemStateChanged(java.awt.event.ItemEvent e) {
        adaptee.config_dumpFontSize_itemStateChanged(e);
    }
}
