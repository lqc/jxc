package org.lqc.jxc.tests;

import static org.lqc.jxc.types.PrimitiveType.INT;
import static org.lqc.jxc.types.Type.ANY;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.lqc.jxc.types.FunctionType;
import org.lqc.jxc.types.PrimitiveType;
import org.lqc.jxc.types.Type;
import org.lqc.util.NonUniqueElementException;
import org.lqc.util.POGraph;
import org.lqc.util.PartialyComparable;

public class GraphDrawer<T extends PartialyComparable<T>> extends Canvas 
{
	private POGraph<T> _model;
	private HashMap<POGraph<T>.GraphNode, Point> _pos;
	private Vector<Vector<POGraph<T>.GraphNode>> _layers;
		
	public GraphDrawer(POGraph model) {
		/* Constructor */	
		_model = model;
		_pos = new HashMap<POGraph<T>.GraphNode, Point>();		
		_layers = new Vector<Vector<POGraph<T>.GraphNode>>();
		
		Vector<POGraph<T>.GraphNode> la = new Vector<POGraph<T>.GraphNode>();
		
		HashSet<POGraph<T>.GraphNode> nodes = 
			new HashSet<POGraph<T>.GraphNode>();
		
		nodes.add(_model.root());		
		la.add(_model.root());
		_layers.add(la);		
		
		
		while(! la.isEmpty() ) {
			Vector<POGraph<T>.GraphNode> lb = new Vector<POGraph<T>.GraphNode>();
						
			for(POGraph<T>.GraphNode n : la) {
				for(POGraph<T>.GraphNode c : n.children())
				{
					if(!nodes.contains(c)) {
						lb.add(c);
						nodes.add(c);
					}
				}
			}
			
			la = lb;
			_layers.add(la);			
		}
		
		/* layers filled */
		
		
	}
	
	@Override
	public Dimension getSize() {
		return new Dimension(900, 700);
	}
	
	@Override
	public void paint(Graphics g) {		
		g.setColor(Color.white);
		
		final int width = getWidth();
		final int height = getHeight();
		
		g.clearRect(0, 0, width, height);
		
		final int ystep = height / _layers.size();		
		
		int yoffset = 0;
		for(List<POGraph<T>.GraphNode> l : _layers) {
			if(l.size() == 0) break;
			final int xstep = width / l.size();
			int xoffset = 0;
			for(POGraph<T>.GraphNode n : l) {
				paintNode(g, n, xoffset, yoffset, xstep, ystep);
				xoffset += xstep;
			}
			yoffset += ystep;
		}
		
		paintArrows(g, _model.root());		
	}
	
	public void paintNode(Graphics g, POGraph<T>.GraphNode n, 
			int x, int y, int w, int h)
	{
		if(!_pos.containsKey(n)) {
			g.setColor(Color.black);
			g.drawRect(x+(int)(w*0.1), y+(int)(h*0.1), (int)(w*0.8), (int)(h*0.8));		
			if(n.value() != null)
				g.drawString(n.value().toString(), x+(int)(w*0.2), y+(int)(h*0.2));
			
			_pos.put(n, new Point(x+w/2, y+h/2) );
		}
	}
	
	 
	void paintArrows(Graphics g, POGraph<T>.GraphNode n) {
		Point p = _pos.get(n);				
		
		for(POGraph<T>.GraphNode c : n.children()) {
			Point q = _pos.get(c);
			
			g.drawLine(p.x, p.y, q.x, q.y);
			paintArrows(g, c);			
		}
	}
		

	/**
	 * @param args
	 */
	public static void draw(POGraph g) {
		
		Frame f = new Frame("Graph drawer");
		f.addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}			
		});
		
		f.add( new GraphDrawer(g) );
		f.pack();	
	
		f.setVisible(true);
	}
	
	public static void main(String[] args) 
		throws NonUniqueElementException 
	{
		POGraph<Type> g = new POGraph<Type>();		
		
		g.insert( new FunctionType(ANY, ANY) );		
		//g.insert( new FunctionType(INT, ANY) );
		g.insert( new FunctionType(INT, INT, INT) );
		g.insert( new FunctionType(INT, ANY, ANY) );		
		g.insert( new FunctionType(INT, ANY, INT) );
		g.insert( new FunctionType(INT, INT, ANY) );		
		// g.insert( new FunctionType(ANY, INT, INT) );

		GraphDrawer.draw(g);		
	}

}
