package adventure.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import adventure.entity.TeamFormation;
import br.gov.frameworkdemoiselle.util.Reflections;
import br.gov.frameworkdemoiselle.util.Strings;

public final class Misc {

	private Misc() {
	}

	public static <T> void copyFields(T from, T to) throws Exception {
		for (Field field : Reflections.getNonStaticFields(to.getClass())) {
			if (Reflections.getFieldValue(field, from) != null && Reflections.getFieldValue(field, to) == null) {
				Object value = Reflections.getFieldValue(field, from);

				String setter = "set" + Strings.firstToUpper(field.getName());
				Method method = to.getClass().getMethod(setter, value.getClass());
				method.invoke(to, value);
			}
		}
	}

	private static String stringfy(List<String> members) {
		String memberNames = "";
		for (int i = 0; i < members.size(); i++) {
			String separator;

			if (i == 0) {
				separator = "";
			} else if (i == members.size() - 1) {
				separator = " e ";
			} else {
				separator = ", ";
			}

			memberNames += separator + members.get(i);
		}
		return memberNames;
	}

	public static String stringfyTeamFormation(List<TeamFormation> members) {
		List<String> result = new ArrayList<String>();
		for (TeamFormation teamFormation : members) {
			result.add(teamFormation.getUser().getProfile().getName());
		}

		return stringfy(result);
	}

	public static String capitalize(String string) throws Exception {
		String capitalized = null;

		if (string != null) {
			capitalized = "";

			for (String part : string.toLowerCase().split(" ")) {
				if (part.length() > 2) {
					capitalized += Strings.firstToUpper(part);
				} else {
					capitalized += part;
				}
				capitalized += " ";
			}

			capitalized = capitalized.trim();
		}

		return capitalized;
	}
}
