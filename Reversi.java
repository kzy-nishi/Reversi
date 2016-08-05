package Reversi;

import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.Timer;
/**
 * 2016-6-14 kzy.nishi@me.com
 * 2016-7-6 Update
 */
public class Reversi {

	private MainPanel panel = new MainPanel();
	protected static JLabel lblIcon;												// MainPanelから参照
	protected static  JTextField textPlayer;
	protected static  JTextField textComputer;
	//
	private int width = (panel.COLUMN - 2) * panel.SIZE;				// パネル（Play Field）の大きさ
	private int height = (panel.ROW - 2) * panel.SIZE;					// パネル（Play Field）の大きさ
	private int mMove;															// 押されたキーの種類
	private JFrame frame;
	private final int LEFT = 1;
	private final int RIGHT = 2;
	private final int UP = 3;
	private final int DOWN = 4;
	private JButton btnButton, btnButton_1;
	private JLabel lblMessage;
	//Plyer1 vs Plyer2 モードのときtrue、falseのときはPlyer vs Computerモード
	private static boolean pvspf = false;
	private boolean result = true;
	private boolean rsltAI = true;
	private boolean rsltCpu = true;
	private final int delay = 1000; 				//milliseconds
	/*
	 * ComputerクラスのインスタンスであるaiKinako.simulater()を実行して、getFx,getFyで
	 * 最適な打ち手を出力する。
	 * レベル０のaiKinaoは乱数で打ち手を決める。
	 */
	private final int LEVEL = 0;
	private Computer aiKinako = new Computer(LEVEL);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		/*
		 * 2014-1-9 kzy,nishi@me.com
		 * ===================================
		 * 唯一の mainメソッド、プログラムはここからスタートする。
		 * ====================================
		 * このクラスではウインドウやテキストフィールドの配置・外観を設定し、
		 * キー入力・マウス入力など、ユーザーインターフェイスもここで処理する。
		 * 実際の動作プログラムは機能毎にクラスに分けてる。
		 * 一つのクラスには一つの機能を持たせる。なんでもありのクラスは良くない。
		 */
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Reversi window = new Reversi();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * コンストラクタ　＝＞　オブジェクトがインスタンス化されたときに最初に実行される。
	 * （初期化・初期設定）
	 */
	public Reversi() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(300, 100, 624, 500);						// ウインドウの位置と大きさ
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//
		frame.getContentPane().setLayout(null);
		panel.setBounds(10, 10, width, height);									// Play Fieldの大きさ
		frame.getContentPane().add(panel);

		/*
		 * アクションイベントtaskComputerを設定してタイマーで起動する。
		 */
		 ActionListener taskComputer = new ActionListener() {
			 public void actionPerformed(ActionEvent evt) {
				/*
				 * PLAYERの処理が終わったら、自動的にCOMPUTERの駒を置くための準備
				 */
				copyArrey();
				/*
				 * ComputerクラスのインスタンスであるaiKinako.simulater()を実行して、getFx,getFyで
				 * 最適な打ち手を出力する。
				 */
				rsltAI = aiKinako.simulater();
				//
				System.out.print("aiKinakoの駒は、 ");
				System.out.println(aiKinako.getFx() + " " + aiKinako.getFy());
				//
				rsltCpu = panel.squareUpDate(MainPanel.COMPUTER,
						 aiKinako.getFx() * panel.SIZE - panel.SIZE / 2, aiKinako.getFy() * panel.SIZE - panel.SIZE / 2);
			}
		};

		/*
		 * マウスリスナーをセットしてマウスイベントを検知する。
		 */
//		frame.addMouseListener(new MouseAdapter() {
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Point point = e.getPoint();
				/*
				 *  'Player VS Computer' のとき
				 */
				if (!pvspf) {
					if (!MainPanel.getTurn()) {
						result = panel.squareUpDate(MainPanel.PLAYER, point.x, point.y);	// セル情報の更新
						lblMessage.setText("COMPUTER's turn.");
						/*
						 * PLAYERに続いてCOMPUTERが駒を置くが、そのまま実行すると、同時に画面描画が
						 * 行われるので、別イベントにしてタイマーで起動する。（一回だけ）
						 */
						Timer timer = new Timer(delay, taskComputer);
						timer.start();
						timer.setRepeats(false);		// タイマーイベントは一回で終わり
					} else {
						JOptionPane.showMessageDialog(frame, "computer is thinking.");
					}
				/*
				 *  'Player1 VS Player2' のとき
				 */
				} else if (pvspf) {
					if (!MainPanel.getTurn()) {
						result = panel.squareUpDate(MainPanel.PLAYER, point.x, point.y);	// セル情報の更新
						lblMessage.setText("It's another players's turn.");
					} else {
						result = panel.squareUpDate(MainPanel.COMPUTER, point.x, point.y);	// セル情報の更新
						lblMessage.setText("It's your turn.");
					}
				// どちらも選択されていないとき
				} else {
					JOptionPane.showMessageDialog(frame,
							"Please select either 'Player VS Computer' or 'Player1 VS Player2'. ");
				}
				if (!result) JOptionPane.showMessageDialog(frame, "Piece is not put in the square.");
				if (!rsltCpu) JOptionPane.showMessageDialog(frame, "aiKinako が置けないマス目に駒を置いた。");
				frame.repaint();		// ウインドウを再描画する。（MainPanelのPaintを実行）
			}
		});

		/*
		 * キーリスナーをセットしてキー入力イベントを検知する。
		 */
