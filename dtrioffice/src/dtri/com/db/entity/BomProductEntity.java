package dtri.com.db.entity;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;

/**
 * BOM 清單
 * 
 **/
public class BomProductEntity {
	private Date sys_create_date;
	private String sys_create_user;
	private Date sys_modify_date;
	private String sys_modify_user;
	private Integer id;
	private String product_model;
	private String version_motherboard;
	private String bom_number;
	private String note;
	private Integer useful;
	private String checked;
	private Integer kind;
	private String bom_type;
	private String transfer_user;// 轉讓人
	private String mfg_part_no;// MFG Part No(驗證碼)
	private String parts_no;// Parts No(組件號)

	// 例外處理 非SQL 物件(查詢條件)
	private BomGroupEntity groupEntity;
	// 例外處理 非SQL 查詢工單使用
	private String orderId;
	// 例外處理 非SQL 回傳 自動化 生管開單
	private JSONObject tempAutoBomPrint;

	// SQL 回傳條件
	private List<BomGroupEntity> groupEntitis;

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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProduct_model() {
		return product_model;
	}

	public void setProduct_model(String product_model) {
		this.product_model = product_model;
	}

	public String getVersion_motherboard() {
		return version_motherboard;
	}

	public void setVersion_motherboard(String version_motherboard) {
		this.version_motherboard = version_motherboard;
	}

	public String getBom_number() {
		return bom_number;
	}

	public void setBom_number(String bom_number) {
		this.bom_number = bom_number;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Integer getUseful() {
		return useful;
	}

	public void setUseful(Integer useful) {
		this.useful = useful;
	}

	public BomGroupEntity getGroupEntity() {
		return groupEntity;
	}

	public void setGroupEntity(BomGroupEntity groupEntity) {
		this.groupEntity = groupEntity;
	}

	public List<BomGroupEntity> getGroupEntitis() {
		return groupEntitis;
	}

	public void setGroupEntitis(List<BomGroupEntity> groupEntitis) {
		this.groupEntitis = groupEntitis;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public Integer getKind() {
		return kind;
	}

	public void setKind(Integer kind) {
		this.kind = kind;
	}

	public String getBom_type() {
		return bom_type;
	}

	public void setBom_type(String bom_type) {
		this.bom_type = bom_type;
	}

	public String getTransfer_user() {
		return transfer_user;
	}

	public void setTransfer_user(String transfer_user) {
		this.transfer_user = transfer_user;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public JSONObject getTempAutoBomPrint() {
		return tempAutoBomPrint;
	}

	public void setTempAutoBomPrint(JSONObject tempAutoBomPrint) {
		this.tempAutoBomPrint = tempAutoBomPrint;
	}

	public String getMfg_part_no() {
		return mfg_part_no;
	}

	public void setMfg_part_no(String mfg_part_no) {
		this.mfg_part_no = mfg_part_no;
	}

	public String getParts_no() {
		return parts_no;
	}

	public void setParts_no(String parts_no) {
		this.parts_no = parts_no;
	}
}
