package com.liuyun.log4j.varia;

import com.liuyun.log4j.Level;
import com.liuyun.log4j.spi.Filter;
import com.liuyun.log4j.spi.LoggingEvent;

//级别分类过滤器
public class LevelRangeFilter extends Filter {

    /**
     * Do we return ACCEPT when a match occurs. Default is
     * <code>false</code>, so that later filters get run by default
     */
    boolean acceptOnMatch = false;

    Level levelMin;
    Level levelMax;


    /**
     * Return the decision of this filter.
     */
    public int decide(LoggingEvent event) {
        if (this.levelMin != null) {
            if (event.getLevel().isGreaterOrEqual(levelMin) == false) {
                // level of event is less than minimum
                return Filter.DENY;
            }
        }

        if (this.levelMax != null) {
            if (event.getLevel().toInt() > levelMax.toInt()) {
                // level of event is greater than maximum
                // Alas, there is no Level.isGreater method. and using
                // a combo of isGreaterOrEqual && !Equal seems worse than
                // checking the int values of the level objects..
                return Filter.DENY;
            }
        }

        if (acceptOnMatch) {
            // this filter set up to bypass later filters and always return
            // accept if level in range
            return Filter.ACCEPT;
        } else {
            // event is ok for this filter; allow later filters to have a look..
            return Filter.NEUTRAL;
        }
    }

    /**
     * Get the value of the <code>LevelMax</code> option.
     */
    public Level getLevelMax() {
        return levelMax;
    }


    /**
     * Get the value of the <code>LevelMin</code> option.
     */
    public Level getLevelMin() {
        return levelMin;
    }

    /**
     * Get the value of the <code>AcceptOnMatch</code> option.
     */
    public boolean getAcceptOnMatch() {
        return acceptOnMatch;
    }

    /**
     * Set the <code>LevelMax</code> option.
     */
    public void setLevelMax(Level levelMax) {
        this.levelMax = levelMax;
    }

    /**
     * Set the <code>LevelMin</code> option.
     */
    public void setLevelMin(Level levelMin) {
        this.levelMin = levelMin;
    }

    /**
     * Set the <code>AcceptOnMatch</code> option.
     */
    public void setAcceptOnMatch(boolean acceptOnMatch) {
        this.acceptOnMatch = acceptOnMatch;
    }
}

