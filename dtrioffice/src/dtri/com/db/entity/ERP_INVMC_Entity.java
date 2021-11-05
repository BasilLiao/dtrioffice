package dtri.com.db.entity;

//庫別規格-查詢
public class ERP_INVMC_Entity {
	
	// --INVMC.MC001 品號
	// --INVMC.MC002 庫別
	// --cms.MC002 as CMC002 庫別名稱
	// --INVMC.MC003 儲位
	// --INVMC.MC007 庫存數量
	// --INVMC.MC004 安全庫存
	// --INVMC.MC012 最近入庫日
	// --INVMC.MC013 最近出庫日

	private String mc001;
	private String mc002;
	private String cmc002;
	private String mc003;
	private String mc007;
	private String mc004;
	private String mc012;
	private String mc013;
	public String getMc001() {
		return mc001;
	}
	public void setMc001(String mc001) {
		this.mc001 = mc001;
	}
	public String getMc002() {
		return mc002;
	}
	public void setMc002(String mc002) {
		this.mc002 = mc002;
	}
	public String getCmc002() {
		return cmc002;
	}
	public void setCmc002(String cmc002) {
		this.cmc002 = cmc002;
	}
	public String getMc003() {
		return mc003;
	}
	public void setMc003(String mc003) {
		this.mc003 = mc003;
	}
	public String getMc007() {
		return mc007;
	}
	public void setMc007(String mc007) {
		this.mc007 = mc007;
	}
	public String getMc004() {
		return mc004;
	}
	public void setMc004(String mc004) {
		this.mc004 = mc004;
	}
	public String getMc012() {
		return mc012;
	}
	public void setMc012(String mc012) {
		this.mc012 = mc012;
	}
	public String getMc013() {
		return mc013;
	}
	public void setMc013(String mc013) {
		this.mc013 = mc013;
	}
}
