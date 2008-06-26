/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.util;

import junit.framework.TestCase;

public class I18nizedResourceTest extends TestCase {

    private GUIResourceBundle resource;

    /*
      * @see TestCase#setUp()
      */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
      * @see TestCase#tearDown()
      */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetMessage() {
//        resource = GUIResourceBundle.getInstance("samurai.util.messages");
        resource = GUIResourceBundle.getInstance();
        assertTrue("\u65e5\u672c\u8a9e".equals(resource.getMessage("test")) || "test".equals(resource.getMessage("test")));
        assertTrue("\u65e5\u672c\u8a9efoo\u3064\u304b\u3048\u307e\u3059\u304b\uff1f".equals(resource.getMessage("test2", "foo")) || "foofoobar".equals(resource.getMessage("test2", "foo")));
    }
}
