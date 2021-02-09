package com.dres.gon;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.NotSerializableException;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.dres.grpx.GradientIcon2D;
import com.dres.grpx.Txt;
import com.dres.util.Apt;


class SelectionBar extends JPanel{
	/**
	 * 
	 */
	//private HexaGonWild hexaGonWild;
	private static final long serialVersionUID = 1L;
	private static final double PI = Math.PI;
	private void writeObject(java.io.ObjectOutputStream out)
		     throws IOException{
		throw new NotSerializableException();
	}
	protected JTextField
		durationBox,
		pauseBox,
		sizeBox,
		rowsBox,
		stageBox,
		delayBox,
		gradientR,
		distanceBoxX,
		distanceBoxY,
		lagReductionBox,
		timingBox,
		offsetBox,
		rotationBox;
	protected JCheckBox
		movesLeft,
		antiAliasingBox,
		bouncesColor,
		cacheBox,
		vRamBox;
	protected Box self;
	int padding = 5;
	Preset p;
	TheGon hgw;
	GradientIcon2D gi2d;
	AdvancedPolygonEditor ape;
	public void refresh(){
		internalRefresh();
	}
	public void refresh(Preset s){
		if(p != null)
			p.getPolygon().clearUpdates();
		p = s;
		internalRefresh();
		p.getPolygon().onChange(new Apt(){
			@Override
			public void call() {
				hgw.initCalcs();
			}
		});
	}
	private void internalRefresh(){
		durationBox.setText("" + p.getBaseDuration());
		sizeBox.setText("" + p.getSizeFactor());
		pauseBox.setText("" + p.getBasePause());
		rowsBox.setText("" + p.getRows());
		stageBox.setText("" + p.getStages());
		delayBox.setText("" + p.getBaseReactionDelay());
		gradientR.setText("" + p.getGradientRepeats());
		distanceBoxX.setText("" + p.getDistanceMultiplierX());
		distanceBoxY.setText("" + p.getDistanceMultiplierY());
		lagReductionBox.setText("" + p.getLagReduction());
		timingBox.setText("" + p.getBaseMultiplier());
		offsetBox.setText("" + p.getOffset() / PI);
		rotationBox.setText("" + p.getRotationAmt() / (2 * PI));
		movesLeft.setSelected(p.isDirectionLeft());
		antiAliasingBox.setSelected(p.isAntiAliasing());
		bouncesColor.setSelected(p.isBouncing());
		cacheBox.setSelected(p.caches());
		vRamBox.setSelected(p.vRAM());
		//vRamBox.setEnabled(p.caches());
		gi2d.changeTo(p);
		ape.changeTo(p.getPolygon());
		ape.repaint();
	}
	public SelectionBar(TheGon tgw, Preset s){
		this.hgw = tgw;
		p = s;
		Box b = self = Box.createVerticalBox();
		durationBox = new Txt(new Apt(){
			@Override
			public void call() {
				durationAction();
		}});
		sizeBox = new Txt(new Apt(){
			@Override
			public void call() {
				sizeAction();
		}});
		pauseBox = new Txt(new Apt(){
			@Override
			public void call(){
				pauseAction();
			}
		});
		rowsBox = new Txt(new Apt(){
			@Override
			public void call(){
				rowAction();
			}
		});
		stageBox = new Txt(new Apt(){
			@Override
			public void call(){
				stageAction();
			}
		});
		delayBox = new Txt(new Apt(){
			@Override
			public void call() {
				delayAction();
			}
		});
		gradientR = new Txt(new Apt(){
			@Override
			public void call() {
				gradientRAction();
			}
		}, 4);
		distanceBoxX = new Txt(new Apt(){
			@Override
			public void call() {
				distanceActionX();
			}
		});
		distanceBoxY = new Txt(new Apt(){
			@Override
			public void call() {
				distanceActionY();
			}
		});
		lagReductionBox = new Txt(new Apt(){
			@Override
			public void call(){
				lagReductionAction();
			}
		}, 4);
		timingBox = new Txt(new Apt(){
			@Override public void call(){
				timingMultAction();
			}
		});
		offsetBox = new Txt(
			new Apt(){
				public void call(){
					offsetAction();
				}
			}
		);
		rotationBox = new Txt(new Apt(){
			@Override
			public void call() {
				rotationAction();
			}
		});
		cacheBox = new JCheckBox("Caches", p.caches());
		cacheBox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cacheAction();
			}});
		antiAliasingBox = new JCheckBox("Anti-Aliasing", p.isAntiAliasing());
		antiAliasingBox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AAAction();
			}});
		movesLeft = new JCheckBox("RotationLeft", p.isDirectionLeft());
		movesLeft.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				movesLeftAction();
			}});
		bouncesColor = new JCheckBox("Color Bounces", p.isBouncing());
		bouncesColor.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				bouncesAction();
			}});

		vRamBox = new JCheckBox("V-RAM caching", p.vRAM());
		vRamBox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				vRAMAction();
			}});
		vRamBox.setEnabled(false);
		add("Duration:  ", 				durationBox);
		add("Pause Time:  ", 			pauseBox);
		add("Distance Delay:  ", 		delayBox);
		add("Multiplier(by above 3):  ",timingBox);
		add("Stages:  ", 				stageBox);
		add("Lag Reduction Factor:  ",	lagReductionBox);
		add("Num Rows:  ", 				rowsBox);
		add("Size Factor:  ", 			sizeBox);
		add("Distance Factor X:  ",		distanceBoxX);
		add("Distance Factor  Y:  ",	distanceBoxY);
		add("Offset:  ",				offsetBox);
		add("Rotation Amt:  ",			rotationBox);
		b.add(tst);
		b.add(Box.createVerticalStrut(padding));
		JPanel fl = new JPanel(new GridLayout(2, 3));
		fl.add(movesLeft);
		fl.add(antiAliasingBox);
		fl.add(cacheBox);
		fl.add(new JLabel());
		fl.add(new JLabel());
		fl.add(vRamBox);
		b.add(fl);
		b.add(Box.createVerticalStrut(padding));
		b.add(gi2d = new GradientIcon2D(p));
		b.add(Box.createVerticalStrut(padding));
		
		addy("Color passes made:  ", 	gradientR);
		b.add(Box.createVerticalStrut(padding));
		b.add(bouncesColor);
		b.add(Box.createVerticalStrut(padding));
		
		ape = new AdvancedPolygonEditor(p.getPolygon());
		b.add(ape);

		refresh(s);
		this.add(b);
		
	}
	JPanel tst = new JPanel(new GridLayout(12,2));
	private void add(String s, JTextField box){
		tst.add(new JLabel(s));
		tst.add(box);
	}
	private void addy(String s, JTextField box){
		JPanel tst = new JPanel(new GridLayout(1, 2));
		tst.add(new JLabel(s));
		tst.add(box);
		self.add(tst);
	}
	private void AAAction(){
		p.setAntiAliasing(antiAliasingBox.isSelected());
	}
	/*
	private void addx(String s, JTextField box){
		JLabel lb = new JLabel(s);
		lb.setLabelFor(box);
		GridBagLayout gb = new GridBagLayout();
		JPanel horiz = new JPanel(gb);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = gbc.weighty = 0;
		horiz.add(lb, gbc);
        gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = gbc.weighty = 1.0;
		horiz.add(box, gbc);
		self.add(horiz);
		//self.add(lb);
		//self.add(box);
		self.add(Box.createVerticalStrut(padding));
	}*/
	private void cacheAction(){
		p.caches(cacheBox.isSelected());
		//vRamBox.setEnabled(p.caches());
	}
	private void bouncesAction(){
		p.setBouncing(bouncesColor.isSelected());
	}
	private void movesLeftAction(){
		p.setDirectionLeft(movesLeft.isSelected());
	}
	private void rotationAction(){
		p.setRotationAmt(2 * PI * zeroAction(rotationBox, p.getRotationAmt()/ (2 * PI)));
	}
	private void offsetAction(){
		p.setOffset(PI * zeroAction(offsetBox, p.getOffset()/ PI));
	}
	private void timingMultAction(){
		p.setTimingMultiplier(nonZeroAction(timingBox, p.getBaseMultiplier()));
	}
	private void lagReductionAction(){
		int i = (int)zeroAction(lagReductionBox, p.getLagReduction());
		p.setLagReduction(i);
	}
	private void gradientRAction(){
		p.setGradientRepeats((int)nonZeroAction(gradientR, p.getGradientRepeats()));
	}
	private void delayAction(){
		p.setReactionDelay(zeroAction(delayBox, p.getBaseReactionDelay()));
	}
	private void stageAction(){
		p.setStages((int)zeroAction(stageBox, p.getStages()));
	}
	private void distanceActionX(){
		p.setDistanceMultiplierX(zeroAction(distanceBoxX, p.getDistanceMultiplierX()));
	}
	private void distanceActionY(){
		p.setDistanceMultiplierY(zeroAction(distanceBoxY, p.getDistanceMultiplierY()));
	}
	private void vRAMAction(){
		//Preset.vRAMCache = (vRamBox.isSelected());
	}
	boolean hasRowAction = false, rowIsUp = false;
	private void rowAction(){
		if(rowIsUp)
			return;
		if(!hasRowAction)
			rowIsUp = true;
		if (!hasRowAction) {
			int i = JOptionPane.showConfirmDialog(null,
					"Warning! Adjusting this may cause lag.\nDo you wish to continue?", "Warning", JOptionPane.YES_NO_OPTION,
					JOptionPane.ERROR_MESSAGE);
			if(i == JOptionPane.YES_OPTION){
				hasRowAction = true;
			}
		}
		if(hasRowAction)
			p.setRows((int)nonZeroAction(rowsBox, p.getRows()));
		else
			rowsBox.setText("" + p.getRows());
		rowIsUp = false;
	}
	private void pauseAction(){
		p.setPause(zeroAction(pauseBox, p.getBasePause()));
	}
	private void durationAction(){
		p.setDuration((int)nonZeroAction(durationBox, p.getBaseDuration()));
		hgw.tick(0);
		//if(hgw.tick > p.getDuration())
		//	hgw.tick = 0;
	}
	private void sizeAction(){
		p.setSizeFactor(nonZeroAction(sizeBox, p.getSizeFactor()));
		hgw.initCalcs();
	}
	private double nonZeroAction(JTextField source, double def){
		double sf = -1;
		try{
			sf = Double.parseDouble(source.getText());
		}catch(Exception e){
			source.setText(def + "");
		}
		if(sf <= 0){
			source.setText(def + "");
			return def;
		}else{
			return sf;
		}
	}
	private double zeroAction(JTextField source, double def){
		double sf = -1;
		try{
			sf = Double.parseDouble(source.getText());
		}catch(Exception e){
			source.setText(def + "");
		}
		if(sf < 0){
			source.setText(def + "");
			return def;
		}else{
			return sf;
		}
	}
}