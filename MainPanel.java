package Reversi;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * 2016-6-14 kzy.nishi@me.com
 * 2016-7-6 Update
 */
public class MainPanel extends JPanel {
	/*
	 * 2014-1-9 kzy,nishi@me.com
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
	/**
	 * 2016-6-14 kzy.nishi@me.com
	 * 2016-7-4 Update
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Create the panel.
	 */
	protected static final int ROW = 10;						// ０-７で８行、８列 + 外周WALLで10×10
	protected static final int COLUMN = 10;
	protected static final int NOBODY = 0;			// まだ何も属性が無い状態
	protected static final int PLAYER = 1;				// プレイヤーのSquare
	protected static final int COMPUTER = 2;			// コンピュータのSquare
	protected static final int WALL = 3;					// 盤の外周の見えないSquare
	protected static final int MARKER = 4;
	protected static final int SIZE = 50;
	private static boolean turn = true;				// プレイの順番　false:PLAYER  true:COMPUTER(PLAYER2)

	/*
	 * TODO  三要素の配列にして、Undoを使えるようにする。
	 */
	private Square[][] sq = new Square[ROW][COLUMN];		// 二次元配列を準備　　リバーシ（8×8）
	private BufferedImage bgimg;				// 背景画像取り込み用バッファ
	private int bgWidth, bgHeight;				// 背景画像の大きさ
	/*
	 * 背景画像の設定
	 */
//	private String bFilename = "map1.jpg";
//	private String bFilename = "universe1.png";
	private String bFilename = "AndVSApp.jpg";
	//
	private int ENEMY;				// 初期値　COMPUTER(PLAYER2)
	private int fx = -1;				// 初期値をゼロに設定すると、最初に　sq[0][0] に駒を置いてしまうため-1に設定
	private int fy = -1;
	private int imgw, imgh;
	private final int getPiece= 0;			// chkSquare 内でのmodeChk分岐パラメータ
	private final int fchkConv = 1;		// chkSquare 内でのmodeChk分岐パラメータ
	private ImageIcon turnImag = Square.icnPlayer;
	private boolean fchkNoPlace = false;
	private boolean fchkGameOver = false;
	private boolean fchkPlayerWinn = false;
	private int p, q;				// 取得した駒の集計用カウンタ
	private Color cl = Color.BLACK;
	private Color clr = Color.RED;
	private BasicStroke st = new BasicStroke(1.0f);		// 描画の際の線幅の設定;
	private BasicStroke str  = new BasicStroke(5.0f);


	/**
	 *　コンストラクタ
	 * @throws IOException
	 */
	public MainPanel(){
		/*
		 * 2014-1-9 kzy,nishi@me.com
		 * MainPanelクラスがこのプログラムの本体として全体の動作をコントロールする。
		 * 画面表示もここの peint()メソッドで処理する。
		 * 他のクラスから勝手に表示させない事、なぜならpeint()メソッドはイベント毎に
		 * 自動で再描画されるので便利だから。
		 */
		File path = new File(bFilename);							// 背景表示用
		try {
			String fPath = path.getCanonicalPath();
			bgimg = ImageIO.read(new File(fPath));
			imgw = bgimg.getWidth();								// 取り込んだ画像のサイズ
			imgh = bgimg.getHeight();
		} catch (IOException e) {
			e.printStackTrace();
			bgimg = null;
		}
		/*
		 *  Squareクラスのインスタンスを生成する。
		 *  Square（8×８）の外側のSquareをWALL属性にする。
		 *  盤面操作は全て10×10のSquareで考える。
		 */
		for(int i = 0; i < ROW; i++ ){
			for(int j = 0; j < COLUMN; j++){
				sq[i][j] = new Square(i, j, NOBODY);								// 最初は全てtypが0でNOBODY属性
				if(i == 0 || i == ROW - 1) {sq[i][j].setTyp(WALL);}			// 盤を取り囲む外側のエリアはWALL属性にする。
				if(j == 0 || j == COLUMN - 1) {sq[i][j].setTyp(WALL);}
				sq[i][j].setSquare(cl, st);													// マス目の枠線の設定
			}
		}
		changeTurn();
	}


