package dtri.com.db.entity;

import java.util.Date;

/**
 * @author Basil
 * 
 *         生產紀錄
 **/
public class ProductionRecordsEntity {

	private Date sys_create_date;
	private String sys_create_user;
	private Date sys_modify_date;
	private String sys_modify_user;
	private String new_id;// 廢止 生產紀錄會用到
	private String id;
	private String order_id;
	private String client_name;
	private Integer production_quantity;
	private String product_model;
	private Integer bom_id;
	private String bom_product_id;
	private String bom_product_content;// bom規格json{}
	private String bom_product_customer_id;// 客戶定義用

	private String version_motherboard;// 主板號
	private String note;
	private String pm_note;// 專案經理NOTE
	private String come_from;// 來源
	private Integer product_progress;
	private Integer product_status;
	private String product_start_sn;
	private String product_end_sn;
	private String bom_type;// BOM 類型 product = 產品BOM /accessories = 配件BOM
	private String bom_principal;// 負責人

	private Date product_hope_date;// 期望日
	private Date product_check_date;// 檢核日
	private Date product_finish_date;// 完工日
	private String product_package;// 包裝json{}
	private String product_type;// 產品狀態 技轉中 / 可量產
	private String product_name;// 產品品名
	private String product_specification;// 產品規格

	private String mfg_part_no;// MFG Part No(驗證碼)
	private String parts_no;// Parts No(組件號)

	public Date getProduct_hope_date() {
		return product_hope_date;
	}

	public void setProduct_hope_date(Date product_hope_date) {
		this.product_hope_date = product_hope_date;
	}

	public Date getProduct_check_date() {
		return product_check_date;
	}

	public void setProduct_check_date(Date product_check_date) {
		this.product_check_date = product_check_date;
	}

	public Date getProduct_finish_date() {
		return product_finish_date;
	}

	public void setProduct_finish_date(Date product_finish_date) {
		this.product_finish_date = product_finish_date;
	}

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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getClient_name() {
		return client_name;
	}

	public void setClient_name(String client_name) {
		this.client_name = client_name;
	}

	public Integer getProduction_quantity() {
		return production_quantity;
	}

	public void setProduction_quantity(Integer production_quantity) {
		this.production_quantity = production_quantity;
	}

	public String getProduct_model() {
		return product_model;
	}

	public void setProduct_model(String product_model) {
		this.product_model = product_model;
	}

	public String getBom_product_id() {
		return bom_product_id;
	}

	public void setBom_product_id(String bom_product_id) {
		this.bom_product_id = bom_product_id;
	}

	public String getVersion_motherboard() {
		return version_motherboard;
	}

	public void setVersion_motherboard(String version_motherboard) {
		this.version_motherboard = version_motherboard;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getCome_from() {
		return come_from;
	}

	public void setCome_from(String come_from) {
		this.come_from = come_from;
	}

	public Integer getProduct_status() {
		return product_status;
	}

	public void setProduct_status(Integer product_status) {
		this.product_status = product_status;
	}

	public Integer getProduct_progress() {
		return product_progress;
	}

	public void setProduct_progress(Integer product_progress) {
		this.product_progress = product_progress;
	}

	public Integer getBom_id() {
		return bom_id;
	}

	public void setBom_id(Integer bom_id) {
		this.bom_id = bom_id;
	}

	public String getNew_id() {
		return new_id;
	}

	public void setNew_id(String new_id) {
		this.new_id = new_id;
	}

	public String getProduct_start_sn() {
		return product_start_sn;
	}

	public void setProduct_start_sn(String product_start_sn) {
		this.product_start_sn = product_start_sn;
	}

	public String getProduct_end_sn() {
		return product_end_sn;
	}

	public void setProduct_end_sn(String product_end_sn) {
		this.product_end_sn = product_end_sn;
	}

	public String getBom_type() {
		return bom_type;
	}

	public void setBom_type(String bom_type) {
		this.bom_type = bom_type;
	}

	public String getBom_product_customer_id() {
		return bom_product_customer_id;
	}

	public void setBom_product_customer_id(String bom_product_customer_id) {
		this.bom_product_customer_id = bom_product_customer_id;
	}

	public String getProduct_package() {
		return product_package;
	}

	public void setProduct_package(String product_package) {
		this.product_package = product_package;
	}

	public String getBom_product_content() {
		return bom_product_content;
	}

	public void setBom_product_content(String bom_product_content) {
		this.bom_product_content = bom_product_content;
	}

	public String getProduct_type() {
		return product_type;
	}

	public void setProduct_type(String product_type) {
		this.product_type = product_type;
	}

	public String getBom_principal() {
		return bom_principal;
	}

	public void setBom_principal(String bom_principal) {
		this.bom_principal = bom_principal;
	}

	public String getPm_note() {
		return pm_note;
	}

	public void setPm_note(String pm_note) {
		this.pm_note = pm_note;
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

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getProduct_specification() {
		return product_specification;
	}

	public void setProduct_specification(String product_specification) {
		this.product_specification = product_specification;
	}

}
