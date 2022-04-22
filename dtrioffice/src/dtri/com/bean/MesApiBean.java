package dtri.com.bean;

/**
 * 
 * 串接用API MES
 * 
 **/
public class MesApiBean {
	private String pr_type;// 製令單類型
	private String pr_w_years;// 保固年分
	private String ph_wpro_id; // 工作程序
	private String pr_wline_id; // 產線線別

	private String ps_b_f_sn; // 特殊SN 固定
	private String ps_b_sn; // 特殊SN 序列
	private String ph_p_number; // 認證編號
	private String ph_p_name;//產品編號/名稱

	public String getPr_type() {
		return pr_type;
	}

	public void setPr_type(String pr_type) {
		this.pr_type = pr_type;
	}

	public String getPr_w_years() {
		return pr_w_years;
	}

	public void setPr_w_years(String pr_w_years) {
		this.pr_w_years = pr_w_years;
	}

	public String getPh_wpro_id() {
		return ph_wpro_id;
	}

	public void setPh_wpro_id(String ph_wpro_id) {
		this.ph_wpro_id = ph_wpro_id;
	}

	public String getPs_b_f_sn() {
		return ps_b_f_sn;
	}

	public void setPs_b_f_sn(String ps_b_f_sn) {
		this.ps_b_f_sn = ps_b_f_sn;
	}

	public String getPs_b_sn() {
		return ps_b_sn;
	}

	public void setPs_b_sn(String ps_b_sn) {
		this.ps_b_sn = ps_b_sn;
	}

	public String getPh_p_number() {
		return ph_p_number;
	}

	public void setPh_p_number(String ph_p_number) {
		this.ph_p_number = ph_p_number;
	}

	public String getPh_p_name() {
		return ph_p_name;
	}

	public void setPh_p_name(String ph_p_name) {
		this.ph_p_name = ph_p_name;
	}

	public String getPr_wline_id() {
		return pr_wline_id;
	}

	public void setPr_wline_id(String pr_wline_id) {
		this.pr_wline_id = pr_wline_id;
	}

}