	@Override
	public void paint(Graphics g){
		/*
		 * @see javax.swing.JComponent#paint(java.awt.Graphics)
		 * 表示はここpaint(Graphics g)にまとめる。
		 * repaint()を実行するとpaint(Graphics g)が実行される。
		 */
//		g.drawImage(bgimg, 0, 0, null);		// 背景（固定）
		/*
		 * g.drawImage(bgimg, 表示画像左上の角X位置、Y位置、表示画像右下の角X位置、Y位置、
		 * 						元画像切出し左上の角X位置、Y位置、元画像切出し右下の角X位置、Y位置、
		 * 						this)
		 */
		int x = 0, y = 0;
		g.drawImage(bgimg,  0, 0, SIZE * (ROW - 2), SIZE * (COLUMN - 2), x, y,  x + imgw, y + imgh, null);
		/*
		 *  盤面の再描画
		 *  および、Square情報の更新（挟まれたSquareを反転させる。）
		 *  盤面をクリックするかEnterキーを押したときに配列ar[][]の属性を書き換える。
		 *  プレイヤーがクリックした場合は、属性をplayerに、コンピュータが選択した場合は属性をcomputerに
		 */
		Graphics2D g2 = (Graphics2D)g;
		/*
		 * 盤面操作は10×10のSquareで考えるが、表示は外周のSquareを除いた8×８で行う。
		 * 盤面上一番左上のマス目は配列1,1なので、i, j の初期値は1,1から始める。
		 */
		for(int i = 1; i < ROW - 1; i++ ){
			for(int j = 1; j < COLUMN - 1; j++){
				Color c = sq[i][j].getSqColor();										// マス目枠線の色を設定
				g2.setColor(c);
				BasicStroke s = sq[i][j].getSqStroke();									// マス目枠線の太さを設定
				g2.setStroke(s);
				/*
				 * 表示に合わせて配列引数を1,1から始めるとマス目位置がずれるので、-1して調整する。
				 */
				g2.drawRect(SIZE * (i - 1), SIZE * (j - 1), SIZE, SIZE);		// 枠線の描画
				sq[i][j].drawSquare();											// 盤面の駒を描画
				/*
				 *  それぞれのSquareに配置する画像（img）と位置を示す。
				 *  表示に合わせて配列引数を1,1から始めると駒位置がずれるので、-1して調整する。
				 *  g.drawImage(img, 表示画像左上の角X位置、Y位置、X方向表示幅、Y方向表示幅、this)
				 */
				g.drawImage(sq[i][j].img, SIZE * (i- 1), SIZE * (j - 1), SIZE, SIZE, null);
			}
		}
		//
		printSquare();
		/*
		 *
		 */
		Reversi.lblIcon.setIcon(turnImag);
//		 得点の表示
		Reversi.textPlayer.setText(String.valueOf(p));
		Reversi.textComputer.setText(String.valueOf(q));
	}

