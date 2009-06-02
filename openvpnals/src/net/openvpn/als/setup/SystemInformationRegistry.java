package net.openvpn.als.setup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SystemInformationRegistry {

	private static SystemInformationRegistry instance;
	private HashMap providers = new HashMap();
	
	public static SystemInformationRegistry getInstance() {
		return instance == null ? instance = new SystemInformationRegistry() : instance;
	}
	
	
	public synchronized void registerProvider(SystemInformationProvider provider) {
		providers.put(provider.getName(), provider);
	}
	
	public synchronized void unregisterProvider(SystemInformationProvider provider) {
		providers.remove(provider.getName());
	}
	
	public List getProviders() {
		return new ArrayList(providers.values());
	}
	
	
	
}
