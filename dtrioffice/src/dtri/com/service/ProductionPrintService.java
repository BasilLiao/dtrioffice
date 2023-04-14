package dtri.com.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dtri.com.bean.JsonObjBean;
import dtri.com.bean.JsonTemplateBean;
import dtri.com.db.entity.ProductionRecordsEntity;
import dtri.com.db.entity.ProductionSnEntity;
import dtri.com.db.entity.SoftwareVersionEntity;
import dtri.com.db.pgsql.dao.ProductionRecordsDAO;
import dtri.com.tools.Fm_Time_Model;
import dtri.com.tools.JsonDataModel;

@Transactional
@Service
public class ProductionPrintService {
	@Autowired
	private ProductionRecordsDAO productionDao;
	@Autowired
	private APIService apiService;

	public List<ProductionRecordsEntity> searchProduction(ProductionRecordsEntity entity, int offset, int page_total) {
		String all_limit = " OFFSET " + offset + " LIMIT " + page_total;
		List<ProductionRecordsEntity> entitys = productionDao.queryProductionRecords(entity, all_limit);
		return entitys;
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

	/** JSON to 清單(解析 製令單) **/
	public ProductionRecordsEntity jsonToEntities2(JSONObject content) {
		ProductionRecordsEntity entity = new ProductionRecordsEntity();

		// 查詢?(BOM 表單)
		// 產品型號?
		if (!content.isNull("product_model") && !content.get("product_model").equals(""))
			entity.setProduct_model(content.getString("product_model"));
		// 總數?
		if (!content.isNull("production_quantity") && !content.get("production_quantity").equals(""))
			entity.setProduction_quantity(content.getInt("production_quantity"));
		// BOM ID
		if (!content.isNull("bom_id") && !content.get("bom_id").equals(""))
			entity.setBom_id(content.getInt("bom_id"));
		// 工單號?
		if (!content.isNull("production_id") && !content.get("production_id").equals(""))
			entity.setId(content.getString("production_id"));
		// 產品料號
		if (!content.isNull("bom_product_id") && !content.get("bom_product_id").equals(""))
			entity.setBom_product_id(content.getString("bom_product_id"));
		// 產品料號 客戶 BOM
		if (!content.isNull("bom_product_c_id") && !content.get("bom_product_c_id").equals(""))
			entity.setBom_product_customer_id(content.getString("bom_product_c_id"));
		// 進度? 狀態類型 完成ERP工單(準備物料)=1/完成注意事項(預約生產)=2/完成->流程卡(準備生產)=3/=4/ =5
		if (!content.isNull("product_progress") && content.getInt("product_progress") >= 0)
			entity.setProduct_progress(content.getInt("product_progress"));
		// PM 備註
		if (!content.isNull("note") && !content.get("note").equals(""))
			entity.setNote(content.getString("note"));
		// 客戶名稱
		if (!content.isNull("client_name") && !content.get("client_name").equals(""))
			entity.setClient_name(content.getString("client_name"));
		// 訂單
		if (!content.isNull("order_id") && !content.get("order_id").equals(""))
			entity.setOrder_id(content.getString("order_id"));
		// BOM type
		if (!content.isNull("bom_type") && !content.get("bom_type").equals(""))
			entity.setBom_type(content.getString("bom_type"));

		// 類型? 單據類型
		if (!content.isNull("product_status") && content.getInt("product_status") >= 0) {
			entity.setProduct_status(content.getInt("product_status"));
		} else {
			entity.setProduct_status(1);
		}
		// 規格
		if (!content.isNull("bom_product_content") && !content.get("bom_product_content").equals(""))
			entity.setBom_product_content(content.getString("bom_product_content"));

		// 預計出貨日
		if (!content.isNull("product_hope_date") && !content.get("product_hope_date").equals(""))
			entity.setProduct_hope_date(Fm_Time_Model.toDate(content.getString("product_hope_date")));
		// 產品規格敘述
		if (!content.isNull("pr_specification") && !content.get("pr_specification").equals(""))
			entity.setProduct_specification(content.getString("pr_specification"));
		// 產品名稱
		if (!content.isNull("pr_name") && !content.get("pr_name").equals(""))
			entity.setProduct_name(content.getString("pr_name"));

		return entity;
	}

	/** JSON to 清單(解析 SN 序號版本) **/
	public ArrayList<ProductionSnEntity> jsonToEntities3(JSONObject content) {
		ArrayList<ProductionSnEntity> p_s_e = new ArrayList<ProductionSnEntity>();
		JSONArray sn_j_a = content.getJSONArray("sn");

		for (int i = 0; i < sn_j_a.length(); i++) {
			ProductionSnEntity p_s_n = new ProductionSnEntity();
			p_s_n.setSn_group(sn_j_a.getJSONObject(i).getInt("sn_group"));
			p_s_n.setSn_name(sn_j_a.getJSONObject(i).getString("sn_name"));
			p_s_n.setSn_value(sn_j_a.getJSONObject(i).getString("sn_value"));
			p_s_n.setSn_id(sn_j_a.getJSONObject(i).getInt("sn_id"));
			p_s_e.add(p_s_n);
		}

		return p_s_e;
	}

	/** 清單 to JSON **/
	public JSONObject entitiesToJson(List<ProductionRecordsEntity> bpg2, List<SoftwareVersionEntity> bpg3, ArrayList<ProductionSnEntity> bpg4,
			JSONObject sn_obj, String sn_burn_fixed, String sn_burn_nb, int sn_burn_quantity) {
		JSONObject list = new JSONObject();
		JSONObject item_All = new JSONObject();
		JSONArray jsonAll = new JSONArray();
		JSONArray jsonArray = new JSONArray();

		// 需生產製令單
		// 標題
		jsonArray = new JSONArray();
		jsonAll = new JSONArray();
		jsonArray.put("建立時間");
		jsonArray.put("建立者");
		jsonArray.put("工單號碼");
		jsonArray.put("BID");
		jsonArray.put("BOM料號(公司)");
		jsonArray.put("BOM料號(客戶)");
		jsonArray.put("生產數量");
		jsonArray.put("訂單編號");
		jsonArray.put("客戶名稱");
		jsonArray.put("產品型號");

		jsonArray.put("主機板(硬體)-版本");
		jsonArray.put("單據來源");
		jsonArray.put("主/配件");

		jsonArray.put("修改時間");
		jsonArray.put("修改者");
		jsonAll.put(jsonArray);
		// 內容 產品清單
		for (ProductionRecordsEntity entity : bpg2) {
			jsonArray = new JSONArray();

			jsonArray.put(Fm_Time_Model.to_yMd_Hms(entity.getSys_create_date()));
			jsonArray.put(entity.getSys_create_user());
			jsonArray.put(entity.getId());
			jsonArray.put(entity.getBom_id());
			jsonArray.put(entity.getBom_product_id());
			jsonArray.put(entity.getBom_product_customer_id() == null ? "" : entity.getBom_product_customer_id());
			jsonArray.put(entity.getProduction_quantity());
			jsonArray.put(entity.getOrder_id());
			jsonArray.put(entity.getClient_name());
			jsonArray.put(entity.getProduct_model());
			jsonArray.put(entity.getVersion_motherboard());// s_10
			jsonArray.put(entity.getCome_from());
			jsonArray.put(entity.getBom_type());
			jsonArray.put(Fm_Time_Model.to_yMd_Hms(entity.getSys_modify_date()));
			jsonArray.put(entity.getSys_modify_user());
			// 隱藏
			jsonArray.put(entity.getProduct_hope_date() != null ? Fm_Time_Model.to_yyMMdd(entity.getProduct_hope_date()) : "");// s_15 產品-預計時間
			jsonArray.put(entity.getBom_product_content());// s_16 產品規格-規格
			jsonArray.put(entity.getProduct_type());// s_17 產品-狀態
			jsonArray.put(entity.getBom_principal());// s_18 產品規格-負責人
			jsonArray.put(entity.getMfg_part_no());// s_19 產品 驗證碼
			jsonArray.put(entity.getParts_no());// s_20 組件號
			jsonArray.put(entity.getProduct_name());// s_21 產品品號
			jsonArray.put(entity.getProduct_specification());// s_22 產品敘述

			jsonAll.put(jsonArray);
		}

		list.put("production_list", jsonAll);
		// 軟體定義
		if (bpg3 != null) {
			jsonArray = new JSONArray();
			// 內容 軟體清單
			for (SoftwareVersionEntity entity : bpg3) {
				jsonArray = new JSONArray();
				jsonArray.put(entity.getId());
				jsonArray.put(entity.getBom_id());

				jsonArray.put(entity.getBom_product_id());
				jsonArray.put(entity.getClient_name());

				jsonArray.put(entity.getMb_ver());
				jsonArray.put(entity.getMb_ver_ecn());
				jsonArray.put(entity.getNvram());

				jsonArray.put(entity.getBios());
				jsonArray.put(entity.getProduct_model_in());
				jsonArray.put(entity.getEc());
				jsonArray.put(entity.getOs());

				jsonArray.put(entity.getNote());
				jsonArray.put(entity.getNote1());
				jsonArray.put(entity.getNote2());
				item_All.put(entity.getClient_name() + '-' + entity.getBom_product_id(), jsonArray);
			}
			list.put("softwareVer_list", item_All);

		}
		// 產品規則SN條碼-標題 目前SN序列
		JSONArray r_list = new JSONArray();

		// 內容 產品清單
		bpg4.stream().forEach(i -> {
			// 單個
			JSONObject one = new JSONObject();
			one.put("sn_group", i.getSn_group());
			one.put("sn_group_name", i.getSn_group_name());
			one.put("sn_group_sort", i.getSn_group_sort());
			one.put("sn_id", i.getSn_id());
			one.put("sn_name", i.getSn_name());
			one.put("sn_value", i.getSn_value());
			r_list.put(one);
		});

		// 如果有客製化標籤(添加 sn_list_burn)
		JSONArray labs_c = new JSONArray();
		if (sn_burn_nb != null && !sn_burn_nb.equals("") && sn_burn_fixed != null && !sn_burn_fixed.equals("") && sn_burn_quantity > 0) {

			int sn_l = sn_burn_nb.length();
			for (int j = 0; j < sn_burn_quantity; j++) {
				// sn_burn_fixed
				String burn_nb = String.format("%0" + sn_l + "d", Integer.parseInt(sn_burn_nb) + j);
				burn_nb = sn_burn_fixed + burn_nb;
				labs_c.put(burn_nb);
			}
			sn_obj.put("sn_list_burn", labs_c);
		}
		list.put("sn_all_list", r_list);
		list.put("sn_all_nb", sn_obj);

		// 製令單類型
		JSONArray a_val = new JSONArray();
		a_val.put((new JSONObject()).put("value", "一般製令_No(sn)").put("key", "A511_no_sn"));
		a_val.put((new JSONObject()).put("value", "一般製令_Both(sn)").put("key", "A511_no_and_has_sn"));
		a_val.put((new JSONObject()).put("value", "一般製令_Create(sn)").put("key", "A511_has_sn"));

		a_val.put((new JSONObject()).put("value", "重工製令_Old(sn)").put("key", "A521_old_sn"));
		a_val.put((new JSONObject()).put("value", "重工製令_Both(sn)").put("key", "A521_no_and_has_sn"));
		a_val.put((new JSONObject()).put("value", "重工製令_Create(sn)").put("key", "A521_has_sn"));

		a_val.put((new JSONObject()).put("value", "維護製令_No(sn)").put("key", "A522_service"));
		a_val.put((new JSONObject()).put("value", "拆解製令_No(sn)").put("key", "A431_disassemble"));
		a_val.put((new JSONObject()).put("value", "委外製令_No(sn)").put("key", "A512_outside"));
		list.put("pr_type", a_val);

		// 製令單類型
		a_val = new JSONArray();
		a_val.put((new JSONObject()).put("value", "1_year").put("key", "1"));
		a_val.put((new JSONObject()).put("value", "2_year").put("key", "2"));
		a_val.put((new JSONObject()).put("value", "3_year").put("key", "3"));
		a_val.put((new JSONObject()).put("value", "4_year").put("key", "4"));
		a_val.put((new JSONObject()).put("value", "5_year").put("key", "5"));
		a_val.put((new JSONObject()).put("value", "6_year").put("key", "6"));
		a_val.put((new JSONObject()).put("value", "7_year").put("key", "7"));
		a_val.put((new JSONObject()).put("value", "8_year").put("key", "8"));
		a_val.put((new JSONObject()).put("value", "9_year").put("key", "9"));
		a_val.put((new JSONObject()).put("value", "10_year").put("key", "10"));
		list.put("pr_w_years", a_val);

		// 製令單類型
		a_val = new JSONArray();
		a_val.put((new JSONObject()).put("value", "預設流程").put("key", "1"));
		list.put("ph_wpro_id", apiService.mes_WorkstationProgramList());

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

		templateBean.setWebPageBody("html/body/production_print_body.html");
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
