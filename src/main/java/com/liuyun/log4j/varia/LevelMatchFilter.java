package com.liuyun.log4j.varia;

import com.liuyun.log4j.Level;
import com.liuyun.log4j.helpers.OptionConverter;
import com.liuyun.log4j.spi.Filter;
import com.liuyun.log4j.spi.LoggingEvent;

//日志级别匹配过滤器
public class LevelMatchFilter extends Filter {

    /**
     * Do we return ACCEPT when a match occurs. Default is
     * <code>true</code>.
     */
    boolean acceptOnMatch = true;

    Level levelToMatch;

    public void setLevelToMatch(String level) {
        levelToMatch = OptionConverter.toLevel(level, null);
    }

    public String getLevelToMatch() {
        return levelToMatch == null ? null : levelToMatch.toString();
    }

    public void setAcceptOnMatch(boolean acceptOnMatch) {
        this.acceptOnMatch = acceptOnMatch;
    }

    public boolean getAcceptOnMatch() {
        return acceptOnMatch;
    }


    /**
     * Return the decision of this filter.
     * <p>
     * Returns {@link Filter#NEUTRAL} if the <b>LevelToMatch</b> option
     * is not set or if there is not match.  Otherwise, if there is a
     * match, then the returned decision is {@link Filter#ACCEPT} if the
     * <b>AcceptOnMatch</b> property is set to <code>true</code>. The
     * returned decision is {@link Filter#DENY} if the
     * <b>AcceptOnMatch</b> property is set to false.
     */
    public int decide(LoggingEvent event) {
        if (this.levelToMatch == null) {
            return Filter.NEUTRAL;
        }

        boolean matchOccured = false;
        if (this.levelToMatch.equals(event.getLevel())) {
            matchOccured = true;
        }

        if (matchOccured) {
            if (this.acceptOnMatch) {
                return Filter.ACCEPT;
            } else {
                return Filter.DENY;
            }
        } else {
            return Filter.NEUTRAL;
        }
    }
}
