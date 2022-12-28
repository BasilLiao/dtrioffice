package dtri.com.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dtri.com.bean.BomProductGroupBean;
import dtri.com.bean.JsonObjBean;
import dtri.com.bean.JsonTemplateBean;
import dtri.com.db.entity.BomGroupEntity;
import dtri.com.db.entity.BomProductEntity;
import dtri.com.db.entity.BomTypeItemEntity;
import dtri.com.db.entity.ERP_INVMB_Entity;
import dtri.com.db.entity.ERP_MoctaEntity;
import dtri.com.db.mssql.dao.ERP_MoctaDao;
import dtri.com.db.pgsql.dao.BomAccessoriesProductDao;
import dtri.com.db.pgsql.dao.BomAccessoriesTypeItemDao;
import dtri.com.db.pgsql.dao.BomProductDao;
import dtri.com.db.pgsql.dao.BomTypeItemDao;
import dtri.com.tools.Fm_Time_Model;
import dtri.com.tools.JsonDataModel;

@Transactional
@Service
public class BomPrintService {
	@Autowired
	private BomProductDao productDao;
	@Autowired
	private BomTypeItemDao itemDao;

	@Autowired
	private BomAccessoriesProductDao productAccDao;
	@Autowired
	private BomAccessoriesTypeItemDao itemAccDao;
	@Autowired
	private ERP_MoctaDao moctaDao;
	@Autowired
	private LoginService loginService;

