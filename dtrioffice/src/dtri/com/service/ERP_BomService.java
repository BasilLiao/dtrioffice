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
import dtri.com.db.entity.ERP_BOMMD_Entity;
import dtri.com.db.entity.ERP_INVMB_Entity;
import dtri.com.db.entity.ERP_INVMC_Entity;
import dtri.com.db.entity.ERP_MOC_PUR_Entity;
import dtri.com.db.mssql.dao.ERP_BomDao;
import dtri.com.models.Fm_Time_Model;
import dtri.com.models.JsonDataModel;

@Transactional
@Service
public class ERP_BomService {
	@Autowired
	private ERP_BomDao erpDao;
	// @Autowired
	// private LoginService loginService;

	/**
	 * @param entity 查詢 料號
	 * @return 查詢後清單
	 * 
	 **/
	public List<ERP_INVMB_Entity> searchINVMB(ERP_INVMB_Entity entity, int offset, int page_total) {
		String all_limit = "OFFSET " + offset + " ROWS FETCH FIRST " + page_total + " ROWS ONLY";
		List<ERP_INVMB_Entity> list = erpDao.queryERP_INVMB_List(entity, all_limit);
		return list;
	}

	/**
	 * @param entity 查詢 庫存
	 * @return 查詢後清單
	 * 
	 **/
	public List<ERP_INVMC_Entity> searchINVMC(ERP_INVMC_Entity entity) {

		List<ERP_INVMC_Entity> list = erpDao.queryERP_INVMC_List(entity);
		return list;
	}

	/**
	 * @param entity 查詢 BOM
	 * @return 查詢後清單
	 * 
	 **/
	public List<ERP_BOMMD_Entity> searchBOMMD(ERP_BOMMD_Entity entity) {

		List<ERP_BOMMD_Entity> list = erpDao.queryERP_BOMMD_List(entity);
		return list;
	}

	/**
	 * @param entity 查詢 未來異動
	 * @return 查詢後清單
	 * 
	 **/
	public List<ERP_MOC_PUR_Entity> searchMOCPUR(ERP_MOC_PUR_Entity entity) {

		List<ERP_MOC_PUR_Entity> list = erpDao.queryERP_MOC_PUR_List(entity);
		return list;
	}

	/** JSON to 清單 **/
	public ERP_INVMB_Entity jsonToEntities(JSONObject content) {
		ERP_INVMB_Entity entity = new ERP_INVMB_Entity();

		if (!content.isNull("MB001") && !content.getString("MB001").equals(""))
			entity.setMb001(content.getString("MB001"));

		if (!content.isNull("MB002") && !content.getString("MB002").equals(""))
			entity.setMb002(content.getString("MB002"));

		if (!content.isNull("MB003") && !content.getString("MB003").equals(""))
			entity.setMb003(content.getString("MB003"));

		if (!content.isNull("MB017") && !content.getString("MB017").equals(""))
			entity.setMb017(content.getString("MB017"));

		if (!content.isNull("MC002") && !content.getString("MC002").equals(""))
			entity.setMc002(content.getString("MC002"));
		entity.setMc005("Y");
		return entity;
	}

	/** JSON to 清單 **/
	public ERP_INVMC_Entity jsonToEntities2(JSONObject content) {
		ERP_INVMC_Entity entity = new ERP_INVMC_Entity();

		if (!content.isNull("stock_MC001") && !content.getString("stock_MC001").equals(""))
			entity.setMc001(content.getString("stock_MC001"));
		return entity;
	}

	/** JSON to 清單 **/
	public ERP_BOMMD_Entity jsonToEntities3(JSONObject content) {
		ERP_BOMMD_Entity entity = new ERP_BOMMD_Entity();

		if (!content.isNull("bom_MD001") && !content.getString("bom_MD001").equals(""))
			entity.setMd001(content.getString("bom_MD001"));
		return entity;
	}

	/** JSON to 清單 **/
	public ERP_MOC_PUR_Entity jsonToEntities4(JSONObject content) {
		ERP_MOC_PUR_Entity entity = new ERP_MOC_PUR_Entity();
		entity.setTp003(Fm_Time_Model.to_yMd(new Date()));
		if (!content.isNull("stock_c_S001") && !content.getString("stock_c_S001").equals(""))
			entity.setS000(content.getString("stock_c_S001"));

		return entity;
	}

	/**
	 * 清單 to JSON
	 * 
	 * @param p_Entities 物料清單資料
	 **/
	public JSONObject entitiesToJson(List<ERP_INVMB_Entity> p_Entities) {
		JSONObject list = new JSONObject();
		JSONArray jsonAll = new JSONArray();
		// 標題
		JSONArray jsonArray = new JSONArray();

		jsonArray.put("品號");
		jsonArray.put("品名");
		jsonArray.put("規格");
		// 內容
		jsonArray.put("內容描述");
		jsonArray.put("主要庫別");
		jsonArray.put("倉庫名稱");
		jsonArray.put("有效倉別");
		jsonArray.put("id");
		jsonAll.put(jsonArray);
		// 內容
		int id = 1;
		for (ERP_INVMB_Entity entity : p_Entities) {
			jsonArray = new JSONArray();
			// 內容
			jsonArray.put(entity.getMb001());
			jsonArray.put(entity.getMb002());
			jsonArray.put(entity.getMb003());
			jsonArray.put(entity.getMb009());
			jsonArray.put(entity.getMb017());
			jsonArray.put(entity.getMc002());
			jsonArray.put(entity.getMc005());
			jsonArray.put("ID_" + id++);

			jsonAll.put(jsonArray);
		}
		list.put("list", jsonAll);
		return list;
	}

