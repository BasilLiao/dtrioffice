package dtri.com.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dtri.com.db.entity.ERP_PURTC_PURTD_Entity;
import dtri.com.db.mssql.dao.ERP_PurchasingListDao;

@Transactional
@Service
public class ERP_PurchasingListService {
	/**
	 * 取得
	 * 
	 * @return 查詢後清單
	 * 
	 **/
	@Autowired
	ERP_PurchasingListDao listDao;
	
	public List<ERP_PURTC_PURTD_Entity> searchERP_PurchasingList(ERP_PURTC_PURTD_Entity event) {
		List<ERP_PURTC_PURTD_Entity> entities= new ArrayList<ERP_PURTC_PURTD_Entity>(); 
		entities = listDao.queryERP_PurchasingList(event);
	return entities;
	}

}
