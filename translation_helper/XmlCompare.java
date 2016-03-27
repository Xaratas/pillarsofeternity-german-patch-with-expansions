package poehelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
 * Einfacher Text prüfer für Pillars of Eternity Stringtable Dateien. Könnt man Teilweise in einen Commit Hook wandeln.
 * 
 * @author xar
 *
 */
public class XmlCompare {

	public static void main(String[] args) {
		XmlCompare xmlCompare = new XmlCompare();
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
							if (file.getParent().getFileName().toString().contains("test")
									|| file.getFileName().toString().contains("test")
									|| file.getParent().getFileName().toString().contains("debug")
									|| file.getFileName().toString().contains("debug")
									|| file.getFileName().toString().contains("voice_set")
									|| file.getParent().getFileName().toString().contains("prototype")) {
								continue outer;
							}
							xmlCompare.compare(file, patch);
							System.out.println();
							continue outer;
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		for (String string : xmlCompare.foundTags) {
			System.out.println(string);
		}
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
						// filePrinted = checkLanguage(tagContent, filePrinted, patch, id);
						// filePrinted = checkForTags(tagContent, filePrinted, patch, id);
						// filePrinted = checkForPairedQuotation(tagContent, filePrinted, patch, id);
						filePrinted = checkDamageKinds(tagContent, filePrinted, patch, id);
					}
					// TODO Token Check […en] equals […de] in Reihenfolge und
					// Menge
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
						// filePrinted = checkLanguage(tagContent, filePrinted, patch, id);
						// filePrinted = checkForTags(tagContent, filePrinted, patch, id);
						//filePrinted = checkForPairedQuotation(tagContent, filePrinted, patch, id);
						filePrinted = checkDamageKinds(tagContent, filePrinted, patch, id);
					}
					// filePrinted = hasTranslation(file, line, linePatch, id,
					// filePrinted, "FemaleText");
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Gibts eine Übersetzung wenn es den englischen Text gibt?
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
	 * Alle Anführungszeichen in der von uns gewünschten version? Und Paarweise? Auslassungszeichen sind noch nicht behandelt.
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
