# Struts-Spring-FW
既存のカード発行システムのサポート期間の関係から、FFISからはFFSからFFISへのリリースが6月末必達で要望頂いている
Struts1不支持了

①トークンチェック

トランザクショントークンチェックの処理を
Struts -> Terasoulna ライブラリへ移行
（※Springライブラリにはトランザクショントークンチェックはない）するため、移行内容に問題がないか（新旧で動作が同じになっているか）を確認する。
import org.terasoluna.gfw.web.token.transaction.TransactionTokenCheck;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenType;

@TransactionTokenCheck
	protected Boolean isTokenValid(final HttpServletRequest request) {
		logger.debug("********* TransactionTokenCheck CHECK TOKEN *********");
		final HandlerMethod method = new HandlerMethod(this, new Object(){}.getClass().getEnclosingMethod());
		return transactionTokenInterceptor.isTokenValid(request, method);
	}
	
②例外発生	 自定義異常処理（全局自定義異常処理）

③strtus-config.xml での設定から Spiring アノテーションでの
設定に移行するため、


					
					
Bug #10230
リクエストパラメータが1つ且つ、String配列のFormプロパティへ空文字パラメータをセットするパターンのときに Strtus, Spring 間での差異が発生

Strtus > String1 要素[""]
Spring > String0 要素無し

対策内容:
Springでのリクエストパラメータの配列セット処理をカスタマイズし、空文字をセットするよう修正
import org.springframework.core.convert.converter.Converter;
public class StringToArrayConverter implements Converter<String, String[]>{

	@Override
	public String[] convert(String source) {
		return new String[] {source};
	}
}


Bug #9537
フレームワーク間の一時保存アップロードファイルの削除処理に差異があるため発生。
Strtus:FormFile.destroyメソッドで明示的に削除処理を呼び出す必要がある
Spring:リクエスト毎に一時保存アップロードファイルは自動削除される
該当エラー箇所では、リクエストをまたいでアップロードファイルを保持する必要があるため、
Spring側では削除されないようフラグを立てる必要があるが、その実装漏れによるもの

対策内容:
一時保存アップロードファイル削除処理を実行しないよう修正
	


Bug #9810
struts-config.xml内に定義されている下記設定が、Spring移行後WebMvcConfig.javaにハードコーディングされている。
そのため、運用時に設定変更を実施することができない。

==============================
<!-- ファイルアップロード用の定義 -->
<controller maxFileSize="-1" 
bufferSize="4096" 
tempDir="C:/cisa/work/upload" />

=============================
bufferSizeはStrtus固有の設定値でアップロード処理のバッファサイズ指定です。
アップロードファイルが大きい場合に本設定値を大きくするとパフォーマンス改善につながるようなものです。
cisa_manageでは4096となっていますが、これはStrtusのデフォルト値です。
Springにはこのような設定値はなく、FWに委ねるようになっています。

対策内容:
アップロードの設定値をWEB-INF/applicationContext.xmlから変更できるよう修正


Bug #9487
StrutsのDynaActionFormの挙動を再現している、
jp.co.fujifilm.cisa.manage.form.DynaActionForm.getMap()メソッドの動作の差異によるものです。
Formの値をMapオブジェクトとして返す際、プロパティ値取得できていないため発生しています。

対策内容:
コードfield.setAccessible(true);を追加し、Formのプロパティ値取得できるよう修正。
field.setAccessible(true);
final Object value = field.get(this);
if(field.getType() == String.class && value == null) {
	dynaValues.put(field.getName(), "");
}else {
	dynaValues.put(field.getName(), value);
}

Spring4之前，@ControllerAdvice在同一调度的Servlet中协助所有控制器。Spring4已经改变：@ControllerAdvice支持配置控制器的子集，而默认的行为仍然可以利用。

在Spring4中， @ControllerAdvice通过annotations(), basePackageClasses(), basePackages() 方法定制用于选择控制器子集。

不过据经验之谈，只有配合@ExceptionHandler最有用，其它两个不常用。
