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

strtus-config.xml での設定から Spiring アノテーションでの
設定に移行するため、
3.使用 @ControllerAdvice，不用任何的配置，只要把这个类放在项目中，Spring能扫描到的地方。就可以实现全局异常的回调。
@ControllerAdvice是一个@Component，用于定义@ExceptionHandler，@InitBinder和@ModelAttribute方法，适用于所有使用@RequestMapping方法。
@ExceptionHandler(value = {Exception.class})	
	public String execute( Exception e,/* ExceptionConfig conf, ActionMapping map, ActionForm form, */HttpServletRequest req, HttpServletResponse res) throws ServletException {
		String result = "/pages/common/error.jsp";
		
		log().error("CisaExceptionHandler error");

		// セッション生成を試みて(isNew)trueの場合、もしくはログイン時にセットした値がnullであればセッションタイムアウト
		boolean ret = false;
		try {
			HttpSession session = req.getSession();
// mod tys-yazawa Phase8静的解析対応
			if (session != null) {
//CIMSA-Phase11step1-二重タブ&更新無効化対応 2013/10/09 MODIFY liu START ---------------------------------->>
				// CISAApplicationException場合
				if(CISAApplicationException.class.equals(e.getClass())){
					CISAApplicationException cisaEx = (CISAApplicationException)e;


Spring4之前，@ControllerAdvice在同一调度的Servlet中协助所有控制器。Spring4已经改变：@ControllerAdvice支持配置控制器的子集，而默认的行为仍然可以利用。

在Spring4中， @ControllerAdvice通过annotations(), basePackageClasses(), basePackages() 方法定制用于选择控制器子集。

不过据经验之谈，只有配合@ExceptionHandler最有用，其它两个不常用。
