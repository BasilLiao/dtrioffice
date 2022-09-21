package dtri.com.db.entity;

import java.util.Date;

/**
 * 生管排程資料
 * 
 **/
public class ProductionManagementEntity {
	// 系統基本資料
	private Date sys_create_date;
	private String sys_create_user;
	private Date sys_modify_date;
	private String sys_modify_user;
	private Integer useful;
	private String note;
	// 主要資料 ERP
	private Integer id;// 流水號
	private String moc_id;// 製令單(TA001+TA002)
	private String moc_ta006;// 產品品號
	private String moc_ta034;// 產品品名
	private String moc_ta035;// 產品規格
	private Integer moc_ta015;// 預計生產數
	private Integer moc_ta017;// 已生產數
	private String moc_ta009;// (預計)開工日
	private String moc_ta010;// (預計)完工日
	private String moc_ta011;// 狀態碼 1.未生產,2.已發料,3.生產中,Y.已完工,y.指定完工
	private String cop_tc012;// 客戶:訂單單號
	private String cop_tc015;// 客戶:訂單備註
	private String order_id;// 廠內:訂單單號A222-123456789
	// 主要資料 ERP Expansion
	private String bom_kind;// 技轉中 or 可量產
	/*
	 * [{"date":"2022-01-02 10:20:59", "user":"admin", "ms":"XXXX"}, {}]
	 */
	private String moc_note;// 生管 備註事項 json格式
	private String moc_status;// 生管 開單狀態 0=未開注意事項1=已開注意事項2=已核准流程卡
	private Integer moc_priority;// 生管 優先權
	private String inv_note;// 倉庫 備注事項 json格式
	private Integer ivn_status;// 倉庫 備料狀況1=齊料 2=未齊 3=未備
	private String mpr_note;// 物控 備註事項 json格式
	private String mpr_date;// 物控 (預計)齊料日
	private Integer mpr_items;// 物控 缺幾項 物料(共)
	private String mes_note;// 製造 備註事項 json格式

	public Date getSys_create_date() {
		return sys_create_date;
	}

	public void setSys_create_date(Date sys_create_date) {
		this.sys_create_date = sys_create_date;
	}

	public String getSys_create_user() {
		return sys_create_user;
	}

	public void setSys_create_user(String sys_create_user) {
		this.sys_create_user = sys_create_user;
	}

	public Date getSys_modify_date() {
		return sys_modify_date;
	}

	public void setSys_modify_date(Date sys_modify_date) {
		this.sys_modify_date = sys_modify_date;
	}

	public String getSys_modify_user() {
		return sys_modify_user;
	}

	public void setSys_modify_user(String sys_modify_user) {
		this.sys_modify_user = sys_modify_user;
	}

	public Integer getUseful() {
		return useful;
	}

	public void setUseful(Integer useful) {
		this.useful = useful;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMoc_id() {
		return moc_id;
	}

	public void setMoc_id(String moc_id) {
		this.moc_id = moc_id;
	}

	public String getMoc_ta006() {
		return moc_ta006;
	}

	public void setMoc_ta006(String moc_ta006) {
		this.moc_ta006 = moc_ta006;
	}

	public String getMoc_ta034() {
		return moc_ta034;
	}

	public void setMoc_ta034(String moc_ta034) {
		this.moc_ta034 = moc_ta034;
	}

	public String getMoc_ta035() {
		return moc_ta035;
	}

	public void setMoc_ta035(String moc_ta035) {
		this.moc_ta035 = moc_ta035;
	}

	public Integer getMoc_ta015() {
		return moc_ta015;
	}

	public void setMoc_ta015(Integer moc_ta015) {
		this.moc_ta015 = moc_ta015;
	}

	public Integer getMoc_ta017() {
		return moc_ta017;
	}

	public void setMoc_ta017(Integer moc_ta017) {
		this.moc_ta017 = moc_ta017;
	}

	public String getMoc_ta009() {
		return moc_ta009;
	}

	public void setMoc_ta009(String moc_ta009) {
		this.moc_ta009 = moc_ta009;
	}

	public String getMoc_ta010() {
		return moc_ta010;
	}

	public void setMoc_ta010(String moc_ta010) {
		this.moc_ta010 = moc_ta010;
	}

	public String getMoc_ta011() {
		return moc_ta011;
	}

	public void setMoc_ta011(String moc_ta011) {
		this.moc_ta011 = moc_ta011;
	}

	public String getCop_tc012() {
		return cop_tc012;
	}

	public void setCop_tc012(String cop_tc012) {
		this.cop_tc012 = cop_tc012;
	}

	public String getCop_tc015() {
		return cop_tc015;
	}

	public void setCop_tc015(String cop_tc015) {
		this.cop_tc015 = cop_tc015;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getBom_kind() {
		return bom_kind;
	}

	public void setBom_kind(String bom_kind) {
		this.bom_kind = bom_kind;
	}

	public String getMoc_note() {
		return moc_note;
	}

	public void setMoc_note(String moc_note) {
		this.moc_note = moc_note;
	}

	public String getMoc_status() {
		return moc_status;
	}

	public void setMoc_status(String moc_status) {
		this.moc_status = moc_status;
	}

	public Integer getMoc_priority() {
		return moc_priority;
	}

	public void setMoc_priority(Integer moc_priority) {
		this.moc_priority = moc_priority;
	}

	public String getInv_note() {
		return inv_note;
	}

	public void setInv_note(String inv_note) {
		this.inv_note = inv_note;
	}

	public Integer getIvn_status() {
		return ivn_status;
	}

	public void setIvn_status(Integer ivn_status) {
		this.ivn_status = ivn_status;
	}

	public String getMpr_note() {
		return mpr_note;
	}

	public void setMpr_note(String mpr_note) {
		this.mpr_note = mpr_note;
	}

	public String getMpr_date() {
		return mpr_date;
	}

	public void setMpr_date(String mpr_date) {
		this.mpr_date = mpr_date;
	}

	public Integer getMpr_items() {
		return mpr_items;
	}

	public void setMpr_items(Integer mpr_items) {
		this.mpr_items = mpr_items;
	}

	public String getMes_note() {
		return mes_note;
	}

	public void setMes_note(String mes_note) {
		this.mes_note = mes_note;
	}

}
