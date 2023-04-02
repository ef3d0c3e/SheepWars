package org.ef3d0c3e.sheepwars.stats;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;

public class StatDouble extends StatValue
{
	public double value;

	StatDouble()
	{
		super();
		value = 0.0;
	}

	public StatDouble clone()
	{
		final StatDouble s = new StatDouble();
		s.value = this.value;
		return s;
	}

	public String format(final String fmt)
	{
		return MessageFormat.format(fmt, value);
	}

	public String serialize()
	{
		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(6, RoundingMode.HALF_UP);
		return bd.toPlainString();
	}

	public void deserialize(final String in)
	{
		value = Double.valueOf(in);
	}
}
