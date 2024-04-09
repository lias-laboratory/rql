package fr.ensma.lias.rql.dbimport;

import java.text.Normalizer;

/**
 * @author Bilal REZKELLAH
 */
public abstract class NameNormalizer {

	public String withoutAccents(String source) {
		String normalized = Normalizer.normalize(source, Normalizer.Form.NFD);
		return normalized.replaceAll("[\u0300-\u036F]", "");
	}

	public String withoutSpecialChar(String source) {
		return source.replaceAll("[^a-zA-Z0-9\\s]", "_");
	}

	public abstract String colNameNormalizer(String source);
}