	/**
	 * Square情報の更新 ＝＞ 盤面をクリックしたときに呼ばれる。
	 * クリックされた座標からSquareを特定して、駒が置ける場所か判定
	 * 置けなければアラートメッセージを表示、駒が置ける馬車なら属性を変更
	 * 対戦相手の駒が自分の駒に挟まれているかを判定、挟まれていれば属性を変更
	 * 対戦相手の駒を置ける場所にマーカーを設定する。
	 * repaint()でpaintが呼ばれて駒を描画
	 *
	 * computerとの対戦モードのときは、駒を置ける場所にマーカーを設定した後、
	 * 最適な場所を選択して駒を置く　＝＞　Computerクラス
	 */
	public boolean squareUpDate(int cellTyp, int posx, int posy){
		/*
		 * クリックされたエリアを特定する。
		 * 盤面操作エリア(10x10)と表示エリア(8x8)が異なる。
		 * ゲーム用盤面はその外側のWall属性のマス目を除いて8X8で表示しているため、
		 * 盤面の一番左上のマス目は配列としては（１，１）となるので+1している。
		 */
		fx =  (int)(posx / SIZE + 1);			//
		fy =  (int)(posy / SIZE + 1);			//
//		System.out.print(fx);
//		System.out.println(fy);
		/*
		 * 最初に駒が置ける場所かチェックする。
		 * 駒が置けなければ　false を返して戻る。
		 */
		if (!sq[fx][fy].getFconv()){
			return false;
		}
		/*
		 * クリックされたSquareの属性を変更する。
		 */
		defaultSquare();								// 一旦全てのマス目枠線の色と太さを戻す。
		sq[fx][fy].setSquare(clr, str);			// クリックされたマス目を強調する。
		sq[fx][fy].setTyp(cellTyp);
		sq[fx][fy].drawSquare();					// 盤面の駒画像を選択
		//
		chkSquare(fx, fy, getPiece);			// 獲得できる駒をチェック（駒の挟み込み）
		changeTurn();
		//
		erasMarker();									// マーカーを消して、得点集計
		repaint();
		//
		chkConv();										// 駒を置けるマス目をチェック
		return true;
	}
	/**
	 * ターンを変える。
	 */
	private void changeTurn(){
		/*
		 *  プレイの順番　false:PLAYER  true:COMPUTER(ENEMY)
		 *  初期値はturn = true
		 *  コンストラクタの中でchangeTurnを1回実行しているので、結局Playerが先手
		 */
		if (turn == false){
			turn = true;					// turnはPLAYERからCOMPUTERに
			ENEMY = PLAYER;
			turnImag = Square.icnComputer;		// 画面右下の画像を切り替える。
		} else {
			turn = false;					// turnはCOMPUTERからPLAYERに
			ENEMY = COMPUTER;
			turnImag = Square.icnPlayer;		// 画面右下の画像を切り替える。
		}
	}

