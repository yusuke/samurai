package samurai.remotedump;

import junit.framework.TestCase;
import org.junit.Test;

public class VMTest extends TestCase{
    @Test
    public void test(){
        VM vm = new VM(1111, "com.intellij.rt.execution.application.AppMain gridgrid.GridgridApplication");
        assertEquals("gridgrid.GridgridApplication", vm.getFqcn());
    }
}
