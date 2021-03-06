package com.github.mwierzchowski.helios.service.constraint;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.DayOfWeek;
import java.util.Set;
import java.util.stream.Stream;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.stream.Collectors.toSet;

/**
 * Constraint for Strings that should contain name of week.
 * @author Marcin Wierzchowski
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = WeekDay.Validator.class)
public @interface WeekDay {
    String message() default "{helios.constraints.WeekDay}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default { };

    /**
     * Validator implementation
     */
    class Validator implements ConstraintValidator<WeekDay, String> {
        /**
         * Keeps list of allowed values (day names)
         */
        private static Set<String> daysOfWeek = Stream.of(DayOfWeek.values())
                .map(Enum::toString)
                .collect(toSet());

        /**
         * Main validator method
         * @param dayName string with day name
         * @param context validation context
         * @return validation result
         */
        @Override
        public boolean isValid(String dayName, ConstraintValidatorContext context) {
            return dayName == null || daysOfWeek.contains(dayName);
        }
    }
}
