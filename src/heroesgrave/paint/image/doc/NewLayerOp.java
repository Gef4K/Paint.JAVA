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

package heroesgrave.paint.image.doc;

import heroesgrave.paint.gui.LayerManager.LayerNode;
import heroesgrave.paint.main.Paint;

public class NewLayerOp extends DocumentChange
{
	private LayerNode canvas, parent;
	
	public NewLayerOp(LayerNode parent)
	{
		canvas = parent.createNoAdd();
		this.parent = parent;
	}
	
	public void apply()
	{
		parent.restore(canvas);
		Paint.main.gui.layers.redrawTree();
	}
	
	public void revert()
	{
		canvas.deleteNoChange();
		Paint.main.gui.layers.redrawTree();
	}
}