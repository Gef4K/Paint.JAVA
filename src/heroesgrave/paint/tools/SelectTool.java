/*
 *	Copyright 2013 HeroesGrave
 *
 *	This file is part of Paint.JAVA
 *
 *	Paint.JAVA is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>
*/

package heroesgrave.paint.tools;

import heroesgrave.paint.image.MultiChange;
import heroesgrave.paint.image.SelectionCanvas.CombineMode;
import heroesgrave.paint.image.ShapeChange;
import heroesgrave.paint.main.Input;
import heroesgrave.paint.main.Paint;

import java.awt.AlphaComposite;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

public class SelectTool extends Tool
{
	public static enum SelectionType
	{
		RECTANGLE, ELLIPSE
	}
	
	public int sx, sy;
	public Shape shape;
	public JComboBox<SelectionType> type;
	
	public SelectTool(String name)
	{
		super(name);
		
		JLabel label = (JLabel) menu.getComponent(0);
		
		SpringLayout layout = new SpringLayout();
		menu.setLayout(layout);
		
		type = new JComboBox<SelectionType>(new SelectionType[]{SelectionType.RECTANGLE, SelectionType.ELLIPSE});
		type.setFocusable(false);
		
		JLabel label2 = new JLabel("Selection Type:");
		
		menu.add(type);
		menu.add(label2);
		
		layout.putConstraint(SpringLayout.WEST, label, 5, SpringLayout.WEST, menu);
		layout.putConstraint(SpringLayout.WEST, label2, 20, SpringLayout.EAST, label);
		layout.putConstraint(SpringLayout.WEST, type, 20, SpringLayout.EAST, label2);
		layout.putConstraint(SpringLayout.EAST, menu, 0, SpringLayout.EAST, type);
		
		layout.putConstraint(SpringLayout.NORTH, type, -2, SpringLayout.NORTH, menu);
		
		layout.putConstraint(SpringLayout.SOUTH, menu, 0, SpringLayout.SOUTH, type);
	}
	
	public void onPressed(int x, int y, int button)
	{
		sx = x;
		sy = y;
		this.shape = getShape((SelectionType) type.getSelectedItem(), x, y, 1, 1);
		Paint.main.gui.canvas.selector = new MultiChange(new ShapeChange(shape, 0xff0066ff).setFill(true).setComposite(
				AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0x3f / 255f)), new ShapeChange(shape, 0xff001133).setComposite(AlphaComposite
				.getInstance(AlphaComposite.SRC_OVER, 0x7f / 255f)));
	}
	
	public void onReleased(int x, int y, int button)
	{
		int minX = Math.min(x, sx);
		int minY = Math.min(y, sy);
		int width = Math.abs(x - sx);
		int height = Math.abs(y - sy);
		Paint.main.gui.canvas.selector = null;
		shape = null;
		if(width < 1 || height < 1)
		{
			return;
		}
		Paint.main.gui.canvas.selection.create(getShape((SelectionType) type.getSelectedItem(), minX, minY, width, height), getMode());
	}
	
	public void whilePressed(int x, int y, int button)
	{
		int minX = Math.min(x, sx);
		int minY = Math.min(y, sy);
		int width = Math.abs(x - sx);
		int height = Math.abs(y - sy);
		if(shape instanceof Ellipse2D.Float)
		{
			Ellipse2D.Float s1 = (Ellipse2D.Float) shape;
			s1.x = minX;
			s1.y = minY;
			s1.width = width;
			s1.height = height;
		}
		else if(shape instanceof Rectangle2D.Float)
		{
			Rectangle2D.Float s1 = (Rectangle2D.Float) shape;
			s1.x = minX;
			s1.y = minY;
			s1.width = width;
			s1.height = height;
		}
		Paint.main.gui.canvas.getPanel().repaint();
	}
	
	public static CombineMode getMode()
	{
		if(Input.CTRL)
		{
			if(Input.ALT)
			{
				return CombineMode.XOR;
			}
			else
			{
				return CombineMode.ADD;
			}
		}
		else if(Input.ALT)
		{
			return CombineMode.SUBTRACT;
		}
		else if(Input.SHIFT)
		{
			return CombineMode.INTERSECT;
		}
		else
		{
			return CombineMode.REPLACE;
		}
	}
	
	public Shape getShape(SelectionType type, int x, int y, int w, int h)
	{
		switch(type)
		{
			case RECTANGLE:
				return new Rectangle2D.Float(x, y, w, h);
			case ELLIPSE:
				return new Ellipse2D.Float(x, y, w, h);
			default:
				return null;
		}
	}
}