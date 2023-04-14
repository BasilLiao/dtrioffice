package dtri.com.service;

import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dtri.com.bean.JsonObjBean;
import dtri.com.bean.JsonTemplateBean;
import dtri.com.db.entity.ERP_PM_Entity;
import dtri.com.db.entity.ProductionRecordsEntity;
import dtri.com.db.pgsql.dao.ProductionManagementDao;
import dtri.com.db.pgsql.dao.ProductionRecordsDAO;
import dtri.com.tools.Fm_Time_Model;
import dtri.com.tools.JsonDataModel;

/**
 * @author Basil 登記生產紀錄
 * 
 * 
 **/
@Transactional
@Service
public class ProductionRecordsService {

	@Autowired
	private LoginService loginService;
	@Autowired
	private ProductionRecordsDAO dao;

	@Autowired
	private ProductionManagementDao pm_Dao;

	/**
	 * @param entity
	 * @return 查詢後清單
	 * 
	 **/
	public List<ProductionRecordsEntity> searchAll(ProductionRecordsEntity entity, int offset, int page_total) {
		String all_limit = " OFFSET " + offset + " LIMIT " + page_total;
		List<ProductionRecordsEntity> list = dao.queryProductionRecords(entity, all_limit);
		// 順便移除 過期清單
		ProductionRecordsEntity entityExpired = new ProductionRecordsEntity();
		System.out.println(Fm_Time_Model.to_yMd_Hms(Fm_Time_Model.to_count(-120, new Date())));
		entityExpired.setSys_modify_date(Fm_Time_Model.to_count(-120, new Date()));
		entityExpired.setSys_modify_user("system");
		dao.updateAllExpiredProgress(entityExpired);
		return list;
	}

	/** 寫入 登入生產紀錄 **/
	public boolean addRecords(JSONObject json, int p_status) {
		boolean check = false;
		ProductionRecordsEntity recordsEntity = new ProductionRecordsEntity();

		recordsEntity.setSys_create_date(new Date());
		recordsEntity.setSys_create_user(loginService.getSessionUserBean().getAccount());
		recordsEntity.setSys_modify_date(new Date());
		recordsEntity.setSys_modify_user(loginService.getSessionUserBean().getAccount());
		recordsEntity.setId(json.getString("production_id"));
		recordsEntity.setOrder_id(json.getString("order_id"));
		recordsEntity.setClient_name(json.getString("client_name"));
		recordsEntity.setProduction_quantity(json.getInt("production_quantity"));
		recordsEntity.setProduct_model(json.getString("product_model"));
		recordsEntity.setBom_product_id(json.getString("bom_product_id"));
		recordsEntity.setBom_product_customer_id(json.getString("bom_product_customer_id"));
		recordsEntity.setMfg_part_no(json.getString("mfg_part_no"));
		recordsEntity.setParts_no(json.getString("parts_no"));

		recordsEntity.setBom_id(json.getInt("bom_id"));
		recordsEntity.setVersion_motherboard(json.getString("version_motherboard"));
		recordsEntity.setNote(json.getString("note"));
		recordsEntity.setPm_note(json.getString("pm_note"));
		recordsEntity.setCome_from(json.getString("come_from"));
		recordsEntity.setProduct_status(p_status);
		recordsEntity.setProduct_progress(json.getInt("product_progress"));
		recordsEntity.setBom_type(json.getString("bom_type"));
		recordsEntity.setBom_principal(json.getString("bom_principal"));// bom 負責人
		recordsEntity.setProduct_hope_date(Fm_Time_Model.toDate(json.getString("product_hope_date")));// 期望出貨時間
		/*
		 * Ex:"cb_pkg_all": { "cb_pkg_2": { "cb_pkg": "", "cb_pkg_name": "BOG" },
		 * "cb_pkg_1": { "cb_pkg": "", "cb_pkg_name": "Power cord" },
		 */
		recordsEntity.setProduct_package(json.getJSONObject("cb_pkg_all") + "");// 包裝型態
		/*
		 * "bom_product_content": { "not": [ { "amount": "", "name": "LCDBrightness:",
		 * "content": "Normal" }, "have": [ { "amount": "", "name": "CPU:",
		 * "content":"intel I7-6600U_VPRO" },
		 **/
		recordsEntity.setBom_product_content(json.getJSONObject("bom_product_content") + "");// 產品規格
		recordsEntity.setProduct_type(json.getString("product_type"));// 技轉中 可量產
		// 抓取生管排程資料
		ERP_PM_Entity search_data = new ERP_PM_Entity();
		search_data.setUseful(1);// 有效=1 無效=2
		search_data.setMoc_ta006('%' + json.getString("bom_product_id") + '%');// BOM號
		// --search_data.setMoc_id(json.getString("production_id"));//製令單號
		List<ERP_PM_Entity> erp_pm_ets = pm_Dao.searchERP_PM_List(search_data);
		if (erp_pm_ets.size() > 0) {
			recordsEntity.setProduct_specification(erp_pm_ets.get(0).getMoc_ta035());// 產品規格
			recordsEntity.setProduct_name(erp_pm_ets.get(0).getMoc_ta034());// 產品品名
		}

		if (dao.beforeCheckAddOne(json.getString("production_id")) == null) {
			dao.addOne(recordsEntity);
			check = true;
		}

		return check;
	}

