package poehelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Einfacher Textprüfer für Pillars of Eternity Stringtable Dateien. Könnte man teilweise in einen Commit Hook wandeln.
 * 
 * @author xar
 *
 */
public class XmlCompare {

	/** modus 0 = 2 Sprachen vergleichen, modus 1 = Alle Sprachen vergleichen. */
	private int modus = 1;
	/** gender 0 = Default Text, gender 1 = Female Text, gender 2 = alle */
	private int gender = 0;

	public static void main(String[] args) {
		XmlCompare xmlCompare = new XmlCompare();
		if (xmlCompare.modus == 0) {
			xmlCompare.xmlCompareTwo();
		}
		if (xmlCompare.modus == 1) {
			xmlCompare.xmlTokenCompare();
		}
	}
	
	private boolean isInterestingFile(Path file) {
		return !file.getParent().getFileName().toString().contains("test")
				&& !file.getFileName().toString().contains("test")
				&& !file.getParent().getFileName().toString().contains("debug")
				&& !file.getFileName().toString().contains("debug")
				&& !file.getFileName().toString().contains("voice_set")
				&& !file.getParent().getFileName().toString().contains("prototype");
	}

	private void xmlCompareTwo() {
		String baseGameFolder;
		String patchGameFolder;
		String data = "";
		for (int i = 0; i < 3; i++) {
			if (i == 0) {
				System.out.println("Basegame");
				data = "data";
			}
			if (i == 1) {
				System.out.println("Expansion 1");
				data = "data_expansion1";
			}
			if (i == 2) {
				System.out.println("Expansion 2");
				data = "data_expansion2";
			}
			baseGameFolder = "/opt/gog/Pillars of Eternity/game/PillarsOfEternity_Data/" + data + "/localized/de";
			// patchGameFolder = "/opt/gog/Pillars of Eternity/game/PillarsOfEternity_Data/" + data + "/localized/en"; // special use
			patchGameFolder = "/home/xar/gitrepos/poe_translation/" + data + "/localized/de_patch";

			try {
				List<Path> filesInFolder = Files.walk(Paths.get(baseGameFolder)).filter(Files::isRegularFile).sorted()
						.collect(Collectors.toList());
				List<Path> filesPathFolder = Files.walk(Paths.get(patchGameFolder)).filter(Files::isRegularFile)
						.sorted().collect(Collectors.toList());
				outer: for (Path file : filesInFolder) {
					for (Path patch : filesPathFolder) {
						if (file.getParent().getFileName().equals(patch.getParent().getFileName())
								&& file.getFileName().equals(patch.getFileName())) {
							if (isInterestingFile(file)) {
								this.compare(file, patch);
								System.out.println();
							}
							continue outer;
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		for (String string : this.foundTags) {
			System.out.println(string);
		}
	}

	private void xmlTokenCompare() {
		String data = null;
		for (int i = 0; i < 3; i++) {
			if (i == 0) {
				System.out.println("Basegame");
				data = "data";
			}
			if (i == 1) {
				System.out.println("Expansion 1");
				data = "data_expansion1";
			}
			if (i == 2) {
				System.out.println("Expansion 2");
				data = "data_expansion2";
			}
			String patchGameFolderDE_PATCH = "/home/xar/gitrepos/poe_translation/" + data + "/localized/de_patch";

			languages = new String[] { "en", "de_patch", "es", "it", "pl", "ru", "fr" };
			String baseGameFolder = "/opt/gog/Pillars of Eternity/game/PillarsOfEternity_Data/" + data + "/localized/";

			try {
				int j = 0;
				List<Path> filesPathFolder = Files.walk(Paths.get(baseGameFolder + languages[j]))
						.filter(Files::isRegularFile).sorted().collect(Collectors.toList());

				for (Path file : filesPathFolder) {
					if (isInterestingFile(file)) {
						this.compareFile(file);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}



	/**
	 * Liest syncron über alle Sprachvarianten die gleichen ids. Die Texte in lines können dann verschiedenen Checkfunktionen zugeführt werden.
	 * 
	 * @param file
	 *            - Basis Datei, für die anderen Sprachen wird nur der Language Ordner im Pfad ersetzt.
	 */
	private void compareFile(Path file) {
		// Die / sind wichtig, damit die Endgame Slides nicht kaputt ersetzt werden ;) Da kommt en vor.
		String[] lang = { "/en/", "/de_patch/", "/es/", "/it/", "/pl/", "/ru/", "/fr/" };
		int a = 0;
		List<BufferedReader> readers = new ArrayList<>();
		BufferedReader reader;
		boolean femaleDone;

		try {
			readers.add(Files.newBufferedReader(file));
			for (a = 1; a < lang.length; a++) {
				reader = Files.newBufferedReader(Paths.get(file.toString().replace(lang[0], lang[a])));
				readers.add(reader);
			}
			String[] lines = new String[lang.length];
			String id;
			String[] tagContent = new String[lang.length];
			boolean filePrinted = false;
			while ((lines[0] = readers.get(0).readLine()) != null) {
				if (lines[0].contains("<ID>")) {
					id = lines[0];
					for (a = 1; a < readers.size(); a++) {
						lines[a] = readers.get(a).readLine();
						while (!lines[a].contains(lines[0])) {
							// read Line, sync to number
							lines[a] = readers.get(a).readLine();
						}
					}
					lines[0] = readers.get(0).readLine(); // <DefaultText>
					for (a = 1; a < readers.size(); a++) {
						lines[a] = readers.get(a).readLine(); // <DefaultText>
					}

					for (a = 0; a < readers.size(); a++) {
						if (!lines[a].contains("<DefaultText />")) {
							tagContent[a] = lines[a].trim().replace("<DefaultText>", "").replace("</DefaultText>", "");
							while (!lines[a].contains("</DefaultText>")) {
								lines[a] = readers.get(a).readLine();
								tagContent[a] += lines[a].trim().replace("</DefaultText>", "");
							}

						}
					}
					// Default Text ist immer gefüllt, da reichen die ersten Beiden
					if (tagContent[0] != null && tagContent[1] != null) {
						if (gender % 2 == 0) {
							/* Insert check methods here */
							filePrinted = this.compareTags(tagContent, filePrinted, file, id);
						}
					}

					tagContent = new String[tagContent.length];
					for (a = 0; a < readers.size(); a++) {
						while (!lines[a].contains("<Female")) {
							lines[a] = readers.get(a).readLine();
						}
					}
					for (a = 0; a < readers.size(); a++) {
						if (!lines[a].contains("<FemaleText />")) {
							tagContent[a] = lines[a].trim().replace("<FemaleText>", "").replace("</FemaleText>", "");
							while (!lines[a].contains("</FemaleText>")) {
								lines[a] = readers.get(a).readLine();
								tagContent[a] += lines[a].trim().replace("</FemaleText>", "");
							}
						}
					}
					femaleDone = false;
					// Female Text ist stark sprachabhängig gefüllt.
					if (gender >= 1) {
						for (int i = 0; i < tagContent.length; i++) {
							if (tagContent[i] != null && !femaleDone) {
								/* Insert check methods here */
								filePrinted = this.compareTags(tagContent, filePrinted, file, id);
								femaleDone = true;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			for (BufferedReader br : readers) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Vergleicht Auto Ersetzungs Tags (Genauer: alles in []) über alle Sprachvarianten und schreit bei offensichtlichen Unterschieden. Sonst werden alle gesichteten Tags
	 * ausgegeben. (Könnnte man beruhigen)
	 * 
	 * @param tagContent
	 * @param filePrinted
	 * @param file
	 * @param id
	 * @return
	 */
	private boolean compareTags(String[] tagContent, boolean filePrinted, Path file, String id) {
		String regex = "(\\[.*?\\])*"; // non greedy
		Pattern pattern = Pattern.compile(regex);
		StringBuilder sb = new StringBuilder();
		int oldCount = 0;
		int newCount = 0;
		boolean found = false;
		boolean bad = false;
		for (int a = 0; a < tagContent.length; a++) {
			if (tagContent[a] == null)
				continue;
			Matcher matcher = pattern.matcher(tagContent[a]);
			newCount = 0;
			while (matcher.find()) {
				if (matcher.group() != null && matcher.group().length() > 0) {
					newCount++;
					foundTags.add(matcher.group());
					sb.append(matcher.group() + " ");
					found = true;
				}
			}
			if (a != 0 && oldCount != newCount) {
				// Anzahl stimmt nicht
				System.out.println("Anzahl stimmt nicht!");
				bad = true;
				break;
			}
			oldCount = newCount;
		}
		if (found) {
			System.out.println(file + " ID:" + id);
			System.out.println(sb.toString());
			if (bad) {
				for (int i = 0; i < tagContent.length; i++) {
					System.out.println(languages[i] + ": " + tagContent[i]);
				}
			}
		}
		return false;
	}

	// jaha, fast ein xml reader *hust*
	private void compare(Path file, Path patch) {
		try (BufferedReader fileReader = Files.newBufferedReader(file);
				BufferedReader patchReader = Files.newBufferedReader(patch);) {
			String line;
			String linePatch = "";
			String id;
			String tagContent;
			boolean filePrinted = false;
			while ((line = fileReader.readLine()) != null) {
				if (line.contains("<ID>")) {
					id = line;
					while (!linePatch.contains(line)) {
						// read Line, sync to number
						linePatch = patchReader.readLine();
					}
					line = fileReader.readLine(); // <DefaultText>
					linePatch = patchReader.readLine(); // <DefaultText>
					if (!linePatch.contains("<DefaultText />")) {
						tagContent = linePatch.trim().replace("<DefaultText>", "").replace("</DefaultText>", "");
						while (!linePatch.contains("</DefaultText>")) {
							linePatch = patchReader.readLine();
							tagContent += linePatch.trim().replace("</DefaultText>", "");
						}
						if (gender % 2 == 0) {
							/* Insert check methods here */
							// filePrinted = checkLanguage(tagContent, filePrinted, patch, id);
							// filePrinted = checkForTags(tagContent, filePrinted, patch, id);
							// filePrinted = checkForPairedQuotation(tagContent, filePrinted, patch, id);
							filePrinted = checkDamageKinds(tagContent, filePrinted, patch, id);
						}
					}
					// filePrinted = hasTranslation(file, line, linePatch, id,
					// filePrinted, "DefaultText");
					while (!line.contains("<Female")) {
						line = fileReader.readLine();
					}
					while (!linePatch.contains("<Female")) {
						linePatch = patchReader.readLine();
					}
					if (!linePatch.contains("<FemaleText />")) {
						tagContent = linePatch.trim().replace("<FemaleText>", "").replace("</FemaleText>", "");
						while (!linePatch.contains("</FemaleText>")) {
							linePatch = patchReader.readLine();
							tagContent += linePatch.trim().replace("</FemaleText>", "");
						}
						if (gender >= 1) {
							/* Insert check methods here */
							// filePrinted = checkLanguage(tagContent, filePrinted, patch, id);
							// filePrinted = checkForTags(tagContent, filePrinted, patch, id);
							// filePrinted = checkForPairedQuotation(tagContent, filePrinted, patch, id);
							filePrinted = checkDamageKinds(tagContent, filePrinted, patch, id);
						}
					}
					// filePrinted = hasTranslation(file, line, linePatch, id, filePrinted, "FemaleText");
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Gibts eine Übersetzung, wenn es den englischen Text gibt?
	 * 
	 * @param file
	 * @param line
	 * @param linePatch
	 * @param id
	 * @param filePrinted
	 * @param tokenName
	 * @return
	 */
	private boolean hasTranslation(Path file, String line, String linePatch, String id, boolean filePrinted,
			String tokenName) {
		if (!line.contains("DEBUG") && //
				!line.contains("TEMP") && //
				!line.contains("Trigger Conv Node") && //
				!line.contains("Bank Node") && //
				!line.contains("Random Node") && //
				!line.contains("Script Node")) {
			if (line.contains("<" + tokenName + " />") && !linePatch.contains("<" + tokenName + " />")) {
				// nicht gleich
				if (!filePrinted)
					System.out.println("File: " + file.toString());
				filePrinted = true;
				System.out.println(id);
				System.out.println(line + " vs. " + linePatch);
			}
			if (!line.contains("<" + tokenName + " />") && linePatch.contains("<" + tokenName + " />")) {
				// nicht gleich
				if (!filePrinted)
					System.out.println("File: " + file.toString());
				filePrinted = true;
				System.out.println(id);
				System.out.println(line + " vs. " + linePatch);
			}
		}
		return filePrinted;
	}

	/** Sehr einfacher Sprach check, stellt fest, dass kein Koreanisch und kein Russisch drin ist, da die ein anderes Alphabet benutzen. ^^ */
	public boolean checkLanguage(String text, boolean filePrinted, Path file, String id) {
		String base = "a-zA-Z0-9äüöÄÜÖßẞ„“‚‘…,:\\.?!%–\\{\\}\\[\\]\\(\\)#&; ÁâāàêéèŴŵòôÎî";
		String relaxed = "[" + base + "\\-\"\'*]*";
		String onlyWanted = "[" + base + "]*";
		if (!text.matches(relaxed)) {
			if (!filePrinted)
				System.out.println("File: " + file.toString());
			filePrinted = true;
			System.out.println(id);
			System.out.println(text);
		}
		return filePrinted;
	}

	SortedSet<String> foundTags = new TreeSet<>();
	private String[] languages;

	/**
	 * Replacement Tokens [Player Culture] [Player Deity] [Player Name] [Player Race] [OrlansHeadGame_OpponentLastResult] [OrlansHeadGame_OpponentLastScore]
	 * [OrlansHeadGame_OpponentTotalScore] [OrlansHeadGame_PlayerLastResult] [OrlansHeadGame_PlayerLastScore] [OrlansHeadGame_PlayerTotalScore] [SkillCheck 0] [SkillCheck 1]
	 * [SkillCheck 2] [SkillCheck 3] [Slot 0] [Slot 1] [Slot 2] [Slot 3] [Slot 4] [Slot 5] [Specified 0] [Specified 1] [{0}]
	 * 
	 * @param text
	 * @param filePrinted
	 * @param file
	 * @param id
	 * @return
	 */
	public boolean checkForTags(String text, boolean filePrinted, Path file, String id) {
		String regex = "(\\[.*?\\])*"; // non greedy
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		boolean found = false;
		while (matcher.find()) {
			if (matcher.group() != null && matcher.group().length() > 0) {
				foundTags.add(matcher.group());
				System.out.print(matcher.group() + " ");
				found = true;
			}
		}
		if (found) {
			if (!filePrinted)
				System.out.println("File: " + file.toString());
			filePrinted = true;
			System.out.println(id);
			// System.out.println(text);
		}
		return filePrinted;
	}

	/**
	 * Alle Anführungszeichen in der von uns gewünschten Version? Und Paarweise? Auslassungszeichen sind noch nicht behandelt.
	 * 
	 * @param text
	 * @param filePrinted
	 * @param file
	 * @param id
	 * @return
	 */
	public boolean checkForPairedQuotation(String text, boolean filePrinted, Path file, String id) {
		long resultQuote = text.chars().filter(ch -> ch == '„').count();
		long resultQuote2 = text.chars().filter(ch -> ch == '“').count();
		long result1Quote = text.chars().filter(ch -> ch == '‚').count();
		long result1Quote2 = text.chars().filter(ch -> ch == '‘').count();
		long falseQuotes = text.chars().filter(ch -> ch == '”').count();
		long falseQuotes1 = text.chars().filter(ch -> ch == '’').count(); // TODO und to test
		long falseQuotes2 = text.chars().filter(ch -> ch == '\'').count();
		long falseQuotes3 = text.chars().filter(ch -> ch == '"').count();
		boolean found = false;
		if (falseQuotes2 != 0) {
			found = true;
		}
		// if (resultQuote != resultQuote2 || result1Quote != result1Quote2 || falseQuotes != 0 || falseQuotes3 != 0) {
		// found = true;
		// }
		if (found) {
			if (!filePrinted)
				System.out.println("File: " + file.toString());
			filePrinted = true;
			System.out.println(id);
			System.out.println(text);
		}
		return filePrinted;
	}

	/**
	 * Burn (damage) => Brand(schaden) <br />
	 * Corrode (damage) => Zersetzungs(schaden) <br />
	 * Crush (damage) => Wucht(schaden) <br />
	 * Freeze (damage) => Frost(schaden) <br />
	 * Pierce (damage) => Stich(schaden) <br />
	 * Raw (damage) => Direkt(schaden) <br />
	 * Shock (damage) => Schock(schaden) ? Elektroshock? <br />
	 * Slash (damage) => Hieb(schaden) <br />
	 * 
	 * @param text
	 * @param filePrinted
	 * @param file
	 * @param id
	 * @return
	 */
	public boolean checkDamageKinds(String text, boolean filePrinted, Path file, String id) {
		String lowerCase = text.toLowerCase();
		if (lowerCase.contains("schaden")) {
			if (!(text.contains("Brand") || text.contains("Zersetzungs") || text.contains("Wucht")
					|| text.contains("Frost") || text.contains("Stich") || text.contains("Direkt")
					|| text.contains("Schock") || text.contains("Hieb"))) {
				if (!filePrinted)
					System.out.println("File: " + file.toString());
				filePrinted = true;
				System.out.println(id);
				System.out.println(text);
			}
		}
		return filePrinted;
	}
}
