package dtri.com.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import dtri.com.db.entity.ERP_BOMMD_Entity;
import dtri.com.db.entity.ERP_INVMB_Entity;
import dtri.com.db.entity.ERP_INVMC_Entity;
import dtri.com.db.entity.ERP_MOC_PUR_Entity;
import dtri.com.db.entity.GroupEntity;
import dtri.com.models.JsonDataModel;
import dtri.com.service.ERP_BomService;
import dtri.com.service.LoginService;

@Controller
public class ErpBomController {

	@Autowired
	LoginService loginService;
	@Autowired
	ERP_BomService bomService;
	// 功能
	final static String SYS_F = "erp_bom.do";

	/**
	 * 
	 * 基本載入 /查詢
	 * 
	 * @param ajaxJSON 限定用JSON
	 */
	@ResponseBody
	@RequestMapping(value = "/erp_bom", method = { RequestMethod.POST }, produces = "application/json;charset=UTF-8")
	public String search_erp_bom(@RequestBody String ajaxJSON) {
		System.out.println("---controller - erp_bom");
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
			ERP_INVMB_Entity entity = new ERP_INVMB_Entity();
			ERP_INVMC_Entity entity2 = new ERP_INVMC_Entity();
			ERP_BOMMD_Entity entity3 = new ERP_BOMMD_Entity();
			ERP_MOC_PUR_Entity entity4 = new ERP_MOC_PUR_Entity();
			if (frontData.get("content") != null && !frontData.get("content").equals("")) {
				entity = bomService.jsonToEntities(frontData.getJSONObject("content"));
				entity2 = bomService.jsonToEntities2(frontData.getJSONObject("content"));
				entity3 = bomService.jsonToEntities3(frontData.getJSONObject("content"));
				entity4 = bomService.jsonToEntities4(frontData.getJSONObject("content"));

				page_nb = bomService.jsonToPageNb(frontData.getJSONObject("content"));
				page_total = bomService.jsonToPageTotal(frontData.getJSONObject("content"));
			}
			List<ERP_INVMB_Entity> p_Entities = new ArrayList<ERP_INVMB_Entity>();
			List<ERP_INVMC_Entity> p_Entities2 = new ArrayList<ERP_INVMC_Entity>();
			List<ERP_BOMMD_Entity> p_Entities3 = new ArrayList<ERP_BOMMD_Entity>();
			List<ERP_MOC_PUR_Entity> p_Entities4 = new ArrayList<ERP_MOC_PUR_Entity>();
			switch (frontData.getString("cellBackOrder")) {
			case "search_update":
				p_Entities = bomService.searchINVMB(entity, page_nb, page_total);
				break;
			case "search_stock":
				p_Entities2 = bomService.searchINVMC(entity2);
				break;
			case "search_bom":
				p_Entities3 = bomService.searchBOMMD(entity3);
				break;
			case "search_stock_c":
				p_Entities4 = bomService.searchMOCPUR(entity4);
				break;
			default:
				p_Entities = bomService.searchINVMB(entity, page_nb, page_total);
				p_Entities2 = bomService.searchINVMC(entity2);
				p_Entities3 = bomService.searchBOMMD(entity3);
				p_Entities4 = bomService.searchMOCPUR(entity4);
				break;

			}
			JSONObject p_Obj = bomService.entitiesToJson(p_Entities);
			p_Obj = bomService.entitiesToJson2(p_Entities2, p_Obj);
			p_Obj = bomService.entitiesToJson3(p_Entities3, p_Obj);
			p_Obj = bomService.entitiesToJson4(p_Entities4, p_Obj);

			// Step4-2 .包裝資料
			r_allData = bomService.ajaxRspJson(p_Obj, frontData, "訪問成功!!");
		} else {
			// Step4-1 .登出 && 包裝 錯誤 資料
			r_allData = bomService.fail_ajaxRspJson(frontData, "你沒有權限!!");
		}

		// Step6.結果回傳
		System.out.println(r_allData);
		return r_allData.toString();
	}

}