	/**
	 * ＝＝＝＝＝chkSquare、駒が挟まれているかを判定する手順＝＝＝＝＝＝＝＝＝＝＝＝＝
	 * クリックされたSquareを起点に左右・上下・斜め上下の順番で各方向にチェック
	 *　	1	チェックしたSquareがNOBODYかWALLか自分の駒なら（相手の駒でなければ）スキップして、
	 *				次の方向のSquareへ--①
	 *		2	チェックしたSquareに相手方の駒があれば、その先のSquareをチェックする。ｰｰ②
	 *		3	①になるまで、②を繰り返し
	 *		4	①になったときに、Squareが空（NOBODY）で終了した場合、WALLの時で終了したときは、
	 *				全てのフラグをリセットする。
	 *		5	Squareが自分の駒で終了したときは、チェック済みフラグがセットされたSquareの属性を自分の
	 *				属性に変更する。
	 *＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	 *		7	repaint()を実行して再描画
	 */
	private void chkSquare(int fx, int fy, int modeChk){
		for (int m = -1; m < 2; m ++){
			for (int n = -1; n < 2; n ++){
				/*
				 * クリックしたSquareの周囲のSquareをチェックする。
				 * 最初は左上のSquareからチェック
				 * チェックしたSquareに相手方の駒があれば、その先のSquareをチェックする
				 * n=0,m=0はチェックから省く
				 */
				if (n != 0 || m !=0) {
					if (sq[fx + n][fy + m].getTyp() ==  ENEMY){		// 置いた駒の隣（周囲）の駒が相手の駒なら、
//						System.out.print(n);
//						System.out.println(m);
						/*
						 * IF文を通過したn,mの組み合わせから駒を検索する方向を決める。
						 * -1,-1 | 0,-1 | 1,-1
						 * ------+-----+-------
						 * -1, 0  | 0, 0 | 1, 0
						 * ------+-----+------
						 * -1, 1  | 0, 1 | 1, 1
						 */
						int i = 2;
						while (sq[fx + i * n][fy + i * m].getTyp() ==  ENEMY) {		// さらにその先の駒が相手の駒なら、
							/*
							 * チェックしたマス目が相手の属性であれば、ループを回してカウントアップする。
							 * 300行で1個先の駒チェックが終わってるので、int i = 2として2個先の駒チェックから始める。
							 */
							i ++;
						}
						System.out.print("check " + i + " ");			// チェックしたマス目の数
						System.out.print("X" + fx + i * n + " ");		// Ｘ方向、クリックしたマス目、移動したマス目
						System.out.println("Y" + fy + i * m);			// Ｙ方向、クリックしたマス目、移動したマス目
						/*
						 * 検索が終了したマス目の属性によって処理を変える。
						 * 4	①になったときに、Squareが空（NOBODY）で終了した場合、WALLの時で終了したときは、
						 * 			全てのフラグをリセットする。
						 * 5	Squareが自分の駒で終了したときは、チェック済みフラグがセットされたSquareの属性を自分の
						 *			属性に変更する。
						 */
						switch (modeChk){
						/*
						 * getPieceは自分の駒を置いたときに、挟むことができた相手の駒の属性を自分の駒の属性に変える。
						 * カウンター i は反転させる相手の駒の数＋１を示している。
						*/
						case getPiece:
							/*
							 *  マス目の属性PLAYER、COMPUTERによって処理を変える。
							 */
							switch (sq[fx + i * n][fy + i * m].getTyp()) {
							case PLAYER:		// チェック済みマス目を自分の駒にする。
//								System.out.println("Player " + i);
								while(i > 0) {
									i --;
									sq[fx + i * n][fy +i * m].setTyp(PLAYER);
								}
//								System.out.println("K= " + i);
//								System.out.println("Player " + i);
								break;
							case COMPUTER:	// チェック済みマス目を自分の駒にする。
								while(i > 0) {
									i --;
									sq[fx + i * n][fy + i * m].setTyp(COMPUTER);
								}
								break;
							default:
								break;
							}
							break;
						/*
						 * fchkConvは相手の駒が置かれたときに、自分の駒を置くことが可能なマス目をマークする。
						 * ENEMYの内容によって動作を変える。
						 */
						case fchkConv:
							/*
							 *  自分の駒の先に相手の駒があって、その先のマス目の属性がNOBODY（空）なら
							 *  駒を置けると判断し、マーカーをセットする。
							 */
							switch (sq[fx + i * n][fy + i * m].getTyp()) {
							case NOBODY:		//
									sq[fx + i * n][fy + i * m].setFconv(true);		// マーカーをセット
									fchkNoPlace = true;
								break;
							default:
								break;
							}
						}
						System.out.println("Exit loop");
					}
				}
			}
		}
	}
	/**
	 *全Squareについて駒が置ける場所をチェックしてマーカーをセット
	 */
	private void chkConv(){
		for(int i = 1; i < ROW - 1; i++ ){
			for(int j = 1; j < COLUMN - 1; j++){
				/*
				 * マス目の属性が自分ならchkSquareを実行
				 * NOBODYでなくて、かつ、敵でもない＝＞自分の駒
				 * 自分の駒から見て、相手の駒の先に空のマス目があればマーカーを置く。
				 */
				if (sq[i][j].getTyp() != NOBODY && sq[i][j].getTyp() != ENEMY) {
					chkSquare(i, j, fchkConv);
				}
			}
		}
		if (!fchkNoPlace){
			/*
			 * 自分の駒から見て、相手の駒の先に空のマス目が無ければ、
			 * 自分の駒を置く場所がないのでパスして、ターンを変える。盤面をクリックした場合は
			 * SquareUpDateでターンを変えるが、この場合はここでターんを変える。
			 * ただし、全てのマス目が埋まっていればゲーム終了
			 */
			if (fchkGameOver){
					JOptionPane.showMessageDialog(this, "Game Over!!");
				if (fchkPlayerWinn) {
					JOptionPane.showMessageDialog(this, "Winer is Player!!");
				} else {
					JOptionPane.showMessageDialog(this, "Winer is Computer!!");
				}
			}
			JOptionPane.showMessageDialog(this, "There is no place to put the piece!!");
			changeTurn();
		}
		fchkNoPlace = false;
	}
	/**
	 * 全Squareのマーカーを消す。
	 * 自分と相手の駒を数えて得点を表示する。
	 */
	private void erasMarker(){
		 p = 0;
		 q = 0;
		for(int i = 1; i < ROW - 1; i++ ){
			for(int j = 1; j < COLUMN - 1; j++){
				sq[i][j].setFconv(false);		// マーカーを消す。
//				sq[i][j].setSquare(cl, st);		// マス目枠線の色と太さを元に戻す。
				if (sq[i][j].getTyp() == PLAYER) p ++;
				if (sq[i][j].getTyp() == COMPUTER) q ++;
			}
		}
		//
		if (p + q == (ROW - 2) * (COLUMN- 2) ) {
			fchkGameOver = true;
			if (p > q){
				fchkPlayerWinn = true;
			}
		}
	}
	/*
	 * /マス目枠線の色と太さを元に戻す。
	 */
	private void defaultSquare(){
		for(int i = 1; i < ROW - 1; i++ ){
			for(int j = 1; j < COLUMN - 1; j++){
				sq[i][j].setSquare(cl, st);		//
			}
		}
	}
	/**
	 * 現在のデータを保存して、ゲームを初期化する。
	 * Player VS Computerの場合はComputerのレベルを選択させる。
	 */
	public void gameSave(){
		/*
		 * 現在の盤面データがあれば、データを保存するかメッセージを出して、
		 * 処理方法を分岐する。無ければスルー
		 *  リバーシの初期状態の駒位置を設定する。
		 */
		sq[ROW / 2 - 1][COLUMN / 2 -1].setTyp(PLAYER);			// sq[4][4]
		sq[ROW / 2][COLUMN / 2].setTyp(PLAYER);					// sq[5][5]
		sq[ROW / 2 - 1][COLUMN / 2].setTyp(COMPUTER);		// sq[4][5]
		sq[ROW / 2][COLUMN / 2 -1].setTyp(COMPUTER);			// sq[5][4]
		//
		erasMarker();
		chkConv();										// 駒を置けるマス目をチェック
		repaint();
	}
	/**
	 * プログラム制御用メンバのみGetter,Setterを用意する。
	 * メンバ変数にアクセスする際にはこのメソッドを使う。
	 * 画像データはメンバ変数に直接アクセスする。（間違い発生のリスク少ない）
	 */
	public static void setTurn(boolean turn){
		MainPanel.turn = turn;
	}
	public static boolean getTurn(){
		return turn;
	}
	/**
	 * 返り値がtrueなら駒を置くことができる。
	 */
	public boolean getFconv(int fx, int fy){
		return sq[fx][fy].getFconv();
	}
	/**
	 * 	各マス目の属性を返り値で返す。
	 */
	public int getTyp(int fx, int fy){
		return sq[fx][fy].getTyp();
	}

	/**
	 *  デバッグ用、各Squareの属性を表示
	 *  i, j =＞ j i に入れ替え（表示と向きを合わせるため
	 */
	private void printSquare(){
		System.out.println("sq配列を表示");
		for(int i = 0; i < ROW; i++ ){
			for(int j = 0; j < COLUMN; j++){
				System.out.print(sq[j][i].getTyp() + "  ");
			}
			System.out.println();		// 改行
		}
		System.out.println();		// 改行
		//
		/*
		String fno;
		for(int i = 0; i < ROW; i++ ){
			for(int j = 0; j < COLUMN; j++){
				if (sq[j][i].getFconv()){
					fno = "● ";
				} else {
					fno = "○ ";
				}
				System.out.print(fno);
			}
			System.out.println();		// 改行
		}
		System.out.println();		// 改行
		*/
	}

}