	/**
	 * 清單 to JSON
	 * 
	 * @param p_Entities 庫別清單資料
	 **/
	public JSONObject entitiesToJson2(List<ERP_INVMC_Entity> p_Entities, JSONObject list) {
		JSONArray jsonAll = new JSONArray();
		// 標題
		JSONArray jsonArray = new JSONArray();

		jsonArray.put("品號");
		jsonArray.put("庫別");
		jsonArray.put("庫別名稱");
		// 內容
		jsonArray.put("儲位");
		jsonArray.put("庫存數量");
		jsonArray.put("安全數量");
		jsonArray.put("最近入庫日");
		jsonArray.put("最近出庫日");
		jsonArray.put("id");
		jsonAll.put(jsonArray);
		// 內容
		int id = 1;
		for (ERP_INVMC_Entity entity : p_Entities) {
			jsonArray = new JSONArray();
			// 內容
			jsonArray.put(entity.getMc001());
			jsonArray.put(entity.getMc002());
			jsonArray.put(entity.getCmc002());
			jsonArray.put(entity.getMc003());
			jsonArray.put(entity.getMc007().equals("") ? "" : entity.getMc007().split("\\.")[0]);
			jsonArray.put(entity.getMc004().equals("") ? "" : entity.getMc004().split("\\.")[0]);
			jsonArray.put(entity.getMc012());
			jsonArray.put(entity.getMc013());
			jsonArray.put("ID_" + id++);

			jsonAll.put(jsonArray);
		}
		list.put("list_stock", jsonAll);
		return list;
	}

	/**
	 * 清單 to JSON
	 * 
	 * @param p_Entities BOM 庫別清單資料
	 **/
	public JSONObject entitiesToJson3(List<ERP_BOMMD_Entity> p_Entities, JSONObject list) {
		JSONArray jsonAll = new JSONArray();
		// 標題
		JSONArray jsonArray = new JSONArray();

		jsonArray.put("主件品號");
		jsonArray.put("元件-品號");
		jsonArray.put("製程");
		jsonArray.put("元件-品名");
		// 內容
		jsonArray.put("元件-描述");
		jsonArray.put("元件-數");
		jsonArray.put("元件-生效日");
		jsonArray.put("元件-失效日");
		jsonArray.put("備註");
		jsonArray.put("元件-腳位");
		jsonArray.put("id");

		jsonAll.put(jsonArray);
		// 內容
		int id = 1;
		for (ERP_BOMMD_Entity entity : p_Entities) {
			jsonArray = new JSONArray();
			// 內容
			jsonArray.put(entity.getMd001());
			jsonArray.put(entity.getMd003());
			jsonArray.put(entity.getMd009());
			jsonArray.put(entity.getMb002());
			jsonArray.put(entity.getMb003());
			jsonArray.put(entity.getMd006().equals("") ? "" : entity.getMd006().split("\\.")[0]);
			jsonArray.put(entity.getMd011());
			jsonArray.put(entity.getMd012());
			jsonArray.put(entity.getMd016());
			jsonArray.put(entity.getMd015());
			jsonArray.put("ID_" + id++);

			jsonAll.put(jsonArray);
		}
		list.put("list_bom", jsonAll);
		return list;
	}

	/**
	 * 清單 to JSON
	 * 
	 * @param p_Entities 未來異動清單資料
	 **/
	public JSONObject entitiesToJson4(List<ERP_MOC_PUR_Entity> p_Entities, JSONObject list) {
		JSONArray jsonAll = new JSONArray();
		// 標題
		JSONArray jsonArray = new JSONArray();

		jsonArray.put("預計時間");
		jsonArray.put("異動別");
		jsonArray.put("倉庫名");
		jsonArray.put("倉庫別");
		// 內容
		jsonArray.put("領出量");
		jsonArray.put("入庫量");
		jsonArray.put("備註(工單號 訂單 ...等等)");
		jsonArray.put("id");
		jsonAll.put(jsonArray);
		// 內容
		int id = 1;
		for (ERP_MOC_PUR_Entity entity : p_Entities) {
			jsonArray = new JSONArray();
			// 內容
			String tp003 = "";
			if (entity.getTp003() != null & !entity.getTp003().equals("")) {
				tp003 = entity.getTp003().subSequence(0, 4) + "-" + entity.getTp003().subSequence(4, 6) + "-" + entity.getTp003().subSequence(6, 8);
			}
			jsonArray.put(tp003);
			jsonArray.put(entity.getTp000());
			jsonArray.put(entity.getTp001());
			jsonArray.put(entity.getTp002());
			jsonArray.put(entity.getTp004().equals("") ? "" : entity.getTp004().split("\\.")[0]);
			jsonArray.put(entity.getTp005().equals("") ? "" : entity.getTp005().split("\\.")[0]);
			jsonArray.put(entity.getNote000());
			jsonArray.put("ID_" + id++);
			jsonAll.put(jsonArray);
		}
		list.put("list_stock_c", jsonAll);
		return list;
	}

	/**
	 * 統整回傳包裝
	 * 
	 * @param p_Obj     回傳查詢資料
	 * @param frontData 前端傳入的"控制"資訊 回傳 成功
	 * @param r_Message 回傳訊息
	 **/
	public JSONObject ajaxRspJson(JSONObject p_Obj, JSONObject frontData, String r_Message) {
		JsonDataModel data = new JsonDataModel();
		JSONObject r_allData = new JSONObject();
		JsonObjBean objBean = new JsonObjBean();
		JsonTemplateBean templateBean = new JsonTemplateBean();

		templateBean.setWebPageBody("html/body/erp_bom_body.html");
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
}
