package Reversi;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * 個々のマス目（Square）の状態を表現するクラス
 * マス目の大きさと、位置と、状態（playerかcomputeかnobodyか）を表す。
 */
public class Square {
	/*
	 *  2014-1-9 kzy,nishi@me.com
	 * クラスの設計（クラスの機能を決める）
	 * クラスの機能は一つに絞り込む、いろいろな機能を盛り込まないこと。
	 * クラスの機能（動作内容）　＝＞　メンバメソッド（関数）
	 * クラスのデーター　＝＞　メンバ変数（フィールド）
	 *
	 * 宣言はメソッドも変数も、普通は private にしておく。
	 * public で宣言しておくと他のクラスから参照できるので一見便利に思えるが、
	 * できるだけ public にはしない方が良い。
	 * public を多用するとコードの使い回しができなくなり、コードの見通しも悪くなる。
	 *
	 * private で宣言しておくと、外から見えない（隠ぺいされている）ので、  間違って
	 * データを書き換える事が無く安全になり、バグ発生を防止できる。
	 * 可能な限り変数のスコープは小さく設定した方がコードの使い回しができて良い。
	 * なるべくメンバにはしないで、その場所だけで使う変数はその場所で宣言する。
	 *
	 * 	値の変更が可能な（finalでない）メンバ変数にアクセスする場合はゲッターと
	 * セッターを用意して値の読み出し、書き込みを行い、宣言はprivateにしておく。
	 * （2016-6-28追記）
	 */

	// メンバ変数
	/*
	 * TODO　メニューから駒の画像が選択できるようにする。
	 */
	protected BufferedImage img;
	protected static ImageIcon icnPlayer, icnComputer;
	//
	private int px, py,typ, wf;		// Squareを設定する座標（マス目の左上座標）と属性
	private boolean fconv = false;		// 駒が挟まれたかチェックフラグ
	private int imgw, imgh;
	private String fPathP;			//
	private String fPathC;			//
	private BufferedImage imgPlayer, imgComputer;		// 画像取り込み用バッファ
	private Color cl = Color.BLACK;									// マス目枠線の色
	private BasicStroke st;												// マス目の線幅
	/*
	 * 駒画像の設定
	 */
//	private String player = "MyShip2-1.png";				// Player
//	private String computer = "Enemy2-1.png";			// computer
	private String player = "droid.png";							// Player
	private String computer = "Apple_Logo-Classic.png";			// computer
	private String marker = "ThWhiteBulletM.png";		// 駒を置くことができる事を示すマーカ
	private BufferedImage imgMarker;

	/*
	 *  コンストラクタ　＝＞　クラスからインスタンスを作った時に最初に実行される。
	 *  クラスと同じ名前のメソッド、型は無し
	 *  インスタンスを生成する際の初期データを引数で受け取る。（初期化、初期設定）
	 */
	/**
	 * 盤面上のマス目に相当するSquareを定義する
	 * @param cellTyp
	 */
	public  Square(int px, int py, int typ){			// コンストラクタ
		this.typ = typ;
		this.px = px;
		this.py = py;
		//
		try {
			 fPathP = new File(player).getCanonicalPath();
			imgPlayer = ImageIO.read(new File(fPathP));
			icnPlayer = new ImageIcon(fPathP);
			imgw = imgPlayer.getWidth();								// 取り込んだ画像のサイズ
			imgh = imgPlayer.getHeight();
			//
			fPathC = new File(computer).getCanonicalPath();
			imgComputer = ImageIO.read(new File(fPathC));
			icnComputer = new ImageIcon(fPathC);
			//
			String fPathM = new File(marker).getCanonicalPath();
			imgMarker = ImageIO.read(new File(fPathM));
		} catch (IOException e) {
			e.printStackTrace();
			imgPlayer = null;
			imgComputer = null;
		}
	}

	/**
	 *  メソッド（関数）＝＞　このクラスの機能を定める。
	 *  Squareの属性をチェックして対応する画像を表示する。
	 */
	public void drawSquare(){
		if (fconv == true){
			img = imgMarker;
		} else {
			//
			switch (typ){
			case MainPanel.PLAYER:
				img = imgPlayer;
			break;
			case MainPanel.COMPUTER:
				img = imgComputer;
			break;
			default:		//	NOBODY
				img = null;
			}
		}
	}

	/**
	 * プログラム制御用メンバのみGetter,Setterを用意する。
	 * メンバ変数にアクセスする際にはこのメソッドを使う。
	 * 画像データはメンバ変数に直接アクセスする。（間違い発生のリスク少ない）
	 */
	public void setPx(int px){
		this.px = px;
	}
	public int getPx(){
		return px;
	}
	public void setPy(int py){
		this.py = py;
	}
	public int getPy(){
		return py;
	}
	public void setTyp(int typ){
		this.typ = typ;
	}
	public int getTyp(){
		return typ;
	}
	public void setFconv(boolean fconv){
		this.fconv = fconv;
	}
	public boolean getFconv(){
		return fconv;
	}
	public void setWf(int wf){
		this.wf = wf;
	}
	public int getWf(){
		return wf;
	}
	public void setSquare(Color cl, BasicStroke st){
		this.cl = cl;
		this.st = st;
	}
	public Color getSqColor(){
		return cl;
	}
	public BasicStroke getSqStroke(){
		return st;
	}


}
