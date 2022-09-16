package dtri.com.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import dtri.com.bean.MesApiBean;
import dtri.com.db.entity.BomGroupEntity;
import dtri.com.db.entity.BomProductEntity;
import dtri.com.db.entity.GroupEntity;
import dtri.com.db.entity.ProductionRecordsEntity;
import dtri.com.db.entity.ProductionSnEntity;
import dtri.com.db.entity.SoftwareVersionEntity;
import dtri.com.service.APIService;
import dtri.com.service.LoginService;
import dtri.com.service.ProductionPrintService;
import dtri.com.service.ProductionRecordsService;
import dtri.com.service.ProductionSnService;
import dtri.com.service.SoftwareVersionService;
import dtri.com.tools.JsonDataModel;

@Controller
public class ProductionPrintController {

	@Autowired
	LoginService loginService;
	@Autowired
	ProductionPrintService printService;
	@Autowired
	ProductionRecordsService recordsService;
	@Autowired
	SoftwareVersionService softwareVersionService;
	@Autowired
	ProductionSnService snService;
	@Autowired
	APIService apiService;

	// 功能
	final static String SYS_F = "production_print.do";
	// log 訊息
	Logger logger = LogManager.getLogger();

	/**
	 * 
	 * 基本載入 /查詢 列印出檢查 流程卡
	 * 
	 * @param ajaxJSON 限定用JSON
	 */
	@ResponseBody
	@RequestMapping(value = "/production_print", method = { RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	public String search_bom_product(@RequestBody String ajaxJSON) {

		System.out.println("---controller - bom_print");
		// Step1.取出 session 訊息 & 檢查權限
		List<GroupEntity> group = loginService.getSessionGroupBean();
		boolean checkPermission = loginService.checkPermission(group, SYS_F, "01000001");

		// Step2.解析內容-檢查 -> 取出內容物
		JsonDataModel data = new JsonDataModel();
		JSONObject frontData = (JSONObject) data.frontToBack(ajaxJSON).get("data");
		System.out.println(frontData);

		// Step3.建立回傳元素
		JSONObject r_allData = new JSONObject();
		int page_nb = 0, page_total = 100;
		// Step4.檢查許可權 & 輸入物件
		if (checkPermission) {
			// Step4-1 .DB 取出 正確 資料
			// List<BomProductEntity> entity = new ArrayList<BomProductEntity>();
			ProductionRecordsEntity entity2 = new ProductionRecordsEntity();
			entity2.setProduct_progress(2);// 要是生產流程的
			entity2.setProduct_status(1);// 不是作廢的
			List<ProductionRecordsEntity> bpg2 = new ArrayList<ProductionRecordsEntity>();
			List<SoftwareVersionEntity> bpg3 = new ArrayList<SoftwareVersionEntity>();
			ArrayList<ProductionSnEntity> bpg4 = new ArrayList<ProductionSnEntity>();

			if (frontData.get("content") != null && !frontData.get("content").equals("")) {
				entity2 = printService.jsonToEntities2(frontData.getJSONObject("content"));

				// 取得換頁碼 如果沒有 0
				page_nb = printService.jsonToPageNb(frontData.getJSONObject("content"));
				page_total = printService.jsonToPageTotal(frontData.getJSONObject("content"));
			}
			bpg2 = printService.searchProduction(entity2, page_nb, page_total);
			bpg3 = softwareVersionService.searchSoftwareVersion(new SoftwareVersionEntity(), 0, 999999);
			bpg4 = snService.searchAll();
			JSONObject p_Obj = printService.entitiesToJson(bpg2, bpg3, bpg4, null, null, null, 0);

			// Step4-2 .包裝資料
			r_allData = printService.ajaxRspJson(p_Obj, frontData, "訪問成功!!");
		} else {
			// Step4-1 .登出 && 包裝 錯誤 資料
			r_allData = printService.fail_ajaxRspJson(frontData, "你沒有權限!!");
		}

		// Step6.結果回傳
		// System.out.println(r_allData);
		return r_allData.toString();
	}

	/**
	 * 
	 * 修改進度
	 * 
	 * @param ajaxJSON 限定用JSON
	 */
	@ResponseBody
	@RequestMapping(value = "/production_progress_update", method = { RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	public String production_progress(@RequestBody String ajaxJSON) {
		System.out.println("---controller - production_progress");
		String e_code = "0";
		// Step1.取出 session 訊息 & 檢查權限
		List<GroupEntity> group = loginService.getSessionGroupBean();
		boolean checkPermission = loginService.checkPermission(group, SYS_F, "01001101");

		// Step2.解析內容-檢查 -> 取出內容物
		JsonDataModel data = new JsonDataModel();
		JSONObject frontData = (JSONObject) data.frontToBack(ajaxJSON).get("data");
		System.out.println(frontData);

		// Step3.建立回傳元素
		JSONObject r_allData = new JSONObject();
		int page_nb = 0, page_total = 100;
		// Step4.檢查許可權 & 輸入物件
		if (checkPermission) {
			// Step4-1 .DB 取出 正確 資料
			List<SoftwareVersionEntity> bpg3 = new ArrayList<SoftwareVersionEntity>();
			ArrayList<ProductionSnEntity> bpg4 = new ArrayList<ProductionSnEntity>();
			List<BomProductEntity> bomPreEnts = new ArrayList<BomProductEntity>();
			ProductionRecordsEntity entity = new ProductionRecordsEntity();
			SoftwareVersionEntity entity_sv = new SoftwareVersionEntity();
			ArrayList<ProductionSnEntity> entity_sn = new ArrayList<ProductionSnEntity>();

			if (frontData.get("content") != null && !frontData.get("content").equals("")) {
				entity = printService.jsonToEntities2(frontData.getJSONObject("content"));
				entity_sn = printService.jsonToEntities3(frontData.getJSONObject("content"));
				entity_sv = softwareVersionService.jsonToEntities(frontData.getJSONObject("content"));

				page_nb = printService.jsonToPageNb(frontData.getJSONObject("content"));
				page_total = printService.jsonToPageTotal(frontData.getJSONObject("content"));
			}
			boolean checked = false;// 如有流程失敗 則停止
			e_code += "_1";
			// 生產登記序號清單 ()(數量)quantity
			JSONObject sn_obj = new JSONObject();
			String sn_burn_nb = null;
			String sn_burn_fixed = null;
			int sn_burn_quantity = 0;
			// 如果有 自訂SN
			sn_burn_nb = frontData.getJSONObject("content").has("ps_sn_burn_nb") ? frontData.getJSONObject("content").getString("ps_sn_burn_nb") : "";
			sn_burn_fixed = frontData.getJSONObject("content").has("ps_sn_burn_fixed") ? frontData.getJSONObject("content").getString("ps_sn_burn_fixed") : "";
			sn_burn_quantity = entity.getProduction_quantity();
			if (entity_sn != null && entity_sn.size() > 0) {
				// ============自動生成SN============
				sn_obj = snService.analyze_Sn(entity_sn, entity.getProduction_quantity());

				entity.setProduct_start_sn(sn_obj.getJSONArray("sn_list").getString(0));
				entity.setProduct_end_sn(sn_obj.getJSONArray("sn_list").getString(sn_obj.getJSONArray("sn_list").length() - 1));

				// 更新單一項目
				checked = snService.updateSnById((ProductionSnEntity) sn_obj.get("sn_YYWW"));
				checked = snService.updateSnById((ProductionSnEntity) sn_obj.get("sn_000"));
				e_code += "_2";
			} else if (!sn_burn_nb.equals("") && !sn_burn_fixed.equals("")) {
				// ============如果有 自訂SN============
				String b_str_sn = sn_burn_fixed + sn_burn_nb;
				String b_end_sn = sn_burn_fixed
						+ String.format("%0" + sn_burn_nb.length() + "d", (Integer.parseInt(sn_burn_nb) + entity.getProduction_quantity() - 1));
				entity.setProduct_start_sn(b_str_sn);
				entity.setProduct_end_sn(b_end_sn);

				checked = true;
				e_code += "_3";
			} else {
				entity.setProduct_start_sn("");
				entity.setProduct_end_sn("");
				checked = true;
			}

			// 更新(如果更新不到該工單號 ->新的自動登記)
			if (checked) {
				checked = recordsService.updateProgress(entity);
				e_code += "_4";
				if (!checked) {
					checked = recordsService.addRecords(frontData.getJSONObject("content"), 1);
					e_code += "_5";
				}
			}

			// 登記 Or 更新 軟體版本
			if (checked) {
				SoftwareVersionEntity check_entity_sv = new SoftwareVersionEntity();
				check_entity_sv.setId(entity_sv.getId());
				check_entity_sv.setClient_name(entity_sv.getClient_name());
				check_entity_sv.setBom_product_id(entity_sv.getBom_product_id());
				e_code += "_6";
				if (softwareVersionService.searchSoftwareVersion2(check_entity_sv, 0, 999999).size() >= 1) {
					checked = softwareVersionService.updateSoftwareVers(entity_sv);
					e_code += "_7";
				} else {
					e_code += "_8";
					checked = softwareVersionService.addedSoftwareVers(entity_sv);
				}
			}

			// Step4-2 .DB 查詢 正確 資料
			// 取得換頁碼 如果沒有 0
			ProductionRecordsEntity entity_req = new ProductionRecordsEntity();
			entity_req.setProduct_status(1);
			entity_req.setProduct_progress(2);
			List<ProductionRecordsEntity> p_Entities = printService.searchProduction(entity_req, page_nb, page_total);

			// 製令單
			BomProductEntity bomPreEnt = new BomProductEntity();

			bomPreEnt.setBom_number(entity.getBom_product_id());
			bomPreEnt.setId(entity.getBom_id());
			bomPreEnt.setBom_type(entity.getBom_type());
			bomPreEnt.setGroupEntity(new BomGroupEntity());
			bomPreEnts.add(bomPreEnt);	
			// 軟體
			bpg3 = softwareVersionService.searchSoftwareVersion(new SoftwareVersionEntity(), 0, 999999);
			// SN
			bpg4 = snService.searchAll();
			JSONObject p_Obj = printService.entitiesToJson(p_Entities, bpg3, bpg4, sn_obj, sn_burn_fixed, sn_burn_nb, sn_burn_quantity);

			// Step4-2 .包裝資料(如果有登記過了)
			if (checked) {
				// API 串接至 MES (暫時寫死)
				// HttpClient httpClient = HttpClientBuilder.create().build();
				if (!frontData.getJSONObject("content").getString("pr_type").equals("")
						&& !frontData.getJSONObject("content").getString("pr_w_years").equals("")
						&& !frontData.getJSONObject("content").getString("ph_wpro_id").equals("")) {
					MesApiBean mesApi = new MesApiBean();
					mesApi = apiService.mes_setting_bean(frontData.getJSONObject("content"));

					// 更新完 回歸成常序號 到MES
					if (entity_sn != null && entity_sn.size() > 0) {
						entity.setProduct_start_sn(sn_obj.getJSONArray("sn_list").getString(0));
						entity.setProduct_end_sn(sn_obj.getJSONArray("sn_list").getString(sn_obj.getJSONArray("sn_list").length() - 1));
					}

					apiService.mes_production_create(entity, entity_sv, sn_obj, mesApi, (entity_sn.size() > 0));
				}
				r_allData = printService.ajaxRspJson(p_Obj, frontData, "訪問成功!!");
			} else {
				r_allData = printService.fail_ajaxRspJson(frontData, "更新工單號失敗 異常!! error[" + e_code + "]");
			}
		} else {
			// Step4-1 .登出 && 包裝 錯誤 資料
			r_allData = printService.fail_ajaxRspJson(frontData, "你沒有權限!!");
		}

		// Step6.結果回傳
		// System.out.println(r_allData);
		return r_allData.toString();
	}

}
