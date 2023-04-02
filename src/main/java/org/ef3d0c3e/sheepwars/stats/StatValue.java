package org.ef3d0c3e.sheepwars.stats;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;

public abstract class StatValue
{
	StatValue() {}
	abstract public StatValue clone();

	abstract public String format(final String fmt);
	abstract public String serialize();
	abstract public void deserialize(final String in);
}

