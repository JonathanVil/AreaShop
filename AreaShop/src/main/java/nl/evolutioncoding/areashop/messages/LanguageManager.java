package nl.evolutioncoding.areashop.messages;

import com.google.common.base.Charsets;
import nl.evolutioncoding.areashop.AreaShop;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.*;

import static nl.evolutioncoding.areashop.messages.Message.CHATLANGUAGEVARIABLE;


public class LanguageManager {
	private AreaShop plugin = null;
	private String languages[] = {"EN", "NL", "DE", "CS", "FR", "FI", "PL"};
	private Map<String, List<String>> currentLanguage, defaultLanguage;

	/**
	 * Constructor
	 * @param plugin The AreaShop plugin
	 */
	public LanguageManager(AreaShop plugin) {
		this.plugin = plugin;
		startup();

	}

	/**
	 * Save the default language files and open the current and backup language file
	 */
	public void startup() {
		this.saveDefaults();
		currentLanguage = loadLanguage(plugin.getConfig().getString("language"));
		defaultLanguage = loadLanguage(languages[0]);
	}

	/**
	 * Saves the default language files if not already present
	 */
	public void saveDefaults() {
		// Create the language folder if it not exists
		File langFolder;
		langFolder = new File(plugin.getDataFolder()+File.separator+AreaShop.languageFolder);
		if(!langFolder.exists()) {
			if(!langFolder.mkdirs()) {
				plugin.getLogger().warning("Could not create language directory: "+langFolder.getAbsolutePath());
				return;
			}
		}

		// Create the language files, overwrites if a file already exists
		// Overriding is necessary because otherwise with an update the new lang
		// files would not be used, when translating your own use another
		// file name as the default
		File langFile;
		for(String language : languages) {
			langFile = new File(plugin.getDataFolder()+File.separator+AreaShop.languageFolder+File.separator+language+".yml");
			try(
					InputStream input = plugin.getResource(AreaShop.languageFolder+"/"+language+".yml");
					OutputStream output = new FileOutputStream(langFile)
			) {
				if(input == null) {
					plugin.getLogger().warning("Could not save default language to the '"+AreaShop.languageFolder+"' folder: "+language+".yml");
					continue;
				}
				int read;
				byte[] bytes = new byte[1024];
				while((read = input.read(bytes)) != -1) {
					output.write(bytes, 0, read);
				}
				input.close();
				output.close();
			} catch(IOException e) {
				plugin.getLogger().warning("Something went wrong saving a default language file: "+langFile.getPath());
			}
		}

	}

	/**
	 * Loads the specified language
	 * @param key The language to load
	 */
	public Map<String, List<String>> loadLanguage(String key) {
		Map<String, List<String>> result = new HashMap<>();

		// Load the strings
		File file = new File(plugin.getDataFolder()+File.separator+AreaShop.languageFolder+File.separator+key+".yml");
		try(
				InputStreamReader reader = new InputStreamReader(new FileInputStream(file), Charsets.UTF_8)
		) {
			YamlConfiguration ymlFile = YamlConfiguration.loadConfiguration(reader);
			if(ymlFile.getKeys(false).isEmpty()) {
				plugin.getLogger().warning("Language file "+key+".yml has zero messages.");
				return result;
			}
			for(String messageKey : ymlFile.getKeys(false)) {
				if(ymlFile.isList(messageKey)) {
					result.put(messageKey, new ArrayList<>(ymlFile.getStringList(messageKey)));
				} else {
					result.put(messageKey, new ArrayList<>(Collections.singletonList(ymlFile.getString(messageKey))));
				}
			}
		} catch(IOException e) {
			plugin.getLogger().warning("Could not load set language file: "+file.getAbsolutePath());
		}
		return result;
	}


	/**
	 * Get the message for a certain key, without doing any processing
	 * @param key The key of the message to get
	 * @return The message as a list of strings
	 */
	public List<String> getRawMessage(String key) {
		List<String> message;
		if(key.equalsIgnoreCase(CHATLANGUAGEVARIABLE)) {
			message = plugin.getChatPrefix();
		} else if(currentLanguage.containsKey(key)) {
			message = currentLanguage.get(key);
		} else {
			message = defaultLanguage.get(key);
		}
		if(message == null) {
			return new ArrayList<>();
		}
		return new ArrayList<>(message);
	}

}

































