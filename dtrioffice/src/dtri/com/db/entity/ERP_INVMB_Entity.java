package dtri.com.db.entity;

import java.util.List;

//料號 規格查詢
public class ERP_INVMB_Entity {

	private static List<ERP_INVMB_Entity> erp_INVMB_Entities;// 料件資料
	private String mb001;// 品號/料號
	private String mb002;// 品名
	private String mb003;// 規格
	private String mb009;// 內容描述
	private String mb017;// 庫別(主要)
	private String mc002;// 庫別(名稱)
	private String mc005;// 庫別(有效)

	public String getMb001() {
		return mb001;
	}

	public void setMb001(String mb001) {
		this.mb001 = mb001;
	}

	public String getMb002() {
		return mb002;
	}

	public void setMb002(String mb002) {
		this.mb002 = mb002;
	}

	public String getMb003() {
		return mb003;
	}

	public void setMb003(String mb003) {
		this.mb003 = mb003;
	}

	public String getMb009() {
		return mb009;
	}

	public void setMb009(String mb009) {
		this.mb009 = mb009;
	}

	public String getMb017() {
		return mb017;
	}

	public void setMb017(String mb017) {
		this.mb017 = mb017;
	}

	public String getMc002() {
		return mc002;
	}

	public void setMc002(String mc002) {
		this.mc002 = mc002;
	}

	public String getMc005() {
		return mc005;
	}

	public void setMc005(String mc005) {
		this.mc005 = mc005;
	}

	// 暫時寫死(提供暫存資料 )
	public static List<ERP_INVMB_Entity> getErp_INVMB_Entities() {
		return erp_INVMB_Entities;
	}

	public static void setErp_INVMB_Entities(List<ERP_INVMB_Entity> erp_INVMB_Entities) {
		ERP_INVMB_Entity.erp_INVMB_Entities = erp_INVMB_Entities;
	}

}
