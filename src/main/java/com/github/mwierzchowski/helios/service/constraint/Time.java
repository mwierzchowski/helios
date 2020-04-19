package com.github.mwierzchowski.helios.service.constraint;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.time.LocalTime.parse;

/**
 * Constraint for Strings that should contain time.
 * @author Marcin Wierzchowski
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = Time.Validator.class)
public @interface Time {
    String message() default "{helios.constraints.Time}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * Validator implementation
     */
    class Validator implements ConstraintValidator<Time, String> {
        /**
         * Main validator method
         * @param timeValue string with time
         * @param context validation context
         * @return validation result
         */
        @Override
        public boolean isValid(String timeValue, ConstraintValidatorContext context) {
            return timeValue == null || isParsable(timeValue);
        }

        /**
         * Helper method that checks if string can be parsed to {@link java.time.LocalTime}.
         * @param timeValue string to check
         * @return true if parsable, false otherwise
         */
        boolean isParsable(String timeValue) {
            try {
                return parse(timeValue) != null;
            } catch (Exception ex) {
                return false;
            }
        }
    }
}
