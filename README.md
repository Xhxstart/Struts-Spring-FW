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
