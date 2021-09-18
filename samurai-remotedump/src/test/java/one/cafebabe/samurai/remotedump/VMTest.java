package one.cafebabe.samurai.remotedump;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Execution(ExecutionMode.CONCURRENT)
class VMTest {
    @Test
    void test(){
        VM vm = new VM(1111, "com.intellij.rt.execution.application.AppMain gridgrid.GridgridApplication");
        assertEquals("gridgrid.GridgridApplication", vm.getFqcn());
    }
}
