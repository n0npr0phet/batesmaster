package com.batesmaster;

import com.lowagie.text.Rectangle;


/**
 * float x,y storage.
 * @author Mark Manoukian
 * @author Gregory Pruden
 * @version 0.1
 *
 * copyright 2009 Mark Manoukian and Gregory Pruden
 * 
 *
 */
public class Origin {
	public float x=0.0f;
	public float y=0.0f;
	
	/***
	 * default constructor
	 */
	public Origin()
	{
		
	}
	
	/***
	 * float point constructor
	 * @param newx x point
	 * @param newy y point
	 */
	public Origin(float newx, float newy)
	{
		x = newx;
		y = newy;
	}
	
	/***
	 * special iText page constructor
	 * @param page	iText pdf reader page rectangle
	 * @param originoption	our origin option 0-8
	 */
	public Origin(Rectangle page, int originoption)
	{
		Origin o = ComputeFromPage(page, originoption);
		x = o.x;
		y = o.y;
	}
	
	/***
	 * sets the x point of the origin
	 * @param newValue
	 */
	public void setx(float newValue)
	{
		x = newValue;
	}
	
	/***
	 * sets the y point of the origin
	 * @param newValue
	 */
	public void sety(float newValue)
	{
		y = newValue;
	}
	
	// some origin arithmetic
	
	/***
	 * add two origins return result
	 * @param o the origin to add to this origin
	 * @return the result of the addition
	 */
	public Origin Add(Origin o)
	{
		return new Origin(o.x + this.x, o.y+this.y);
	}
	
	/***
	 * Subtract parameter origin from this origin
	 * @param o the origin to subtract
	 * @return the result
	 */
	public Origin Subtract(Origin o)
	{
		return new Origin(this.x-o.x, this.y-o.y);
	}	
	
	/***
	 * Multiply parameter origin times this origin
	 * @param o the origin to multiply by
	 * @return the resulting origin
	 */
	public Origin Multiply(Origin o)
	{
		return new Origin(o.x * this.x, o.y * this.y);
	}
	
	/***
	 * DivideBy parameter origin
	 * @param o origin to divide by (no zeros)
	 * @return the resulting origin
	 */
	public Origin DivideBy(Origin o)  throws ArithmeticException 
	{
		if (o.x==0.0f) throw new ArithmeticException("x cannot be zero.");
		if (o.x==0.0f) throw new ArithmeticException("y cannot be zero.");
		
		return new Origin(this.x/o.x, this.y/o.y);
	}	
	
	public Origin ComputeFromPage(Rectangle page, int originOption)
	{
			float x = 0;
			float y = 0;
			switch (originOption)
			{
			case 0:
				break;
			case 1:
				x += page.getWidth()/2.0f;
				break;
			case 2:
				x += page.getWidth();
				break;
			case 3:
				x += page.getWidth();
				y += page.getHeight()/2.0f;
				break;
			case 4:
				x += page.getWidth();
				y += page.getHeight();
				break;
			case 5:
				x += page.getWidth()/2.0f;
				y += page.getHeight();
				break;
			case 6:
				y += page.getHeight();
				break;
			case 7:
				y += page.getHeight()/2.0f;
				break;
			case 8:
				x += page.getWidth()/2.0f;
				y += page.getHeight()/2.0f;		
				break;
			default:
				break;
			}	
			return new Origin(x,y);
		}

}