	/**
	 * @param entitys    查詢條件
	 * @param offset     幾頁
	 * @param page_total 數量
	 * @return 查詢後清單
	 * 
	 **/
	public BomProductGroupBean search(List<BomProductEntity> entitys, int offset, int page_total) {
		// 防止輸入為空 或 null
		BomProductGroupBean bpg = new BomProductGroupBean();
		String product_model = "  ";// 產品 型號
		String type_item_group_id = "  ";// 群組ID
		String type_item_id = " ";// 項目ID
		String product_bom_nb = " ";// 對應的BOM
		String all_where_product = " ";// SQL BOM 條件
		String all_where_group = " ";// SQL 群組 條件
		String all_limit = " OFFSET " + offset + " LIMIT " + page_total;
		List<Integer> group_limit_in = new ArrayList<Integer>();

		List<BomProductEntity> p_list = new ArrayList<BomProductEntity>();
		ArrayList<BomGroupEntity> g_list = new ArrayList<BomGroupEntity>();
		List<BomTypeItemEntity> i_list = new ArrayList<BomTypeItemEntity>();
		List<BomTypeItemEntity> i_listAcc = new ArrayList<BomTypeItemEntity>();
		int select_nb = 0;// 查詢條件
		JSONObject packs = new JSONObject();// 自動化 填入 生產注意事項 參數
		// ----------項目----------
		if (entitys.size() > 0 && (entitys.get(0).getBom_number() != null || //
				entitys.get(0).getProduct_model() != null || //
				entitys.get(0).getBom_type() != null || //
				entitys.get(0).getGroupEntity().getType_item_group_id() != null)) {

			for (BomProductEntity entity : entitys) {
				// 組
				if (entity.getGroupEntity().getType_item_group_id() != null && entity.getGroupEntity().getType_item_group_id() != 0) {
					select_nb++;
					all_where_group += "(";
					type_item_group_id = " type_item_group_id = " + entity.getGroupEntity().getType_item_group_id();
					all_where_group += type_item_group_id + " AND ";
				} else {
					continue;
				}
				// 子
				if (entity.getGroupEntity().getType_item_id() != null) {
					type_item_id = " type_item_id = " + entity.getGroupEntity().getType_item_id();
					all_where_group += type_item_id + ") OR ";
				} else {
					all_where_group += "type_item_id !=0 ) OR ";
				}
			}
			// ----------產品----------
			for (BomProductEntity entity : entitys) {
				// 查詢名稱 條件不能與 項目衝突
				if (entity.getProduct_model() != null && !entity.getProduct_model().equals("")) {
					product_model = " product_model LIKE '%" + entity.getProduct_model() + "%' ";
					all_where_product += "(" + product_model + ") AND ";
				}
				if (entity.getBom_number() != null && !entity.getBom_number().equals("")) {
					product_bom_nb = " bom_number LIKE '%" + entity.getBom_number() + "%' ";
					all_where_product += "(" + product_bom_nb + ") AND ";
				}

			}

			// 如果(項目有值) / 如果只有(產品有值)
			if (!all_where_group.equals(" ")) {
				// 除對多餘的 OR
				all_where_group = all_where_group.substring(0, all_where_group.length() - 3);
				// 取得傭有條件的 群組 項目
				if (entitys.get(0).getBom_type().equals("product")) {
					g_list = productDao.queryGroup(all_where_group, null);
				} else {
					g_list = productAccDao.queryAccessoriesGroup(all_where_group, null);
				}

				// 過濾 重複 產品清單
				Map<Integer, Integer> g_m_list = new HashMap<Integer, Integer>();
				if (entitys.size() >= 0) {
					for (BomGroupEntity bomG : g_list) {
						if (g_m_list.containsKey(bomG.getProduct_id())) {
							g_m_list.put(bomG.getProduct_id(), g_m_list.get(bomG.getProduct_id()) + 1);
						} else {
							g_m_list.put(bomG.getProduct_id(), 1);
						}
					}
				}
				// 取得產品條件後 再次取得資料 項目-群組
				if (entitys.size() > 0) {
					all_where_group = " product_id in(";
					for (Entry<Integer, Integer> item : g_m_list.entrySet()) {
						if (select_nb <= item.getValue()) {
							all_where_group += item.getKey() + ",";
						}
					}
					all_where_group += "0)";
					if (entitys.size() == 0 || entitys.get(0).getBom_type().equals("product")) {
						g_list = productDao.queryGroup(all_where_group, null);
					} else {
						g_list = productAccDao.queryAccessoriesGroup(all_where_group, null);
					}
				}

				// ID 條件
				all_where_product += "product_model !='' AND useful = 1 ";
				all_where_product += " AND id in(";
				for (Entry<Integer, Integer> item : g_m_list.entrySet()) {
					if (select_nb <= item.getValue()) {
						all_where_product += item.getKey() + ",";
					}
				}
				all_where_product += "0)";
				// 產品-清單
				if (entitys.size() == 0 || entitys.get(0).getBom_type().equals("product")) {
					p_list = productDao.queryProduct(all_where_product, all_limit);
				} else {
					p_list = productAccDao.queryAccessoriesProduct(all_where_product, all_limit);
				}

			} else {
				all_where_group += "type_item_id !=0";
				// ID 條件
				all_where_product += "product_model !='' AND useful = 1 ";// 只有 (1可使用) 能查
				// 產品-清單
				if (entitys.size() == 0 || entitys.get(0).getBom_type().equals("product")) {
					p_list = productDao.queryProduct(all_where_product, all_limit);
				} else {
					p_list = productAccDao.queryAccessoriesProduct(all_where_product, all_limit);
				}

				for (BomProductEntity one : p_list) {
					group_limit_in.add(one.getId());
				}
				// 取得傭有條件的 群組 項目+產品
				if (entitys.size() == 0 || entitys.get(0).getBom_type().equals("product")) {
					g_list = productDao.queryGroup(all_where_group, group_limit_in);
				} else {
					g_list = productAccDao.queryAccessoriesGroup(all_where_group, group_limit_in);
				}
			}
			// ----------自動化 ERP 製令單--------
			if (entitys.get(0).getOrderId() != null) {
				ERP_MoctaEntity erp_MoctaEntity = new ERP_MoctaEntity();
				List<ERP_MoctaEntity> entities = new ArrayList<ERP_MoctaEntity>();

				// packagePC :ERP->物料-品名-關鍵字:Power cord & 數量0以上
				erp_MoctaEntity.setTA001_TA002(entitys.get(0).getOrderId());
				erp_MoctaEntity.setMB002("POWER CORD");
				entities = moctaDao.queryErpMocta(erp_MoctaEntity);
				if (entities.size() > 0) {
					packs.put("packagePC", entities.get(0).getMB002());
				}

				// packageL :ERP->物料-料號-關鍵字:50-100- & 數量0以上
				entities = new ArrayList<ERP_MoctaEntity>();
				erp_MoctaEntity = new ERP_MoctaEntity();
				erp_MoctaEntity.setTA001_TA002(entitys.get(0).getOrderId());
				erp_MoctaEntity.setMB001("50-100-");
				entities = moctaDao.queryErpMocta(erp_MoctaEntity);
				if (entities.size() > 0) {
					packs.put("packageL", entities.get(0).getMB002());
				}

				// packageBL :ERP->物料-品名-關鍵字:BOX label & 數量0以上
				entities = new ArrayList<ERP_MoctaEntity>();
				erp_MoctaEntity = new ERP_MoctaEntity();
				erp_MoctaEntity.setTA001_TA002(entitys.get(0).getOrderId());
				erp_MoctaEntity.setMB002("BOX label");
				entities = moctaDao.queryErpMocta(erp_MoctaEntity);
				if (entities.size() > 0) {
					packs.put("packageBL", entities.get(0).getMB002());
				}

				// packageBP :ERP->物料-關鍵字:BULK CARTON & 數量0以上
				entities = new ArrayList<ERP_MoctaEntity>();
				erp_MoctaEntity = new ERP_MoctaEntity();
				erp_MoctaEntity.setTA001_TA002(entitys.get(0).getOrderId());
				erp_MoctaEntity.setMB002("BULK CARTON");
				entities = moctaDao.queryErpMocta(erp_MoctaEntity);
				if (entities.size() > 0) {
					packs.put("packageBP", entities.get(0).getMB002());
				} else {
					// packageRP :ERP->物料-關鍵字:與上方相反
					packs.put("packageRP", "true");
				}
				// noteMoc :ERP->物料-關鍵字:訂單生產加工組裝包裝備註+生產備註
				entities = new ArrayList<ERP_MoctaEntity>();
				erp_MoctaEntity = new ERP_MoctaEntity();
				erp_MoctaEntity.setTA001_TA002(entitys.get(0).getOrderId());
				entities = moctaDao.queryErpMocta(erp_MoctaEntity);
				if (entities.size() > 0) {
					packs.put("noteMoc", entities.get(0).getTA050() + "\n" + entities.get(0).getTA054());
				}
				// 製令單
				packs.put("orderId", entitys.get(0).getOrderId());
			}

		} else {
			// 無條件
			// 產品-清單
			if (entitys.size() == 0 || entitys.get(0).getBom_type().equals("product")) {
				p_list = productDao.queryProduct("product_model !='' ", all_limit);
			} else {
				p_list = productAccDao.queryAccessoriesProduct("product_model !='' ", all_limit);
			}

			// 取得傭有條件的 群組 項目 (限制)
			for (BomProductEntity one : p_list) {
				group_limit_in.add(one.getId());
			}

			if (entitys.size() == 0 || entitys.get(0).getBom_type().equals("product")) {
				g_list = productDao.queryGroup("type_item_id !=0", group_limit_in);
			} else {
				g_list = productAccDao.queryAccessoriesGroup("type_item_id !=0", group_limit_in);
			}

		}

		// 項目-清單
		i_list = itemDao.queryAll("!= ' '");
		i_listAcc = itemAccDao.queryAll("!= ' '");
		// 產品
		bpg.setBomProductEntities(p_list);
		// 規格
		bpg.setBomGroupEntities(g_list);
		// 自動化 生產注意事項
		bpg.setTempAutoBomPrint(packs);

		bpg.setBomTypeItemEntities(i_list);
		bpg.setBomAccessoriesTypeItemEntities(i_listAcc);
		return bpg;
	}

