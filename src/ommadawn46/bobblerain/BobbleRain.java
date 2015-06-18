package ommadawn46.bobblerain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;
import processing.event.KeyEvent;

public class BobbleRain extends PApplet {
	private static final long serialVersionUID = -920516069357135662L;
	private List<Bobble> bobbles;
	private List<PVector> mouseLocus;
	private int width, height;
	private PVector gravity, indicatorPos, basePos;
	private double gravityAcl, gravityDir;

	public static void main(String args[]){
		PApplet.main(new String[] { "--present", "ommadawn46.bobblerain.BobbleRain" });
	}

	// 初期設定
	public void setup(){
		bobbles = new ArrayList<Bobble>();
		mouseLocus = new ArrayList<PVector>(Arrays.asList(new PVector(mouseX, mouseY), new PVector(mouseX, mouseY), 
				new PVector(mouseX, mouseY) ,new PVector(mouseX, mouseY) ,new PVector(mouseX, mouseY)));
		
		width = displayWidth;
		height = displayHeight;
		mouseX = width/2;
		mouseY = height/2;
		
		gravity = new PVector();
		indicatorPos = new PVector();
		basePos = new PVector();
		
		gravityAcl = 0.0;
		gravityDir = Math.PI / 2;
		calcGravity();
		
		indicatorPos.set(80, 80);
		basePos.set(0, 0);
		
		size(width, height);
	}
	
	// 重力加速度の計算
	private void calcGravity(){
		gravity.set((float)(gravityAcl*Math.cos(gravityDir)), (float)(gravityAcl*Math.sin(gravityDir)));
	}
	
	// ゲッター
	public PVector getGravity(){
		return gravity;
	}
	public PVector getBasePosition(){
		return basePos;
	}

	// キー操作のリスナー
	public void keyPressed(KeyEvent e){
		int key = e.getKeyCode();
		if(key == 37){
			gravityDir += Math.PI*6 / 180;
		}else if(key == 39){
			gravityDir -= Math.PI*6 / 180;
		}else if(key == 38){
			gravityAcl += 0.003;
		}else if(key == 40){
			gravityAcl = gravityAcl > 0.003 ? gravityAcl - 0.003 : 0.0;
		}
		calcGravity();
	}
	
	// マウスクリックのリスナー
	public void mousePressed(){
		PVector pmouse = mouseLocus.get(0);
		PVector speed = new PVector(mouseX-pmouse.x, mouseY-pmouse.y);
		bobbles.add(new Bobble(this, mouseX-basePos.x, mouseY-basePos.y, 100f, speed));
	}

	// 描画ループ
	public void draw(){
		mouseLocus.remove(0);
		mouseLocus.add(new PVector(mouseX, mouseY));
		moveBasePosition();

		noStroke();
		background(255);
		
		drawGridPoint();
		drawIndicator();
		generateBobble(1);

		List<Bobble> outsideBobbles = new ArrayList<Bobble>();
		for(Bobble bobble: bobbles){
			if(bobble.isOnField()){
				bobble.update();
			}else{
				outsideBobbles.add(bobble);
			}
		}
		for(Bobble outsideBobble: outsideBobbles){
			bobbles.remove(outsideBobble);
		}
	}
	
	// 重力の方向を描画
	private void drawIndicator(){
		fill(220,220,230,255);
		ellipse(indicatorPos.x-basePos.x, indicatorPos.y-basePos.y, 60, 60);

		fill(100,50,70,255);
		ellipse(indicatorPos.x-basePos.x, indicatorPos.y-basePos.y, 12, 12);
		triangle((float)(gravityAcl*200*Math.cos(gravityDir)+indicatorPos.x-basePos.x), (float)(gravityAcl*200*Math.sin(gravityDir)+indicatorPos.y-basePos.y),
				(float)(6*Math.cos(gravityDir+Math.PI*1/2)+indicatorPos.x-basePos.x), (float)(6*Math.sin(gravityDir+Math.PI*1/2)+indicatorPos.y-basePos.y),
				(float)(6*Math.cos(gravityDir-Math.PI*1/2)+indicatorPos.x-basePos.x), (float)(6*Math.sin(gravityDir-Math.PI*1/2)+indicatorPos.y-basePos.y));
	}
	
	// グリッドポイントを背景に描画
	private void drawGridPoint(){
		int space = 200;
		fill(120, 200);
		for(int i = -3; i <= width/space + 3; i++){
			for(int j = -3; j <= height/space + 3; j++){
				ellipse((i-(int)(basePos.x/space))*space, (j-(int)(basePos.y/space))*space, 6, 6);
			}
		}
	}
	
	// 座標をウィンドウ上のマウスポウンタの位置に向けて動かす
	private void moveBasePosition(){
		basePos.set(basePos.x - (mouseX-width/2)/50, basePos.y - (mouseY-height/2)/50);
		translate(basePos.x, basePos.y);
	}

	// バブルをランダムに生成
	private void generateBobble(int num){
		if(num > 0){
			double maxSize = 80;
			double diagonal = Math.sqrt(width*width + height*height) + maxSize;
			double angle = Math.random()*Math.PI*2;

			float locX = (float)(width/2 - basePos.x - (diagonal/2)*Math.cos(angle));
			float locY = (float)(height/2 - basePos.y - (diagonal/2)*Math.sin(angle));

			bobbles.add(new Bobble(this, locX, locY, (float)(Math.random()*(maxSize-10)+10), 
					new PVector(gravity.x*100 + (float)Math.random()*6 - 3, gravity.y*100 +(float)Math.random()*6 - 3)));
			generateBobble(num-1);
		}
	}
}