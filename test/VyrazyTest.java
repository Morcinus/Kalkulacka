import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

class VyrazyTest {
	@Test
	void testOnePlusOne() {
		String input = "1+1";
		Vyrazy.Lexer lexer = new Vyrazy.Lexer(input);
		Vyrazy.Node ast = Vyrazy.Parser.parse(lexer);

		assertEquals("(1 + 1)", ast.format());
		assertEquals(2, ast.compute());
	}

	@Test
	void testSum() {
		String input = "1+20+5+9+4";
		Vyrazy.Lexer lexer = new Vyrazy.Lexer(input);
		Vyrazy.Node ast = Vyrazy.Parser.parse(lexer);

		assertEquals("(1 + (20 + (5 + (9 + 4))))", ast.format());
		assertEquals(39, ast.compute());
	}

	@Test
	void testMultiply() {
		String input = "2*5*7*9*15";
		Vyrazy.Lexer lexer = new Vyrazy.Lexer(input);
		Vyrazy.Node ast = Vyrazy.Parser.parse(lexer);

		assertEquals("2 * 5 * 7 * 9 * 15", ast.format());
		assertEquals(9450, ast.compute());
	}

	@Test
	void testMultiplyAndSum() {
		String input = "5+5*8+2+9*4";
		Vyrazy.Lexer lexer = new Vyrazy.Lexer(input);
		Vyrazy.Node ast = Vyrazy.Parser.parse(lexer);

		assertEquals("(5 + (5 * 8 + (2 + 9 * 4)))", ast.format());
		assertEquals(83, ast.compute());
	}

	@Test
	public void testMain() {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		PrintStream sysOutBackup = System.out;
		System.setOut(new PrintStream(outContent));

		String inputString = "1+1\r\n" + "\r\n";

		InputStream sysInBackup = System.in; // backup System.in to restore it later
		ByteArrayInputStream in = new ByteArrayInputStream(inputString.getBytes());
		System.setIn(in);

		Vyrazy.main(null);

		/*
		 * System.out.print("hello"); assertEquals("hello", outContent.toString());
		 */
		assertEquals("'1+1' => '(1 + 1)' = 2\n", outContent.toString());

		System.setIn(sysInBackup);
		System.setOut(sysOutBackup);
	}

}
