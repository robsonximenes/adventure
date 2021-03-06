package adventure.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import adventure.persistence.UserDAO;
import br.gov.frameworkdemoiselle.util.Strings;

public class ExistentUserEmailValidator implements ConstraintValidator<ExistentUserEmail, String> {

	@Override
	public void initialize(final ExistentUserEmail annotation) {
	}

	@Override
	public boolean isValid(final String email, final ConstraintValidatorContext context) {
		boolean result = true;

		if (!Strings.isEmpty(email)) {
			result = UserDAO.getInstance().load(email) != null;
		}

		return result;
	}
}
