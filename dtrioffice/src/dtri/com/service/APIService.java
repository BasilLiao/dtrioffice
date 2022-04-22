package dtri.com.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dtri.com.bean.BomProductGroupBean;
import dtri.com.bean.MesApiBean;
import dtri.com.db.entity.BomGroupEntity;
import dtri.com.db.entity.ProductionRecordsEntity;
import dtri.com.db.entity.SoftwareVersionEntity;

/**
 * 串接服務 MES
 * 
 **/
@Transactional
@Service
public class APIService {
	private static boolean check_connect = true;

	public JSONObject mes_WorkstationProgramList() {
		JSONObject mes_obj = new JSONObject();
		BasicCookieStore cookieStore = new BasicCookieStore();
		SSLContextBuilder builder = new SSLContextBuilder();
		SSLConnectionSocketFactory sslConnectionFactory;
		JSONObject content = new JSONObject();
		// 連線失敗不再連線
		if (!check_connect) {
			return null;
		}
		try {// 使用HTTPS 加密連線
			mes_obj.put("action", "get_work_program");
			builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
			sslConnectionFactory = new SSLConnectionSocketFactory(builder.build());
			CloseableHttpClient httpclient = HttpClients.custom()//
					.setSSLSocketFactory(sslConnectionFactory)//
					.setDefaultCookieStore(cookieStore)//
					.build();
			HttpPost request = new HttpPost("https://localhost:8088/dtrimes/ajax/api.basil");
			StringEntity params = new StringEntity(mes_obj.toString(), "UTF-8");
			params.setContentEncoding("UTF-8");
			request.addHeader("Content-Type", "application/json;charset=UTF-8");
			request.setEntity(params);
			HttpResponse response = httpclient.execute(request);
			HttpEntity entity2 = response.getEntity();
			content = new JSONObject(EntityUtils.toString(entity2));

			System.out.println(content);

		} catch (Exception e) {
			System.out.println(e);
			check_connect = false;
		}

		return content;
	}

	// 轉換資料
	public MesApiBean mes_setting_bean(JSONObject mes) {
		// 非資料庫資料(MES 專用)
		MesApiBean mesApi = new MesApiBean();
		mesApi.setPh_p_number(mes.has("ph_p_number") ? mes.getString("ph_p_number") : "");// 產品 認證/號/名稱
		mesApi.setPh_wpro_id(mes.has("ph_wpro_id") ? mes.getString("ph_wpro_id") : "");// 工作程序
		mesApi.setPr_wline_id(mes.has("pr_wline_id") ? mes.getString("pr_wline_id") : "");// 產線線別
		mesApi.setPr_type(mes.has("pr_type") ? mes.getString("pr_type") : "");// 製令單類型
		mesApi.setPr_w_years(mes.has("pr_w_years") ? mes.getString("pr_w_years") : "");// 保固年分
		mesApi.setPs_b_f_sn(mes.has("ps_sn_burn_fixed") ? mes.getString("ps_sn_burn_fixed") : "");// 特殊SN 固定
		mesApi.setPh_p_name(mes.has("ph_p_name") ? mes.getString("ph_p_name") : "");// 產品編號名稱
		mesApi.setPs_b_sn(mes.has("ps_sn_burn_nb") ? mes.getString("ps_sn_burn_nb") : "");// 特殊SN 序列
		return mesApi;
	}

