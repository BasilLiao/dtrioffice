package dtri.com.db.pgsql.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import dtri.com.db.entity.BomProductEntity;
import dtri.com.db.entity.ERP_BOMMD_Entity;
import dtri.com.db.entity.ERP_INVMB_Entity;
import dtri.com.db.entity.ERP_INVMC_Entity;
import dtri.com.db.entity.ERP_MOC_PUR_Entity;
import dtri.com.db.entity.ERP_PM_Entity;

public interface ProductionManagementDao {

	/**
	 * 同步使用<br>
	 * 取得有效工單
	 * 
	 * @param
	 **/
	List<ERP_PM_Entity> searchERP_PM_List(ERP_PM_Entity entity);

	
	/**
	 * 更新 工單紀錄
	 **/
	Integer updateOneFromERP(ERP_PM_Entity entity);
	/**
	 * 添加 工單紀錄
	 **/
	Integer addedOne(ERP_PM_Entity entity);

	/**
	 * 更新 工單紀錄
	 **/
	Integer updateOne(ERP_PM_Entity entity);
	/**
	 * 標記 工單紀錄 暫時登(3)
	 **/
	Integer updateToUseful3From1();
	
	/**
	 * 標記 工單紀錄 廢止(已完結)
	 **/
	Integer updateToUseful2From3();
	

}