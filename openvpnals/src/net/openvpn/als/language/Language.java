package net.openvpn.als.language;

import java.util.Locale;

/**
 * Encapsulates a single language supported by a language pack.
 * 
 * @see LanguagePackDefinition
 */
public class Language implements Comparable {

	// Private instance variables

	private String code;
	private String description;
	private Locale locale;
	private LanguagePackDefinition pack;

	/**
	 * Constructor
	 * 
	 * @param pack
	 *            the language pack the language is contained in or
	 *            <code>null</code> if language is in the core
	 * @param code
	 *            language code
	 * @param description
	 *            description of language (as presented in the language itself)
	 */
	public Language(LanguagePackDefinition pack, String code, String description) {
		this.code = code;
		this.pack = pack;
		this.description = description;
		String language = code;
		String country = "";
		String variant = "";
		int idx = language.indexOf('_');
		if (idx != -1) {
			country = language.substring(idx + 1);
			language = language.substring(0, idx);
		}
		idx = country.indexOf('_');
		if (idx != -1) {
			variant = country.substring(idx + 1);
			country = country.substring(0, idx);
		}
		locale = new Locale(language, country, variant);
	}
	
	/**
	 * Get the language pack definition or <code>null</code>
	 * if the language is part of the core
	 * 
	 * @return language pack
	 */
	public LanguagePackDefinition getPack() {
		return pack;
	}

	/**
	 * Get a locale object appropriate for this language
	 * 
	 * @return locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Get the code for this language
	 * 
	 * @return code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Get the description for this language.
	 * 
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(T)
	 */
	public int compareTo(Object o) {
		return getDescription().compareTo(((Language) o).getDescription());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		return getCode().equals(((Language) o).getCode());
	}

	/**
	 * Test if the {@link Locale} appropriate for this language equals another
	 * locale
	 * 
	 * @param locale
	 *            locale
	 * @return is same locale
	 */
	public boolean isLocale(Locale locale) {
		return getLocale().equals(locale);
	}
}