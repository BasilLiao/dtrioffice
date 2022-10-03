package dtri.com.db.mssql.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import dtri.com.db.entity.ERP_BOMMD_Entity;
import dtri.com.db.entity.ERP_INVMB_Entity;
import dtri.com.db.entity.ERP_INVMC_Entity;
import dtri.com.db.entity.ERP_MOC_PUR_Entity;
import dtri.com.db.entity.ERP_PM_Entity;

public interface ERP_ProductionManagementDao {

	/**
	 * 同步使用<br>
	 * 取得有效工單
	 * 
	 * @param
	 **/
	List<ERP_PM_Entity> getERP_PM_List();

}