package fr.ensma.lias.rql.dbimport;

/**
 * @author Bilal REZKELLAH
 */
public class MySqlNameNormalizer extends NameNormalizer {

	@Override
	public String colNameNormalizer(String source) {
		source = withoutSpecialChar(withoutAccents(source)).replace(" ", "_");
		while (source.endsWith("_")) {
			source = source.substring(0, source.length() - 1);
		}
		while (source.startsWith("_")) {
			source = source.substring(1, source.length());
		}
		source = source.substring(0, Math.min(source.length(), 64));
		return source;
	}

}
