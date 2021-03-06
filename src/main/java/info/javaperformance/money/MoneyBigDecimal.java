/*
* Copyright 2014 Mikhail Vorontsov
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package info.javaperformance.money;

//import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Safe but slow Money implementation. Uses BigDecimal as a storage.
 */
//@JsonSerialize(using = JsonHelpers.MoneySerializer.class)
class MoneyBigDecimal extends AbstractMoney {
    private final BigDecimal m_value;
    private static MathContext CTX = new MathContext(9, RoundingMode.DOWN);

    public MoneyBigDecimal( final BigDecimal value ) {
        m_value = value;
    }

    public MoneyBigDecimal( final double value )
    {
        m_value = new BigDecimal( value, MathContext.DECIMAL64 ).stripTrailingZeros(); //decimal64 to match double
    }

    public MoneyBigDecimal( final String value )
    {
        //important - do not use DECIMAL64 context here - you will lose precision for huge values.
        //at the same time using it is required for BigDecimal(double) constructor - it matches "double" range.
        m_value = new BigDecimal( value );
    }

    public double toDouble() {
        return m_value.doubleValue();
    }

    /**
     * Convert this value into a BigDecimal. This method is also used for arithmetic calculations when necessary.
     *
     * @return This object as BigDecimal
     */
    public BigDecimal toBigDecimal() {
        return m_value;
    }

    /**
     * Return this value with an opposite sign.
     *
     * @return A new object with the same value with a different sign
     */
    public Money negate() {
        return new MoneyBigDecimal( m_value.negate() );
    }

    /**
     * Convert into a String in a plain notation with a decimal dot.
     * @return a String in a plain notation with a decimal dot.
     */
    @Override
    public String toString() {
        return m_value.toPlainString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MoneyBigDecimal that = (MoneyBigDecimal) o;

        return m_value.equals(that.m_value);
    }

    @Override
    public int hashCode() {
        return m_value.hashCode();
    }

    protected Money add( final MoneyLong other )
    {
        return other.add( this ); //implemented in MoneyLong
    }

    @Override
    protected int compareTo(MoneyLong other) {
        return -(other.compareTo(this)); // flips the response with unary
    }

    /**
     * Multiply the current object by the <code>long</code> value.
     *
     * @param multiplier Multiplier
     * @return A new Money object normalized to the efficient representation if possible
     */
    public Money multiply( final long multiplier ) {
        final BigDecimal res = m_value.multiply(BigDecimal.valueOf(multiplier));
        return MoneyFactory.fromBigDecimal( res );
    }

    /**
     * Multiply the current object by the <code>double</code> value.
     *
     * @param multiplier Multiplier
     * @return A new Money object normalized to the efficient representation if possible
     */
    public Money multiply(double multiplier) {
        return MoneyFactory.fromBigDecimal(
                m_value.multiply(new BigDecimal(multiplier, MathContext.DECIMAL64), MathContext.DECIMAL64) );
    }

    @Override
    public Money multiply(final Money multiplier) {
        return new MoneyBigDecimal(m_value.multiply(multiplier.toBigDecimal()));
    }
    
    @Override
    public Money multiplyLimitedPrecision(final Money multiplier, final int digitCount) {
        return new MoneyBigDecimal(m_value.multiply(multiplier.toBigDecimal(), 
                new MathContext(digitCount, RoundingMode.DOWN)).stripTrailingZeros());
    }
    
    @Override
    public Money multiplyLimitedScale( final Money multiplier, final int scale ) {
        BigDecimal res = m_value.multiply(multiplier.toBigDecimal()).setScale(scale, RoundingMode.DOWN);
        return new MoneyBigDecimal(res.stripTrailingZeros());
    }
    

    /**
     * Divide the current object by the given <code>long</code> divider.
     *
     * @param divider   Divider
     * @param precision Maximal precision to keep. We will round the next digit.
     * @return A new Money object normalized to the efficient representation if possible
     */
    public Money divide( final long divider, final int precision )
    {
        final BigDecimal res = m_value.divide( BigDecimal.valueOf( divider ), MathContext.DECIMAL64 ).stripTrailingZeros();
        return truncate( res, precision );
    }

    /**
     * Divide the current object by the given <code>long</code> divider.
     *
     * @param divider   Divider
     * @param precision Maximal precision to keep. We will round the next digit.
     * @return A new Money object normalized to the efficient representation if possible
     */
    public Money divide( final double divider, final int precision )
    {
        final BigDecimal res = m_value.divide( BigDecimal.valueOf( divider ), MathContext.DECIMAL64 ).stripTrailingZeros();
        return truncate( res, precision );
    }

    /**
     * Truncate the current value leaving no more than {@code maximalPrecision} signs after decimal point.
     * The number will be rounded towards closest digit (0-4 -> 0; 5-9 -> 1)
     *
     * @param maximalPrecision Required precision
     * @return A new Money object normalized to the efficient representation if possible
     */
    private static Money truncate( final BigDecimal val, final int maximalPrecision ) {
        MoneyFactory.checkPrecision( maximalPrecision );

        final BigDecimal res = val.setScale( maximalPrecision, BigDecimal.ROUND_HALF_UP );
        return MoneyFactory.fromBigDecimal( res );
    }

    /**
     * Truncate the current value leaving no more than {@code maximalPrecision} signs after decimal point.
     * The number will be rounded towards closest digit (0-4 -> 0; 5-9 -> 1)
     *
     * @param maximalPrecision Required precision
     * @return A new Money object normalized to the efficient representation if possible
     */
    public Money truncate( final int maximalPrecision ) {
        if ( m_value.scale() <= maximalPrecision )
            return this;
        return truncate(m_value, maximalPrecision);
    }
    
        
    /**
     * Truncate the current value leaving no more than {@code maximalPrecision} signs after decimal point.
     * The number will be rounded towards closest digit (0-4 -> 0; 5-9 -> 1)
     *
     * @param maximalPrecision Required precision
     * @return A new Money object normalized to the efficient representation if possible
     */
    public Money ceil(final int maximalPrecision ) {
        MoneyFactory.checkPrecision( maximalPrecision );

        final BigDecimal res = m_value.setScale( maximalPrecision, BigDecimal.ROUND_UP );
        return MoneyFactory.fromBigDecimal( res );
    }

    @Override
    public int signum() {
        return m_value.signum();
    }


	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeUTF(m_value.toString());
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException  {
		try {
			this.getClass().getField("m_value").setAccessible(true);
			this.getClass().getField("m_value").set(this, new BigDecimal(in.readUTF()));
			this.getClass().getField("m_value").setAccessible(false);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
			Logger.getLogger(MoneyBigDecimal.class.getName()).log(Level.SEVERE, "Serialization failed for MoneyBigDecimal.", ex);
		}
	}
    
    @Override
	public boolean isZero() {
		return signum() == 0;
    }

	@Override
	public long unscaledValue() {
		return m_value.longValue();
	}

    @Override
    public int getScale() {
        // in BigDecimal's terms precision is the number of digits in total, scale is the number of digits to the right of the decimal point
        return m_value.scale();
    }

    @Override
    public Money divide(Money divider, int scale) {
        return MoneyFactory.fromBigDecimal(m_value.divide(divider.toBigDecimal(), scale, RoundingMode.DOWN));
    }
}
