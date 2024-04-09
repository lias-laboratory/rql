package fr.ensma.lias.rql;

import java.util.Date;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.mindrot.jbcrypt.BCrypt;

/**
 * @author Bilal REZKELLAH
 */
public class RqlUtil {

	public static String hashPassword(String password_plaintext) {
		String salt = BCrypt.gensalt(RqlConstant.WORKLOAD);
		String hashed_password = BCrypt.hashpw(password_plaintext, salt);

		return (hashed_password);
	}

	public static boolean checkPassword(String newPassword, String old) {
		return BCrypt.checkpw(newPassword, old);
	}

	public static String encryptToken(AuthToken currentToken, String password, String noise) {
		if (currentToken != null && !currentToken.isValid()) {
			return null;
		}

		return RqlUtil.encrypt(currentToken.getIdentifiant() + RqlConstant.AUTH_TOKEN_SEPARATOR
				+ currentToken.getStartDateValue() + RqlConstant.AUTH_TOKEN_SEPARATOR + noise, password);
	}

	public static AuthToken decryptToken(String value, String password, String noise) {
		if (value == null) {
			return null;
		}

		AuthToken currentToken = null;
		try {
			final String decrypt = RqlUtil.decrypt(value, password);

			if (decrypt == null) {
				return null;
			}

			final String[] split = decrypt.split(RqlConstant.AUTH_TOKEN_SEPARATOR);

			if (split == null || split.length != 3) {
				return null;
			}

			String previousIdentifiant = split[0];
			Date previousStartDate = new Date(Long.parseLong(split[1]));
			String previousNoise = split[2];

			if (!noise.equals(previousNoise)) {
				return null;
			}

			currentToken = new AuthToken(previousIdentifiant, previousStartDate);
			return currentToken;
		} catch (Exception e) {
			return null;
		}
	}

	public static String encrypt(String content, String password) {
		return RqlUtil.getEncryptor(password).encrypt(content);
	}

	public static String decrypt(String content, String password) {
		return RqlUtil.getEncryptor(password).decrypt(content);
	}

	private static StandardPBEStringEncryptor getEncryptor(String password) {
		StandardPBEStringEncryptor cookieEncryptor = new StandardPBEStringEncryptor();
		cookieEncryptor.setStringOutputType("hexadecimal");
		cookieEncryptor.setPassword(password);
		return cookieEncryptor;
	}
}