	/** 更新單據進度 **/
	public boolean updateProgress(ProductionRecordsEntity entity) {
		boolean check = false;
		entity.setSys_modify_date(new Date());
		entity.setSys_modify_user(loginService.getSessionUserBean().getAccount());
		if (dao.updateOneProgress(entity) == 1) {
			check = true;
		}
		return check;
	}

	/** 中止_單據進度 **/
	public boolean updateProgress_stop(ProductionRecordsEntity entity) {
		boolean check = false;
		entity.setSys_modify_date(new Date());
		entity.setSys_modify_user(loginService.getSessionUserBean().getAccount());
		if (dao.updateOneStatus(entity) == 1) {
			check = true;
		}
		return check;
	}

	/** 更新單據狀態 **/
	public boolean updateEntity(ProductionRecordsEntity entity) {
		boolean check = false;
		entity.setSys_modify_date(new Date());
		entity.setSys_modify_user(loginService.getSessionUserBean().getAccount());
		if (dao.updateOneContent(entity) == 1) {
			check = true;
		}
		return check;
	}

	/** 檢查換頁碼 如果沒有固定為從0開始 **/
	public int jsonToPageNb(JSONObject content) {
		if (!content.isNull("page_nb") && !content.get("page_nb").equals("")) {
			return content.getInt("page_nb");
		}
		return 0;
	}

	/** 檢查換頁碼 如果沒有固定為100筆資料 **/
	public int jsonToPageTotal(JSONObject content) {
		if (!content.isNull("page_total") && !content.get("page_total").equals("")) {
			return content.getInt("page_total");
		}
		return 100;
	}

	/** JSON to 清單 **/
	public ProductionRecordsEntity jsonToEntities(JSONObject content) {
		ProductionRecordsEntity entity = new ProductionRecordsEntity();

		// 進度? 狀態類型 完成ERP工單(準備物料)=1/完成注意事項(預約生產)=2/完成->流程卡(準備生產)=3/=4/ =5
		if (!content.isNull("product_progress") && content.getInt("product_progress") >= 0)
			entity.setProduct_progress(content.getInt("product_progress"));
		// 工單號?
		if (!content.isNull("id") && !content.getString("id").equals(""))
			entity.setId(content.getString("id"));
		// 工單號 New ?
		if (!content.isNull("new_id") && !content.getString("new_id").equals(""))
			entity.setNew_id(content.getString("new_id"));
		// 類型? 單據類型
		if (!content.isNull("product_status") && content.getInt("product_status") >= 0)
			entity.setProduct_status(content.getInt("product_status"));
		// 類型? 產品SN序號
		if (!content.isNull("product_start_sn") && !content.getString("product_start_sn").equals(""))
			entity.setProduct_start_sn(content.getString("product_start_sn"));
		// BOM? 產品 客戶BOM號
		if (!content.isNull("bom_product_customer_id"))
			entity.setBom_product_customer_id(content.getString("bom_product_customer_id"));
		// 類型? 數量
		if (!content.isNull("production_quantity") && content.getInt("production_quantity") >= 0)
			entity.setProduction_quantity(content.getInt("production_quantity"));
		// 類型? 客戶名稱
		if (!content.isNull("client_name") && !content.getString("client_name").equals(""))
			entity.setClient_name(content.getString("client_name"));
		// 訂單?
		if (!content.isNull("order_id") && !content.getString("order_id").equals(""))
			entity.setOrder_id(content.getString("order_id"));
		// 主附件?
		if (!content.isNull("bom_type") && !content.getString("bom_type").equals(""))
			entity.setBom_type(content.getString("bom_type"));
		// 備註
		if (!content.isNull("note") && !content.getString("note").equals(""))
			entity.setNote(content.getString("note"));
		// 包裝
		if (content.has("cb_pkg_all") && !content.isNull("cb_pkg_all"))
			entity.setProduct_package(content.getJSONObject("cb_pkg_all") + "");
		return entity;
	}

