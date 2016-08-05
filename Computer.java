package Reversi;

/**
 * 2016-6-14 kzy.nishi@me.com
 * 2016-7-6 Update
 */
public class Computer {
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
	private int fx = 0;
	private int fy = 0;
	private int level;
	private final int Kinako = 0;
	private final int Sakura = 1;
	private final int Koko = 2;
	private boolean fconv;
	//
	private Square[][] simSq = new Square[MainPanel.ROW][MainPanel.COLUMN];
	/*
	 * 選択可能なマス目（Square）番号を保存する。
	 * マス目場号は左上のマス目を０番として、
	 * for(int i = 0; i < MainPanel.ROW; i++ ){
			for(int j = 0; j < MainPanel.COLUMN; j++){　の順番で番号を割振る
	 */
	private int[] stackX = new int[MainPanel.ROW * MainPanel.COLUMN];
	private int[] stackY = new int[MainPanel.ROW * MainPanel.COLUMN];
	/*
	 * コンストラクタ
	 * levelの数値の大きさによって手の探索手法と探索深さを変えて、強さを調整する。
	 */
	public Computer(int level) {
		this.level = level;
		int k = 0;
		for(int i = 0; i < MainPanel.ROW; i++ ){
			for(int j = 0; j < MainPanel.COLUMN; j++){
				// 最初は全てtypが0でNOBODY属性
				simSq[i][j] = new Square(i, j, MainPanel.NOBODY);			// 配列の初期化
				stackX[k] = 0;				// 配列の初期化
				stackY[k] = 0;
			}
		}
	}
	/**
	 * Player側の処理が終了後にこのメソッドが呼ばれる。このメソッドでは、
	 * 駒を置くことのできるマス目を順番に評価して最適と評価したマス目も
	 * 位置（ｆｘ、ｆｙ）を返す。
	 * @return
	 */
	public boolean simulater(){
		switch(level){
		/*
		 * aiKinako(レベル0)はサイコロ（乱数）で決める。
		 */
		case Kinako:
			int k = 0;
			for(int i = 1; i < MainPanel. ROW - 1; i++ ){
				for(int j = 1; j < MainPanel.COLUMN - 1; j++){
					if (simSq[i][j].getFconv()){
						stackX[k] = simSq[i][j].getPx();
						stackY[k] = simSq[i][j].getPy();
						k ++;
					}
					/*
					 *  Math.random()は0以上1未満の乱数を生成
					 *  0からk-1までの乱数
					 */
					int rand = (int) Math.random() * (k - 1);
					fx = stackX[rand];
					fy = stackY[rand];
				}
			}
			break;
		case Sakura:
			//
			break;
		case Koko:
			//
			break;
		default:
			//
		}
		return true;
	}
	/**
	 * プログラム制御用メンバのみGetter,Setterを用意する。
	 * メンバ変数にアクセスする際にはこのメソッドを使う。
	 * 画像データはメンバ変数に直接アクセスする。（間違い発生のリスク少ない）
	 */
	public int getFx(){
		return fx;
	}
	public int getFy(){
		return fy;
	}
	/**
	 * 	各マス目の属性を返り値で返す。
	 */
	public void setTyp(int fx, int fy, int typ){
		simSq[fx][fy].setTyp(typ);
	}
	public int getTyp(int fx, int fy){
		return simSq[fx][fy].getTyp();
	}
	/**
	 * 返り値がtrueなら駒を置くことができる。
	 */
	public boolean getFconv(int fx, int fy){
		return simSq[fx][fy].getFconv();
	}
	public void setFconv(int fx, int fy, boolean fconv){
		simSq[fx][fy].setFconv(fconv);
	}
}
