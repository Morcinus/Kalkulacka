import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Vyrazy {
	public static interface Node {
		public static final String INDENT = "  ";

		public int compute();

		public String format();

		public void tree(String indent);
	}

	public static class Number implements Node {
		private final int value;

		public Number(int v) {
			value = v;
		}

		@Override
		public int compute() {
			return value;
		}

		@Override
		public String format() {
			return Integer.toString(value);
		}

		@Override
		public void tree(String indent) {
			System.out.printf("%s%d\n", indent, value);
		}
	}

	public static abstract class BinaryOp implements Node {
		private final Node left;
		private final Node right;
		private final String formatter;
		private final String name;

		public BinaryOp(Node l, Node r, String fmt, String longName) {
			left = l;
			right = r;
			formatter = fmt;
			name = longName;
		}

		protected abstract int compute(int left, int right);

		@Override
		public int compute() {
			return compute(left.compute(), right.compute());
		}

		@Override
		public String format() {
			return String.format(formatter, left.format(), right.format());
		}

		@Override
		public void tree(String indent) {
			System.out.printf("%s%s\n", indent, name);
			left.tree(indent + INDENT);
			right.tree(indent + INDENT);
		}
	}

	public static class Sum extends BinaryOp {
		public Sum(Node l, Node r) {
			super(l, r, "(%s + %s)", "PLUS");
		}

		@Override
		public int compute(int left, int right) {
			return left + right;
		}
	}

	public static class Product extends BinaryOp {
		public Product(Node l, Node r) {
			super(l, r, "%s * %s", "KRAT");
		}

		@Override
		public int compute(int left, int right) {
			return left * right;
		}
	}

	public static enum TokenType {
		NUMBER, SUM, PRODUCT, EOF
	}

	public static class Token {
		private final TokenType type;
		private final int number;

		private Token(TokenType t, int num) {
			type = t;
			number = num;
		}

		public static Token makeNumber(int value) {
			return new Token(TokenType.NUMBER, value);
		}

		public static Token makeSum() {
			return new Token(TokenType.SUM, 0);
		}

		public static Token makeProduct() {
			return new Token(TokenType.PRODUCT, 0);
		}

		public static Token makeEof() {
			return new Token(TokenType.EOF, 0);
		}

		public TokenType getType() {
			return type;
		}

		public int getNumber() {
			if (type != TokenType.NUMBER) {
				throw new IllegalStateException("Not a number.");
			}
			return number;
		}
	}

	public static class Lexer {
		private final List<Token> tokens = new LinkedList<>();

		public Lexer(String input) {
			lexIt(input + " ");
		}

		private void lexIt(String input) {
			// Expects input terminates with blank space
			boolean insideNumber = false;
			int number = 0;

			char charArray[] = input.toCharArray();
			for (int i = 0; i < charArray.length; i++) {
				if ((charArray[i] >= '0') && (charArray[i] <= '9')) {
					insideNumber = true;
					number = number * 10 + (charArray[i] - '0');
					continue;
				}

				if (insideNumber) {
					tokens.add(Token.makeNumber(number));
					number = 0;
					insideNumber = false;
				}
				if (charArray[i] == '+') {
					tokens.add(Token.makeSum());
				} else if (charArray[i] == '*') {
					tokens.add(Token.makeProduct());
				} else if ((charArray[i] == ' ') || (charArray[i] == '\t')) {
					// Skip.
				} else {
					throw new IllegalArgumentException("Wrong input.");
				}
			}
			tokens.add(Token.makeEof());
		}

		public TokenType peek() {
			return tokens.get(0).getType();
		}

		public Token next() {
			return tokens.remove(0);
		}
	}

	public static class Parser {

		private final Lexer lexer;

		private Parser(Lexer lexer) {
			this.lexer = lexer;
		}

		public static Node parse(Lexer lexer) {
			Parser parser = new Parser(lexer);
			return parser.parse();
		}

		private void expect(TokenType expected) {
			TokenType actual = lexer.peek();
			if (actual != expected) {
				String message = String.format("Expected %s, got %s.", expected, actual);
				throw new IllegalArgumentException(message);
			}
		}

		private Node parse() {
			Node result = expression();
			expect(TokenType.EOF);
			return result;
		}

		private Node expression() {
			Node left = factor();
			if (lexer.peek() == TokenType.SUM) {
				lexer.next();
				Node right = expression();
				return new Sum(left, right);
			} else {
				return left;
			}
		}

		private Node factor() {
			Node left = number();
			if (lexer.peek() == TokenType.PRODUCT) {
				lexer.next();
				Node right = factor();
				return new Product(left, right);
			} else {
				return left;
			}
		}

		private Node number() {
			expect(TokenType.NUMBER);
			Token tok = lexer.next();
			return new Number(tok.getNumber());
		}
	}

	/*
	 * public static class Variable { private final String name; private final
	 * char[] value;
	 * 
	 * private Variable(String n, char[] val) { name = n; value = val; } }
	 */

	public static class VariableParser {
		// Variables declared by the user
		public final Map<String, char[]> variables = new HashMap<>();

		public VariableParser() {

		}

		public boolean findVarDeclaration(String input) {
			char charArray[] = input.replaceAll("\\s+", "").toCharArray();

			for (int i = 0; i < charArray.length; i++) {
				if (charArray[i] == '=') {
					if ((charArray[i - 1] >= 'a' && charArray[i - 1] <= 'z')
							|| (charArray[i - 1] >= 'A' && charArray[i - 1] <= 'Z')) {
						char[] variableValue = fillVariableValues(
								Arrays.copyOfRange(charArray, i + 1, charArray.length));

						variableValue = fillVariableValues(variableValue);
						declareVariable(String.valueOf(charArray[i - 1]), variableValue);

						return true;
					}
				}
			}

			return false;
		}

		private char[] fillVariableValues(char[] charArray) {
			String string = "";
			for (int i = 0; i < charArray.length; i++) {
				if ((charArray[i] >= 'a' && charArray[i] <= 'z') || (charArray[i] >= 'A' && charArray[i] <= 'Z')) {
					String character = String.valueOf(charArray[i]);

					string = string + String.valueOf(variables.get(character));
				} else {
					string = string + charArray[i];
				}

			}
			return string.toCharArray();
		}

		private String removeComment(String input) {
			char charArray[] = input.replaceAll("\\s+", "").toCharArray();
			for (int i = 0; i < charArray.length; i++) {
				if (charArray[i] == '#') {
					return String.valueOf(Arrays.copyOfRange(charArray, 0, i));
				}

			}
			return input;
		}

		public String checkVariable(String input) {
			char charArray[] = input.trim().toCharArray();

			for (int i = 0; i < charArray.length; i++) {
				if ((charArray[i] >= 'a' && charArray[i] <= 'z') || (charArray[i] >= 'A' && charArray[i] <= 'Z')) {
					String character = String.valueOf(charArray[i]);

					return String.valueOf(variables.get(character));
				}

			}
			return input;
		}

		private void declareVariable(String variableName, char[] variableValue) {
			variables.put(variableName, variableValue);
		}
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		VariableParser variableParser = new VariableParser();
		String lastLine = "";
		while (sc.hasNextLine()) {

			String currentLine = sc.nextLine();
			currentLine = variableParser.removeComment(currentLine);
			if (variableParser.findVarDeclaration(currentLine) == true) {
				// Declared variable
				continue;
			} else {
				if (currentLine.equals("")) {
					char[] line = variableParser.fillVariableValues(lastLine.toCharArray());
					String input = String.valueOf(line);
					Lexer lexer = new Lexer(input);
					Node ast = Parser.parse(lexer);
					System.out.printf("'%s' => '%s' = %d\n", input, ast.format(), ast.compute());
					// ast.tree("");
				} else {
					lastLine = currentLine;
				}
			}

		}
	}
}