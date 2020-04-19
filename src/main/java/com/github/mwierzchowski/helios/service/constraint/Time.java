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

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = Time.Validator.class)
public @interface Time {
    String message() default "{helios.constraint.Time}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<Time, String> {
        @Override
        public boolean isValid(String timeValue, ConstraintValidatorContext context) {
            return timeValue == null || isParsable(timeValue);
        }

        boolean isParsable(String timeValue) {
            try {
                return parse(timeValue) != null;
            } catch (Exception ex) {
                return false;
            }
        }
    }
}