	/** 清單 to JSON **/
	public JSONObject entitiesToJson(List<ProductionRecordsEntity> prs) {
		JSONObject list = new JSONObject();
		JSONArray jsonAll = new JSONArray();
		// 標題
		JSONArray jsonArray = new JSONArray();
		jsonArray.put("工單號碼");
		jsonArray.put("(公司)BOM料號");
		jsonArray.put("(客戶)BOM料號");
		jsonArray.put("生產數");
		jsonArray.put("進度");
		jsonArray.put("狀態");
		jsonArray.put("預計出貨時間");

		jsonArray.put("建立時間");
		jsonArray.put("建立者");
		jsonArray.put("修改時間");
		jsonArray.put("修改者");
		jsonAll.put(jsonArray);
		// 內容 產品清單
		for (ProductionRecordsEntity entity : prs) {
			jsonArray = new JSONArray();

			jsonArray.put(entity.getId());// 0
			jsonArray.put(entity.getBom_product_id());
			jsonArray.put(entity.getBom_product_customer_id());
			jsonArray.put(entity.getProduction_quantity());
			jsonArray.put(entity.getProduct_progress());
			jsonArray.put(entity.getProduct_status());// 5
			jsonArray.put(entity.getProduct_hope_date() == null ? "" : Fm_Time_Model.to_yyMMdd(entity.getProduct_hope_date()));
			jsonArray.put(Fm_Time_Model.to_yMd_Hms(entity.getSys_create_date()));
			jsonArray.put(entity.getSys_create_user());// 8
			jsonArray.put(Fm_Time_Model.to_yMd_Hms(entity.getSys_modify_date()));
			jsonArray.put(entity.getSys_modify_user());// 10

			// 多餘的隱藏
			jsonArray.put(entity.getCome_from());// 11
			jsonArray.put(entity.getProduct_package());// 12
			jsonArray.put(entity.getVersion_motherboard());// 13
			jsonArray.put(entity.getNote());// 14
			jsonArray.put(entity.getOrder_id());// 15
			jsonArray.put(entity.getClient_name());// 16
			jsonArray.put(entity.getProduct_model());// 17
			jsonArray.put(entity.getProduct_start_sn());// 18
			jsonArray.put(entity.getProduct_end_sn());// 19
			jsonArray.put(entity.getBom_product_content());// 20
			jsonArray.put(entity.getProduct_type());// 21
			jsonArray.put(entity.getPm_note());// 22
			jsonArray.put(entity.getBom_type());// 23
			jsonArray.put(entity.getBom_principal());// 24-負責人
			jsonArray.put(entity.getMfg_part_no());// 25
			jsonArray.put(entity.getParts_no());// 26
			jsonArray.put(entity.getProduct_specification());// 27--ERP產品規格
			jsonArray.put(entity.getProduct_name());// 28--產品名稱

			jsonAll.put(jsonArray);
		}
		list.put("item_list", jsonAll);
		return list;
	}

	/**
	 * 統整回傳包裝
	 * 
	 * @param p_Obj     回傳查詢資料
	 * @param frontData 前端傳入的"控制"資訊 回傳 成功
	 **/
	public JSONObject ajaxRspJson(JSONObject p_Obj, JSONObject frontData, String r_Message) {
		JsonDataModel data = new JsonDataModel();
		JSONObject r_allData = new JSONObject();
		JsonObjBean objBean = new JsonObjBean();
		JsonTemplateBean templateBean = new JsonTemplateBean();

		templateBean.setWebPageBody("html/body/production_records_body.html");
		templateBean.setBodyData(p_Obj);

		objBean.setR_cellBackName(frontData.getString("cellBackName"));
		objBean.setR_action(frontData.getString("action"));
		objBean.setR_status(true);
		objBean.setR_cellBackOrder(frontData.getString("cellBackOrder"));
		objBean.setR_message(r_Message);

		data.setR_JsonData(objBean);
		data.setR_template(templateBean);
		r_allData = data.getR_allData();
		return r_allData;
	}

	/**
	 * 統整回傳包裝
	 * 
	 * @param p_Obj     回傳查詢資料
	 * @param frontData 前端傳入的"控制"資訊 回傳 成功
	 **/
	public JSONObject fail_ajaxRspJson(JSONObject frontData, String r_Message) {
		JsonDataModel data = new JsonDataModel();
		JSONObject r_allData = new JSONObject();
		JsonObjBean objBean = new JsonObjBean();
		JsonTemplateBean templateBean = new JsonTemplateBean();

		objBean.setR_cellBackName(frontData.getString("cellBackName"));
		objBean.setR_action(frontData.getString("action"));
		objBean.setR_status(false);
		objBean.setR_cellBackOrder(frontData.getString("cellBackOrder"));
		objBean.setR_message(r_Message);
		// Step5.包裝
		data.setR_JsonData(objBean);
		data.setR_template(templateBean);
		r_allData = data.getR_allData();
		return r_allData;
	}

}
