import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Montador {

	enum States {
		Beggin, Data, Text
	};

	enum Type {
		DataDirective, TextDirective, Instruction, Data, Commentary, Unknown
	}

	private static States state = States.Beggin;
	private static int numLines = 0;
	private static int numVariables = 0;
	private static boolean textDirectiveAcessed = false;
	private static boolean dataDirectiveAcessed = false;

	private static int numLinesReaded = 0;

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			if (args.length == 0)
				throw new InputFileNotInformedException();
			checkHelp(args);

			File inputFile = new File(args[0]);
			FileReader inputFileReader = new FileReader(inputFile);
			BufferedReader inputBuffer = new BufferedReader(inputFileReader);

			String currentLine;
			Type type;

			currentLine = inputBuffer.readLine();
			while (currentLine != null) {
				numLinesReaded++;
				type = processLine(currentLine);

				if (state == States.Beggin) {
					if (type == Type.TextDirective)
						state = States.Text;
					else if (type == Type.DataDirective)
						state = States.Data;
					else if (type == Type.Data)
						throw new SyntaxException(numLinesReaded, "Você deve declarar variáveis dentro da diretiva data!");
					else if (type == Type.Instruction)
						throw new SyntaxException(numLinesReaded, "Você deve escrever instruções dentro da diretiva text!");
					else if (type == Type.Unknown)
						throw new SyntaxException(numLinesReaded, "Expressão não reconhecida.");
				} else if (state == States.Data) {

				} else if (state == States.Text) {

				}

				currentLine = inputBuffer.readLine();
			}

			inputBuffer.close();

		} catch (InputFileNotInformedException e) {
			System.out.println(e.getMessage());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Não foi possível abrir arquivo de entrada.");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Não foi possível ler do arquivo de entrada.");
		} catch (HelpException e) {
			e.printMessage();
		} catch (SyntaxException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * 
	 * @param currentLine
	 * @return
	 * @throws SyntaxException
	 */
	private static Type processLine(String currentLine) throws SyntaxException {

		String[] words = currentLine.split(" |	");
		ArrayList<String> list = new ArrayList<String>();

		for (String s : words) {
			if (s.length() == 0)
				continue;
			if (s.startsWith("#")) {
				break;
			}
			list.add(s);
		}

		if (list.size() == 0)
			return Type.Commentary;

		if (list.get(0).equals(".text")) {
			if (list.size() != 1) {
				throw new SyntaxException(numLinesReaded, "A diretiva .text não possui argumentos.");
			} else if (textDirectiveAcessed) {
				throw new SyntaxException(numLinesReaded, "Multiplas definições da diretiva .text.");
			}

			textDirectiveAcessed = true;
			return Type.TextDirective;
		} else if (list.get(0).equals(".data")) {
			if (list.size() != 1) {
				throw new SyntaxException(numLinesReaded, "A diretiva .data não possui argumentos.");
			} else if (dataDirectiveAcessed) {
				throw new SyntaxException(numLinesReaded, "Multiplas definições da diretiva .data.");
			}
			dataDirectiveAcessed = true;
			return Type.DataDirective;
		}

		return Type.Unknown;
	}

	/**
	 * 
	 * @param args
	 * @throws HelpException
	 */
	public static void checkHelp(String[] args) throws HelpException {
		for (String i : args)
			if (i.equals("--help"))
				throw new HelpException();
	}

}

class InputFileNotInformedException extends Exception {

	public String getMessage() {
		return "Você deve informar o arquivo de entrada! --help para ajuda";
	}

	private static final long serialVersionUID = -59073372489491388L;

}

class HelpException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1567051310667255360L;

	public void printMessage() {
		System.out.println("Isso é um montador para arquitetura MIPS.");
		System.out.println("\tVocê deve executá-lo da seguinte maneira:\n");
		System.out.println("\t\tjava montador <arquivodeentrada>.asm\n");
		System.out.println("\tO programa gerará um arquivo com o mesmo nome e com extensão .bin");
	}
}

class SyntaxException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int line = -1;
	private String error;

	public SyntaxException(int line, String error) {
		this.line = line;
		this.error = error;
	}

	public String getMessage() {
		return ("Erro de sintaxe na linha " + line + ": " + error);
	}

}