import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

class VyrazyTest {
	@Test
	public void testOnePlusOne() {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		PrintStream sysOutBackup = System.out;
		System.setOut(new PrintStream(outContent));

		String inputString = "1+1\r\n" + "\r\n";

		InputStream sysInBackup = System.in; // backup System.in to restore it later
		ByteArrayInputStream in = new ByteArrayInputStream(inputString.getBytes());
		System.setIn(in);

		Vyrazy.main(null);

		assertEquals("'1+1' => '(1 + 1)' = 2\n", outContent.toString());

		System.setIn(sysInBackup);
		System.setOut(sysOutBackup);
	}

	@Test
	public void testSum() {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		PrintStream sysOutBackup = System.out;
		System.setOut(new PrintStream(outContent));

		String inputString = "1+20+5+9+4\r\n" + "\r\n";

		InputStream sysInBackup = System.in; // backup System.in to restore it later
		ByteArrayInputStream in = new ByteArrayInputStream(inputString.getBytes());
		System.setIn(in);

		Vyrazy.main(null);

		assertEquals("'1+20+5+9+4' => '(1 + (20 + (5 + (9 + 4))))' = 39\n", outContent.toString());

		System.setIn(sysInBackup);
		System.setOut(sysOutBackup);
	}

	@Test
	public void testMultiply() {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		PrintStream sysOutBackup = System.out;
		System.setOut(new PrintStream(outContent));

		String inputString = "2*5*7*9*15\r\n" + "\r\n";

		InputStream sysInBackup = System.in; // backup System.in to restore it later
		ByteArrayInputStream in = new ByteArrayInputStream(inputString.getBytes());
		System.setIn(in);

		Vyrazy.main(null);

		assertEquals("'2*5*7*9*15' => '2 * 5 * 7 * 9 * 15' = 9450\n", outContent.toString());

		System.setIn(sysInBackup);
		System.setOut(sysOutBackup);
	}

	@Test
	public void testSumAndMultiply() {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		PrintStream sysOutBackup = System.out;
		System.setOut(new PrintStream(outContent));

		String inputString = "5+5*8+2+9*4\r\n" + "\r\n";

		InputStream sysInBackup = System.in; // backup System.in to restore it later
		ByteArrayInputStream in = new ByteArrayInputStream(inputString.getBytes());
		System.setIn(in);

		Vyrazy.main(null);

		assertEquals("'5+5*8+2+9*4' => '(5 + (5 * 8 + (2 + 9 * 4)))' = 83\n", outContent.toString());

		System.setIn(sysInBackup);
		System.setOut(sysOutBackup);
	}

	@Test
	public void testVariables() {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		PrintStream sysOutBackup = System.out;
		System.setOut(new PrintStream(outContent));

		String inputString = "2 * 3\r\n" + "a = 5 + 1\r\n" + "b = a + 2\r\n" + "b\r\n" + "\r\n";

		InputStream sysInBackup = System.in; // backup System.in to restore it later
		ByteArrayInputStream in = new ByteArrayInputStream(inputString.getBytes());
		System.setIn(in);

		Vyrazy.main(null);

		assertEquals("'5+1+2' => '(5 + (1 + 2))' = 8\n", outContent.toString());

		System.setIn(sysInBackup);
		System.setOut(sysOutBackup);
	}

	@Test
	public void testComments() {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		PrintStream sysOutBackup = System.out;
		System.setOut(new PrintStream(outContent));

		String inputString = "2 * 3 # 161ad\r\n" + "a = 5 + 1 \r\n" + "b = a + 2 # aa \r\n" + "b #\r\n" + "\r\n";

		InputStream sysInBackup = System.in; // backup System.in to restore it later
		ByteArrayInputStream in = new ByteArrayInputStream(inputString.getBytes());
		System.setIn(in);

		Vyrazy.main(null);

		assertEquals("'5+1+2' => '(5 + (1 + 2))' = 8\n", outContent.toString());

		System.setIn(sysInBackup);
		System.setOut(sysOutBackup);
	}

	@Test
	public void testParentheses() {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		PrintStream sysOutBackup = System.out;
		System.setOut(new PrintStream(outContent));

		String inputString = "3+2*(2+7)+5\r\n" + "\r\n";

		InputStream sysInBackup = System.in; // backup System.in to restore it later
		ByteArrayInputStream in = new ByteArrayInputStream(inputString.getBytes());
		System.setIn(in);

		Vyrazy.main(null);

		assertEquals("'5+1+2' => '(5 + (1 + 2))' = 8\n", outContent.toString());

		System.setIn(sysInBackup);
		System.setOut(sysOutBackup);
	}

}
