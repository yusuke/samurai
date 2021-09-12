package samurai.remotedump;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VMTest {
    @Test
    void test(){
        VM vm = new VM(1111, "com.intellij.rt.execution.application.AppMain gridgrid.GridgridApplication");
        assertEquals("gridgrid.GridgridApplication", vm.getFqcn());
    }
}
