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

package heroesgrave.paint.image;

import heroesgrave.paint.gui.Menu;
import heroesgrave.paint.main.Paint;
import heroesgrave.utils.math.MathUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.TexturePaint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class CanvasManager
{
	public static BufferedImage transparencyBG;
	public static BufferedImage transparencyBGDark;
	
	private IFrame preview;
	private Canvas root;
	private CanvasRenderer panel;
	private Canvas selected;
	private int width, height;
	public SelectionManager selection;
	public MultiChange selector;
	
	private float scale = 1;
	
	public CanvasManager()
	{
		panel = new CanvasRenderer(this);
		selection = new SelectionManager();
		
		setImage(new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB));
	}
	
	public void preview(IFrame frame)
	{
		preview = frame;
		panel.repaint();
	}
	
	public void applyPreview()
	{
		if(preview == null)
			return;
		Paint.addChange(preview);
		panel.repaint();
		preview = null;
	}
	
	public void incZoom()
	{
		float oldScale = scale;
		
		JScrollPane pane = Paint.main.gui.scroll;
		
		if(scale < 1)
		{
			scale = scale + 1 / 10f;
		}
		else if(scale < 32)
		{
			scale = (int) scale + 1;
		}
		
		panel.setScale(scale);
		panel.revalidate();
		
		float factor = scale / oldScale;
		
		Point point = pane.getMousePosition();
		if(point != null)
		{
			Point pos = pane.getViewport().getViewPosition();
			
			int newX = (int) (point.x * (factor - 1f) + factor * pos.x);
			int newY = (int) (point.y * (factor - 1f) + factor * pos.y);
			pane.getViewport().setViewPosition(new Point(newX, newY));
		}
		panel.repaint();
	}
	
	public void decZoom()
	{
		float oldScale = scale;
		
		JScrollPane pane = Paint.main.gui.scroll;
		
		if(scale > 1)
		{
			scale = (int) scale - 1;
		}
		else if(scale > 1 / 10f)
		{
			scale = scale - 1 / 10f;
		}
		
		panel.setScale(scale);
		panel.revalidate();
		
		float factor = scale / oldScale;
		
		Point point = pane.getMousePosition();
		if(point != null)
		{
			Point pos = pane.getViewport().getViewPosition();
			
			int newX = (int) (point.x * (factor - 1f) + factor * pos.x);
			int newY = (int) (point.y * (factor - 1f) + factor * pos.y);
			pane.getViewport().setViewPosition(new Point(newX, newY));
		}
		panel.repaint();
	}
	
	public Canvas getCanvas()
	{
		return selected;
	}
	
	public Canvas getRoot()
	{
		return root;
	}
	
	public void setImage(BufferedImage image)
	{
		this.selected = this.root = new Canvas("Background", image);
		if(Paint.main.gui.layers != null)
			Paint.main.gui.layers.setRoot(this.root);
		this.width = root.getWidth();
		this.height = root.getHeight();
		panel.init();
		panel.setScale(scale);
		panel.repaint();
		panel.revalidate();
	}
	
	public void setRoot(Canvas canvas)
	{
		this.selected = this.root = canvas;
		if(Paint.main.gui.layers != null)
			Paint.main.gui.layers.setRoot(this.root);
		this.width = root.getWidth();
		this.height = root.getHeight();
		panel.init();
		panel.setScale(scale);
		panel.repaint();
		panel.revalidate();
	}
	
	public CanvasRenderer getPanel()
	{
		return panel;
	}
	
	public float getScale()
	{
		return scale;
	}
	
	public static class CanvasRenderer extends JPanel
	{
		private static final long serialVersionUID = 6250531364061875156L;
		
		private CanvasManager mgr;
		private float scale = 1;
		private int lastButton = 0;
		private BufferedImage background;
		
		public static final Color TRANSPARENT = new Color(255, 255, 255, 0);
		
		public CanvasRenderer(CanvasManager mgr)
		{
			this.mgr = mgr;
			this.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mousePressed(MouseEvent e)
				{
					lastButton = e.getButton();
					Paint.main.currentTool.onPressed(MathUtils.floor(e.getX() / scale), MathUtils.floor(e.getY() / scale), e.getButton());
				}
				
				@Override
				public void mouseReleased(MouseEvent e)
				{
					lastButton = e.getButton();
					Paint.main.currentTool.onReleased(MathUtils.floor(e.getX() / scale), MathUtils.floor(e.getY() / scale), e.getButton());
				}
			});
			this.addMouseMotionListener(new MouseMotionListener()
			{
				@Override
				public void mouseDragged(MouseEvent e)
				{
					Paint.main.currentTool.whilePressed(MathUtils.floor(e.getX() / scale), MathUtils.floor(e.getY() / scale), lastButton);
				}
				
				@Override
				public void mouseMoved(MouseEvent e)
				{
					Paint.main.currentTool.whileReleased(MathUtils.floor(e.getX() / scale), MathUtils.floor(e.getY() / scale), lastButton);
				}
			});
		}
		
		public void init()
		{
			background = new BufferedImage(mgr.getWidth(), mgr.getHeight(), BufferedImage.TYPE_INT_ARGB);
		}
		
		public void setScale(float scale)
		{
			this.setPreferredSize(new Dimension(MathUtils.floor(mgr.getWidth() * scale), MathUtils.floor(mgr.getHeight() * scale)));
			this.scale = scale;
		}
		
		@Override
		public void paint(Graphics arg0)
		{
			super.paint(arg0);
			
			Graphics2D g = (Graphics2D) arg0;
			Graphics2D draw = background.createGraphics();
			draw.setBackground(TRANSPARENT);
			draw.clearRect(0, 0, background.getWidth(), background.getHeight());
			
			g.setPaint(new TexturePaint(Menu.DARK_BACKGROUND ? transparencyBGDark : transparencyBG, new Rectangle2D.Float(0, 0, 16, 16)));
			g.fillRect(0, 0, MathUtils.floor(mgr.getWidth() * scale), MathUtils.floor(mgr.getHeight() * scale));
			
			g.setPaint(null);
			
			mgr.root.draw(draw, true);
			
			if(mgr.selector != null)
			{
				mgr.selector.apply(background);
			}
			
			g.drawImage(background, 0, 0, MathUtils.floor(background.getWidth() * scale), MathUtils.floor(background.getHeight() * scale), null);
			
			if(Menu.GRID_ENABLED && scale >= 4)
			{
				g.setColor(Color.gray);
				for(int i = 0; i < background.getWidth(); i++)
				{
					g.drawLine(MathUtils.floor(i * scale), 0, MathUtils.floor(i * scale), MathUtils.floor(background.getHeight() * scale));
				}
				for(int j = 0; j < background.getHeight(); j++)
				{
					g.drawLine(0, MathUtils.floor(j * scale), MathUtils.floor(background.getWidth() * scale), MathUtils.floor(j * scale));
				}
			}
		}
	}
	
	static
	{
		try
		{
			transparencyBG = ImageIO.read(CanvasManager.class.getResource("/heroesgrave/paint/res/tbg.png"));
		}
		catch(IOException e)
		{
			e.printStackTrace();
			transparencyBG = null;
		}
		
		transparencyBGDark = new BufferedImage(2, 2, BufferedImage.TYPE_BYTE_GRAY);
		transparencyBGDark.setRGB(0, 0, 0x777777);
		transparencyBGDark.setRGB(1, 1, 0x777777);
		transparencyBGDark.setRGB(1, 0, 0x555555);
		transparencyBGDark.setRGB(0, 1, 0x555555);
	}
	
	public void select(Canvas canvas)
	{
		if(canvas == selected)
			return;
		if(!this.selection.isSelection(canvas))
			this.selection.drop();
		this.selected = canvas;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public void selectRoot()
	{
		if(root == selected)
			return;
		this.selection.drop();
		this.selected = this.root;
	}
	
	public Canvas getParentOf(Canvas c)
	{
		if(c == root)
			return null;
		return root.getParentOf(c);
	}
	
	public void setDimensions()
	{
		root.checkImage();
		this.width = root.getWidth();
		this.height = root.getHeight();
		panel.init();
		panel.setScale(scale);
		panel.repaint();
		panel.revalidate();
	}
	
	public IFrame getPreview()
	{
		return preview;
	}
}