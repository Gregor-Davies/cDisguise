package dev.brighten.hide.game;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

public class UUIDFetcher {
	
	private static String profile_url = "https://api.mojang.com/users/profiles/minecraft/";
	private static HashMap<String, UUID> cache = new HashMap<>();
	
	public static UUID fetchUUID(String name) throws Exception {
		if(cache.containsKey(name)) {
			return cache.get(name);
		}
		URL url = new URL(profile_url + name);
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		
		String line = reader.readLine();
		
		String[] input = line.split(":");
		String[] id = null;
		if(input.length > 8) {
			id = input[2].split(",");
		} else {
			id = input[1].split(",");
		}
		
		String uuid = id[0].replace("\"", "");
		
		UUID finaluuid = UUID.fromString(uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32));
		
		return finaluuid;
	}
}