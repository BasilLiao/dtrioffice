package dtri.com.db.mssql.dao;

import java.util.List;

import dtri.com.db.entity.ERP_PM_Entity;

public interface ERP_ProductionManagementDao {

	/**
	 * 同步使用<br>
	 * 取得有效工單
	 * 
	 * @param
	 **/
	List<ERP_PM_Entity> getERP_PM_List();

	/**
	 * 同步使用<br>
	 * 取得結束工單
	 * 
	 * @param
	 **/
	ERP_PM_Entity getERP_PM_End_List(ERP_PM_Entity entity);

}