package yurii.karpliuk.phoneContacts.validator;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumberValidator {
    private static final String PHONE_NUMBER_PATTERN = "^\\+380\\d{9}$";
    private static final Pattern pattern = Pattern.compile(PHONE_NUMBER_PATTERN);
    public static boolean isValid(final String phone)  {
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

}
