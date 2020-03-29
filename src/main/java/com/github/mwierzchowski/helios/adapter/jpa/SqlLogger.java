package com.github.mwierzchowski.helios.adapter.jpa;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.P6Logger;
import lombok.extern.slf4j.Slf4j;

import static com.p6spy.engine.common.P6Util.singleLine;
import static com.p6spy.engine.logging.Category.STATEMENT;
import static java.util.regex.Matcher.quoteReplacement;

/**
 * SQL queries logger. Queries are logged at DEBUG level for easier filtering.
 * @author Marcin Wierzchowski
 */
@Slf4j
public class SqlLogger implements P6Logger {
    @Override
    public void logSQL(int connId, String now, long elapsed, Category category, String prepared, String sql, String url) {
        log.debug(category.equals(STATEMENT) ? quoteReplacement(singleLine(sql)) : category.getName());
    }

    @Override
    public void logException(Exception ex) {
        log.debug("", ex);
    }

    @Override
    public void logText(String text) {
        log.debug(text);
    }

    @Override
    public boolean isCategoryEnabled(Category category) {
        return log.isDebugEnabled();
    }
}