	/** 檢查過 第一次列印 **/
	public void checked(int id, String name) {
		productDao.checkedOne(id, name);

	}

	/** 修正 BOM 生產單類型 **/
	public void productkind(int id, int kind) {
		productDao.updateProductKind(id, kind);

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
	public List<BomProductEntity> jsonToEntities(JSONObject content, String action) {
		List<BomProductEntity> entitys = new ArrayList<BomProductEntity>();

		// 查詢?
		if (!content.isNull("search_list") && !content.get("search_list").equals("")) {
			JSONArray search = content.getJSONArray("search_list");
			for (Object object : search) {
				BomProductEntity p_entity = new BomProductEntity();
				BomGroupEntity g_entity = new BomGroupEntity();
				g_entity.setSys_create_date(new Date());
				g_entity.setSys_create_user(loginService.getSessionUserBean().getAccount());
				g_entity.setSys_modify_date(new Date());
				g_entity.setSys_modify_user(loginService.getSessionUserBean().getAccount());
				JSONObject one = new JSONObject(object.toString());
				// 產品?
				if (!one.isNull("p_model") && !one.get("p_model").equals(""))
					p_entity.setProduct_model(one.getString("p_model"));
				// 項目組?
				if (!one.isNull("g_item") && !one.get("g_item").equals(""))
					g_entity.setType_item_group_id(one.getInt("g_item"));
				// 項目?->複數 站存在note內
				if (!one.isNull("i_item") && !one.get("i_item").equals(""))
					g_entity.setType_item_id(one.getInt("i_item"));
				// 項目?->BOM料號
				if (!one.isNull("p_bom_number") && !one.get("p_bom_number").equals(""))
					p_entity.setBom_number(one.getString("p_bom_number"));
				// 類型?
				if (!one.isNull("bom_type") && !one.get("bom_type").equals(""))
					p_entity.setBom_type(one.getString("bom_type"));
				// 工單號?
				if (!one.isNull("order_id") && !one.get("order_id").equals(""))
					p_entity.setOrderId(one.getString("order_id"));

				p_entity.setGroupEntity(g_entity);
				entitys.add(p_entity);
			}
		}

		return entitys;
	}

	/** 清單 to JSON **/
	public JSONObject entitiesToJson(BomProductGroupBean bpg) {
		JSONObject list = new JSONObject();
		JSONArray jsonAll = new JSONArray();
		JSONArray item_listAll = new JSONArray();
		JSONArray groupList = new JSONArray();
		JSONArray groupOne = new JSONArray();
		JSONArray item_acc_listAll = new JSONArray();
		// 標題
		JSONArray jsonArray = new JSONArray();

		jsonArray.put("產品ID");
		jsonArray.put("產品型號");
		jsonArray.put("主機板(硬體)-版本");

		jsonArray.put("對應BOM表 單號");
		jsonArray.put("使用狀態");
		jsonArray.put("備註");
		jsonArray.put("上次建單人");
		jsonArray.put("類型");
		jsonArray.put("建立時間");
		jsonArray.put("建立者");
		jsonArray.put("修改時間");
		jsonArray.put("修改者");
		jsonArray.put("類型");

		jsonAll.put(jsonArray);
		// 內容 產品清單
		for (BomProductEntity entity : bpg.getBomProductEntities()) {
			jsonArray = new JSONArray();
			jsonArray.put(entity.getId());
			jsonArray.put(entity.getProduct_model());
			jsonArray.put(entity.getVersion_motherboard());

			jsonArray.put(entity.getBom_number());
			jsonArray.put(entity.getUseful());
			jsonArray.put(entity.getNote());
			jsonArray.put(entity.getChecked());
			jsonArray.put(entity.getKind());

			jsonArray.put(Fm_Time_Model.to_yMd_Hms(entity.getSys_create_date()));
			jsonArray.put(entity.getSys_create_user());
			jsonArray.put(Fm_Time_Model.to_yMd_Hms(entity.getSys_modify_date()));
			jsonArray.put(entity.getSys_modify_user());
			jsonArray.put(entity.getBom_type());
			jsonAll.put(jsonArray);
		}
		list.put("list", jsonAll);
		// ======== 細項 群組清單========
		boolean checkf = false;
		for (BomGroupEntity entity : bpg.getBomGroupEntities()) {
			// 第一次不算
			if (entity.getType_item_group_id() == 1 && checkf) {
				groupList.put(groupOne);
			}
			jsonArray = new JSONArray();
			jsonArray.put(Fm_Time_Model.to_yMd_Hms(entity.getSys_create_date()));
			jsonArray.put(entity.getSys_create_user());
			jsonArray.put(Fm_Time_Model.to_yMd_Hms(entity.getSys_modify_date()));
			jsonArray.put(entity.getSys_modify_user());

			jsonArray.put(entity.getId());
			jsonArray.put(entity.getProduct_id());
			jsonArray.put(entity.getType_item_id());
			jsonArray.put(entity.getType_item_group_id());
			jsonArray.put(entity.getNumber());
			jsonArray.put(entity.getType_order());
			jsonArray.put(entity.getUseful());

			jsonArray.put(entity.getGroup_name());
			for (int i = 1; i <= 25; i++) {
				Method method;
				try {
					method = entity.getClass().getMethod("getI" + String.format("%02d", i));
					String value = (String) method.invoke(entity);
					if (value == null || value.equals("")) {
						continue;
					}
					jsonArray.put(value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			groupOne.put(jsonArray);
		}
		checkf = true;
		list.put("group_list", groupOne);
		// 項目清單
		// 項目資料+標題
		for (BomTypeItemEntity entity : bpg.getBomTypeItemEntities()) {
			jsonArray = new JSONArray();
			jsonArray.put(entity.getGroup_id());
			jsonArray.put(entity.getGroup_name());
			jsonArray.put(entity.getId());
			jsonArray.put(entity.getType_order());

			for (int i = 1; i <= 25; i++) {
				Method method;
				try {
					method = entity.getClass().getMethod("getI" + String.format("%02d", i));
					String value = (String) method.invoke(entity);
					if (value == null || value.equals("")) {
						continue;
					}
					jsonArray.put(value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			jsonArray.put(entity.getNote());
			item_listAll.put(jsonArray);
		}
		list.put("item_list", item_listAll);

		// ACC 項目清單
		// ACC 項目資料+標題
		for (BomTypeItemEntity entity : bpg.getBomAccessoriesTypeItemEntities()) {
			jsonArray = new JSONArray();
			jsonArray.put(entity.getGroup_id());
			jsonArray.put(entity.getGroup_name());
			jsonArray.put(entity.getId());
			jsonArray.put(entity.getType_order());

			for (int i = 1; i <= 25; i++) {
				Method method;
				try {
					method = entity.getClass().getMethod("getI" + String.format("%02d", i));
					String value = (String) method.invoke(entity);
					if (value == null || value.equals("")) {
						continue;
					}
					jsonArray.put(value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			jsonArray.put(entity.getNote());
			item_acc_listAll.put(jsonArray);
		}
		list.put("item_acc_list", item_acc_listAll);
		// 自動化-填入?
		list.put("tempAutoBomPrint", bpg.getTempAutoBomPrint());
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

		templateBean.setWebPageBody("html/body/bom_print_body.html");
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
