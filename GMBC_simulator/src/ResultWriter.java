import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ResultWriter {
	/**
	 * ライター
	 */
	PrintWriter pw;

	/**
	 * コンストラクタ
	 * Writerを生成
	 * @param path ファイルのパス
	 * @throws IOException
	 */
	public ResultWriter(String path) throws IOException {
		FileWriter file = new FileWriter(path);
		pw = new PrintWriter(new BufferedWriter(file));
	}

	/**
	 * ファイルに１行書き込む
	 * @param str 内容
	 */
	public void println(String str) {
		pw.println(str);
	}

	/**
	 * Writerを閉じる
	 */
	public void close() {
		pw.close();
	}
}
