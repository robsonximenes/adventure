package adventure.util;

import javax.inject.Named;

@Named
public class Constants {

	public static final int NAME_SIZE = 50;

	public int getNameSize() {
		return NAME_SIZE;
	}

	public static final int ABBREVIATION_SIZE = 2;

	public int getAbbreviationSize() {
		return ABBREVIATION_SIZE;
	}

	public static final int EMAIL_SIZE = 255;

	public int getEmailSize() {
		return EMAIL_SIZE;
	}

	public static final int RG_SIZE = 10;

	public int getRgSize() {
		return RG_SIZE;
	}

	public static final int CPF_SIZE = 11;

	public int getCpfSize() {
		return CPF_SIZE;
	}

	public static final int PASSWORD_SIZE = 50;

	public int getPasswordSize() {
		return PASSWORD_SIZE;
	}

	public static final int HASH_SIZE = 64;

	public int getHashSize() {
		return HASH_SIZE;
	}

	public static final int TELEPHONE_SIZE = 15;

	public int getTelephoneSize() {
		return TELEPHONE_SIZE;
	}

	public static final int TEXT_SIZE = 500;

	public int getTextSize() {
		return TEXT_SIZE;
	}

	public static final int GENERIC_ID_SIZE = 20;

	public int getGenericIdSize() {
		return GENERIC_ID_SIZE;
	}

	public static final int ENUM_SIZE = 20;

	public int getEnumSize() {
		return ENUM_SIZE;
	}
	
	public static final int SMALL_DESCRIPTION_SIZE = 100;
	
	public int getSmalDescriptionSize() {
		return SMALL_DESCRIPTION_SIZE;
	}
}
