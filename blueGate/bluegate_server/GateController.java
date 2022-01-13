import java.io.IOException;

public class GateController {

    Runtime runtime;

    public GateController() throws IOException {
        this.runtime = Runtime.getRuntime();

	// set gpio mode to pulse width modulation
	this.runtime.exec("gpio mode 1 pwm");

	// set mode to mark:space ratio
	this.runtime.exec("gpio pwm-ms");

	// adjust gpio clock
	this.runtime.exec("gpio pwmc 192");
	this.runtime.exec("gpio pwmr 2000");

        close_gate();
    }

    public void open() throws IOException {
        synchronized(this) {
            open_gate();
            try {
                Thread.sleep(2000);
            } catch(InterruptedException e) {
                close_gate();
            }
            close_gate();
        }
    }

    public void emergency_open() throws IOException {
        open_gate();
    }

    public void emergency_close() throws IOException {
        close_gate();
    }

    private void open_gate() throws IOException {
        this.runtime.exec("gpio pwm 1 180");
    }

    private void close_gate() throws IOException {
        this.runtime.exec("gpio pwm 1 280");
    }
}