//		frame.addKeyListener(new KeyAdapter() {
		panel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				System.out.println("キーが押された。");
				/*
				 *  J,L,I,Mキーで操作
				 */
				switch (e.getKeyCode() ) {
				case(KeyEvent.VK_J):			// 定数は整数のみ、文字列は不可
					 mMove = LEFT;					// 左
					break;
				case(KeyEvent.VK_L):
					 mMove = RIGHT;				// 右
					break;
				case(KeyEvent.VK_I):
					 mMove =UP;						// 上
					break;
				case(KeyEvent.VK_M):
					 mMove =DOWN;				// 下
					break;
				default:
					//
				}
				panel.squareUpDate(MainPanel.PLAYER, 0, 0);		// セル情報の更新
				frame.repaint();		// ウインドウを再描画する。（MainPanelのPaintを実行）
			}
			//
			@Override
			public void keyReleased(KeyEvent e) {
				System.out.println("キーがリリースされた。");
				mMove = 0;
			}
		});
		/*
		 *
		 */
		btnButton = new JButton("Player VS Computer");
		btnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblMessage.setText("mode is Player VS Computer");
				pvspf = false;
				/*
				 * 盤面をリセットして、初期に戻す。
				 * 途中データがあれば、現在のデータを保存するかどうか、確認用のメッセージを出力する。
				 */
				panel.gameSave();			// ゲームを保存
//				panel = null;						// インスタンスを破棄
//				panel = new MainPanel();		//

			}
		});
		btnButton.setBounds(12, 430, 145, 21);					// ボタンの位置と大きさ
		frame.getContentPane().add(btnButton);
		btnButton.setFocusable(false);											// ボタンからフォーカスを外す。
		//

		btnButton_1 = new JButton("Player1 VS Player2");
		btnButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblMessage.setText("mode is Player1 VS Player2");
				pvspf = true;
				/*
				 * 盤面をリセットして、初期に戻す。
				 * 途中データがあれば、現在のデータを保存するかどうか、確認用のメッセージを出力する。
				 */
				panel.gameSave();			// ゲームを保存
//				panel = null;						// インスタンスを破棄
//				panel = new MainPanel();		//
			}
		});
		btnButton_1.setBounds(164, 430, 145, 21);
		frame.getContentPane().add(btnButton_1);
		btnButton_1.setFocusable(false);

		lblMessage = new JLabel("message");
		lblMessage.setBounds(321, 430, 275, 21);
		frame.getContentPane().add(lblMessage);
		lblMessage.setFocusable(false);

		textPlayer = new JTextField();
		textPlayer.setEditable(false);
		textPlayer.setBounds(500, 10, 96, 37);
		frame.getContentPane().add(textPlayer);
		textPlayer.setColumns(10);

		textComputer = new JTextField();
		textComputer.setEditable(false);
		textComputer.setColumns(10);
		textComputer.setBounds(500, 57, 96, 37);
		frame.getContentPane().add(textComputer);

		JLabel lblScore = new JLabel("PLAYER");
		lblScore.setBounds(422, 10, 66, 37);
		frame.getContentPane().add(lblScore);

		JLabel lblScore_E = new JLabel("CONPUTER");
		lblScore_E.setBounds(422, 57, 66, 37);
		frame.getContentPane().add(lblScore_E);

		JLabel lblTurn = new JLabel("TURN");
		lblTurn.setBounds(422, 356, 50, 54);
		frame.getContentPane().add(lblTurn);

		lblIcon = new JLabel("");
		lblIcon.setBounds(500, 356, 96, 70);
		frame.getContentPane().add(lblIcon);

		/*
		 * キーリスナーを設定しているが、ボタンにフォーカスを取られて
		 * キー入力イベントを検知できないので、
		 * requestFocusInWindow();		//キー入力を有効にするためフレームにフォーカスして
		 * オブジェクト.setFocusable(false);フォーカスを外すための操作が必要
		 */
		frame.requestFocusInWindow();
	}
	/**
	 * プログラム制御用メンバのみGetter,Setterを用意する。
	 * メンバ変数にアクセスする際にはこのメソッドを使う。
	 * 画像データはメンバ変数に直接アクセスする。（間違い発生のリスク少ない）
	 */
	public static boolean getPvsP(){
		return pvspf;
	}
	/**
	 * 配列のコピーを作成して、UNDOとシミュレーション（打ち手探索）に使う。
	 */
	private void copyArrey(){
		for(int i = 0; i < MainPanel.ROW; i++ ){
			for(int j = 0; j < MainPanel.COLUMN; j++){
				/*
				 *  MainPanel内の配列Square[][] sqを
				 *  Computern内の配列Square[][] simSqにコピーする。
				 */
				int typ = panel.getTyp(i, j);
				boolean fconv = panel.getFconv(i, j);
				aiKinako.setTyp(i, j, typ);
				aiKinako.setFconv(i, j, fconv);
			}
		}
//		printSquare();		// デバッグ用
	}
	/**
	 *  デバッグ用、各Squareの属性を表示
	 *  i, j =＞ j i に入れ替え（表示と向きを合わせるため
	 */
	private void printSquare(){
		System.out.println("simSq配列を表示");
		for(int i = 0; i < MainPanel.ROW; i++ ){
			for(int j = 0; j < MainPanel.COLUMN; j++){
				System.out.print(aiKinako.getTyp(j, i) + "  ");
			}
			System.out.println();		// 改行
		}
		System.out.println();		// 改行
	}

}
