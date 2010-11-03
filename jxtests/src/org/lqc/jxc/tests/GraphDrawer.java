package org.lqc.jxc.tests;

import static org.lqc.jxc.types.PrimitiveType.*;

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
import org.lqc.util.DAGraph;
import org.lqc.util.NonUniqueElementException;
import org.lqc.util.PartiallyOrdered;

public class GraphDrawer<K extends PartiallyOrdered<K>, V> extends Canvas 
{
	private DAGraph<K,V> _model;
	private HashMap<DAGraph<K,V>.Node, Point> _pos;
	private Vector<Vector<DAGraph<K,V>.Node>> _layers;
		
	public GraphDrawer(DAGraph<K,V> model) {
		/* Constructor */	
		_model = model;
		_pos = new HashMap<DAGraph<K,V>.Node, Point>();		
		_layers = new Vector<Vector<DAGraph<K,V>.Node>>();
		
		Vector<DAGraph<K,V>.Node> la = new Vector<DAGraph<K,V>.Node>();
		
		HashSet<DAGraph<K,V>.Node> nodes = 
			new HashSet<DAGraph<K,V>.Node>();
		
		nodes.add(_model.getRoot());		
		la.add(_model.getRoot());
		_layers.add(la);		
		
		
		while(! la.isEmpty() ) {
			Vector<DAGraph<K,V>.Node> lb = new Vector<DAGraph<K,V>.Node>();
						
			for(DAGraph<K,V>.Node n : la) {
				for(DAGraph<K,V>.Node c : n.children())
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
		int width = getWidth();
		int height = getHeight();
				
		g.setColor(Color.black);		
		g.fillRect(0, 0, width, height);
						
		final int ystep = height / _layers.size();		
		
		int yoffset = 0;
		for(List<DAGraph<K,V>.Node> l : _layers) {
			if(l.size() == 0) break;
			final int xstep = width / l.size();
			int xoffset = 0;
			for(DAGraph<K,V>.Node n : l) {
				paintNode(g, n, xoffset, yoffset, xstep, ystep);
				xoffset += xstep;
			}
			yoffset += ystep;
		}
	
		g.setColor(Color.cyan);
		paintArrows(g, _model.getRoot());
		_pos.clear();
	}
	
	public void paintNode(Graphics g, DAGraph<K,V>.Node n, 
			int x, int y, int w, int h)
	{
		if(!_pos.containsKey(n)) {
			g.setColor(Color.white);			
			g.drawRect(x+(int)(w*0.1), y+(int)(h*0.1), (int)(w*0.8), (int)(h*0.8));		
			if(n.value() != null)
				g.drawString(n.value().toString(), x+(int)(w*0.2), y+(int)(h*0.2));
			
			_pos.put(n, new Point(x+w/2, y+h/2) );
		}
	}
	
	 
	void paintArrows(Graphics g, DAGraph<K,V>.Node n) {
		Point p = _pos.get(n);				
		
		for(DAGraph<K,V>.Node c : n.children()) {
			Point q = _pos.get(c);			
			g.drawLine(p.x, p.y, q.x, q.y);
			paintArrows(g, c);			
		}
	}		

	/**
	 * @param args
	 */
	public static void draw(DAGraph g) {
		
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
		DAGraph<FunctionType, FunctionType> g = 
			new DAGraph<FunctionType, FunctionType>();		
		
		FunctionType f;
		
		g.insert( f = new FunctionType(ANY, ANY), f );		
		g.insert( f = new FunctionType(INT, INT, INT), f );
		g.insert( f = new FunctionType(INT, ANY, ANY), f );		
		g.insert( f = new FunctionType(INT, ANY, INT), f );
		g.insert( f = new FunctionType(INT, INT, ANY), f );		
		

		GraphDrawer.draw(g);		
	}

}