	/**
	 * 
	 * @param sys_sn 系統自訂號?
	 **/
	public boolean mes_production_create(ProductionRecordsEntity entity, SoftwareVersionEntity software, JSONObject sn_obj, BomProductGroupBean bomProGro,
			MesApiBean mesApi, boolean sys_sn) {

		// API 串接至 MES (暫時寫死)
		// HttpClient httpClient = HttpClientBuilder.create().build();
		BasicCookieStore cookieStore = new BasicCookieStore();
		SSLContextBuilder builder = new SSLContextBuilder();
		SSLConnectionSocketFactory sslConnectionFactory;
		// 連線失敗不再連線
		if (!check_connect) {
			return false;
		}
		try {// 使用HTTPS 加密連線
			JSONObject mes_obj = new JSONObject();
			JSONObject production_records = new JSONObject();// 規格+製令單

			// 規格+製令單
			production_records.put("pr_c_name", entity.getClient_name());
			production_records.put("sys_note", entity.getNote());
			production_records.put("ph_type", mesApi.getPr_type());// 製令單類型
			production_records.put("ph_pb_g_id", "");
			production_records.put("sys_m_user", "");
			production_records.put("ph_schedule", "");
			production_records.put("ph_wp_id", mesApi.getPh_wpro_id());// 工作站-程序
			production_records.put("pr_wl_id", mesApi.getPr_wline_id());// 產線-程序
			production_records.put("ph_p_number", mesApi.getPh_p_number());// 產品 認證/編號
			production_records.put("ph_p_name", mesApi.getPh_p_name());// 產品 認證/編號
			production_records.put("sys_c_user", "");
			production_records.put("ph_id", "");
			production_records.put("sys_ver", "0");
			production_records.put("sys_status", "0");
			production_records.put("ph_s_date", "");
			production_records.put("sys_sn_auto", sys_sn);// 是否 是系統生產

			production_records.put("pr_bom_id", entity.getBom_product_id());
			production_records.put("pr_p_model", entity.getProduct_model());

			String psl = entity.getProduct_start_sn();
			production_records.put("ps_sn_1", sys_sn ? psl.substring(0, 3) : "000");
			production_records.put("ps_sn_2", sys_sn ? psl.substring(3, 4) : "0");
			production_records.put("ps_sn_3", sys_sn ? psl.substring(4, 5) : "0");
			production_records.put("ps_sn_4", sys_sn ? psl.substring(5, 9) : "0000");
			production_records.put("ps_sn_5", sys_sn ? psl.substring(9, 10) : "0");
			production_records.put("ps_sn_6", sys_sn ? psl.substring(10, 13) : "000");

			production_records.put("ps_b_f_sn", mesApi.getPs_b_f_sn());// 客製化 & 燒入流水號
			production_records.put("ps_b_sn", mesApi.getPs_b_sn());// 客製化 & 燒入流水號

			JSONObject prbitem = new JSONObject();
			for (int bg = 0; bg < bomProGro.getBomGroupEntities().size(); bg++) {
				BomGroupEntity bge_one = bomProGro.getBomGroupEntities().get(bg);
				// System.out.println(bge_one);
				String value1 = bge_one.getI01() == null ? "" : bge_one.getI01();
				String value2 = bge_one.getI02() == null ? "" : bge_one.getI02();
				String value3 = bge_one.getI03() == null ? "" : bge_one.getI03();
				String value4 = bge_one.getI04() == null ? "" : bge_one.getI04();
				prbitem.put(bge_one.getGroup_name(), new JSONObject()//
						.put("Is", value1 + value2 + value3 + value4)//
						.put("Qty", bge_one.getNumber()));//

			}
			production_records.put("pr_b_item", prbitem);// 規格
			production_records.put("pr_p_quantity", entity.getProduction_quantity());
			production_records.put("pr_s_item", new JSONObject()// 軟體定義
					.put("OS ", new JSONObject().put("Is", software.getOs()))//
					.put("BIOS ", new JSONObject().put("Is", software.getBios()))//
					.put("M/B_Ver", new JSONObject().put("Is", software.getMb_ver()))//
					.put("Model_In", new JSONObject().put("Is", software.getProduct_model_in()))//
					.put("NV_RAM", new JSONObject().put("Is", software.getNvram()))//
					.put("USER_note1", new JSONObject().put("Is", software.getNote1()))//
					.put("PM_Note", new JSONObject().put("Is", software.getNote()))//
					.put("USER_Note2", new JSONObject().put("Is", software.getNote2()))//
					.put("M/B_Ver_ECN", new JSONObject().put("Is", software.getMb_ver_ecn())));

			production_records.put("pr_c_from", "DTR ERP");
			production_records.put("sys_m_date", "");

			production_records.put("pr_order_id", entity.getOrder_id());
			production_records.put("pr_w_years", mesApi.getPr_w_years());// 保固年分
			production_records.put("ph_pr_id", entity.getId());

			production_records.put("pr_s_sn", entity.getProduct_start_sn());
			production_records.put("sys_sort", "0");
			production_records.put("sys_c_date", "");
			production_records.put("pr_e_sn", entity.getProduct_end_sn());// 結束
			production_records.put("pr_p_ok_quantity", "0");

			if (sn_obj.length() > 0 && sn_obj.has("sn_list")) {
				production_records.put("sn_list", sn_obj.getJSONArray("sn_list"));
			} else if (sn_obj.length() > 0 && sn_obj.has("sn_list_burn")) {
				production_records.put("sn_list", sn_obj.getJSONArray("sn_list_burn"));
			} else {
				production_records.put("sn_list", new JSONArray());
			}

			mes_obj.put("action", "production_create");
			mes_obj.put("create", new JSONArray().put(production_records));

			builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
			sslConnectionFactory = new SSLConnectionSocketFactory(builder.build());
			CloseableHttpClient httpclient = HttpClients.custom()//
					.setSSLSocketFactory(sslConnectionFactory)//
					.setDefaultCookieStore(cookieStore)//
					.build();
			HttpPost request = new HttpPost("https://localhost:8088/dtrimes/ajax/api.basil");
			StringEntity params = new StringEntity(mes_obj.toString(), "UTF-8");
			params.setContentEncoding("UTF-8");
			request.addHeader("Content-Type", "application/json;charset=UTF-8");

			request.setEntity(params);
			HttpResponse response = httpclient.execute(request);
			HttpEntity entity2 = response.getEntity();
			String content = EntityUtils.toString(entity2);
			System.out.println(content);

		} catch (Exception ex) {
			System.out.println(ex);
			check_connect = false;
			return false;
		} finally {

		}
		return true;
	}

}
